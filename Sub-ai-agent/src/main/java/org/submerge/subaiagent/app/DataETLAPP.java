package org.submerge.subaiagent.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;
import org.submerge.subaiagent.advisor.MyLoggerAdvisor;
import org.submerge.subaiagent.chatmemory.DatabaseChatMemory;
import org.submerge.subaiagent.chatmemory.FileBasedChatmemory;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * ClassName: DataETLAPP
 * Package: org.submerge.subaiagent.app
 * Description: 多轮AI对话程序demo
 *
 * @Author Submerge--WangDong
 * @Create 2025/7/12 09:29
 * @Version 1.0
 */
@Component
@Slf4j
public class DataETLAPP {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "拥有 10 年以上实战经验的资深数据 ETL 开发专家，" +
            "精通各类 ETL 工具与全流程技术，能解决性能优化、数据质量等难题；与用户对话时，" +
            "需通过多轮引导性提问挖掘项目背景（业务场景、数据规模等）、梳理技术现状（工具、现存问题等）、" +
            "确认需求细节并预判潜在需求，再提供针对性、可落地的建议，沟通中保持专业耐心，用通俗语言解释技术概念。";


    public DataETLAPP(ChatModel dashscopeChatModel) {

        String filedir = System.getProperty("user.dir") + "/tmp/data/chatmemory";
        FileBasedChatmemory fileBasedChatmemory = new FileBasedChatmemory(filedir);
//        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(fileBasedChatmemory)
                        // SpringAI内置的日志拦截器
//                        ,new SimpleLoggerAdvisor()
                        // 自定义日志拦截器，按需开启
//                        , new MyLoggerAdvisor()
                )
                .build();
    }
//    public DataETLAPP(ChatModel dashscopeChatModel, DatabaseChatMemory databaseChatMemory) {
//        chatClient = ChatClient.builder(dashscopeChatModel)
//                .defaultSystem(SYSTEM_PROMPT)
//                .defaultAdvisors(
//                        new MessageChatMemoryAdvisor(databaseChatMemory)
//                )
//                .build();
//    }


    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content:{}", content);
        return content;
    }

    record DataETLReport(String name, List<String> suggestions) {

    }

    /**
     * 生成报告
     *
     * @param message
     * @param chatId
     * @return
     */
    public DataETLReport doChatWithReport(String message, String chatId) {
        DataETLReport dataETLReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成一份标题以{用户名}+{主题}的相关报告，内容为具体的建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(DataETLReport.class);
        log.info("dataETLReport:{}", dataETLReport);
        return dataETLReport;
    }


}
