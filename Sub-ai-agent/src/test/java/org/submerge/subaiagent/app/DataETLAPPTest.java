package org.submerge.subaiagent.app;

import cn.hutool.extra.template.TemplateEngine;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;
import org.submerge.subaiagent.entity.ChatMessage;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: DataETLAPPTest
 * Package: org.submerge.subaiagent.app
 * Description:
 *
 * @Author Submerge--WangDong
 * @Create 2025/7/12 10:03
 * @Version 1.0
 */
@SpringBootTest
class DataETLAPPTest {

    @Resource
    private DataETLAPP dataETLAPP;

    /**
     * 模板文件，这里是读取不到的，需要放在target文件目录下
     */
//    @Value("classpath:templates/data-modeling-specialized-template.st")
//    private ClassPathResource templateResource;
    @Test
    void testChat() {

        String chatId = UUID.randomUUID().toString();

        // 第一轮对话
        String message = "你好，我是数据开发小星";
        String answer = dataETLAPP.doChat(message, chatId);
        // 第二轮对话
        message = "不超过30字的一句话解释etl";
        answer = dataETLAPP.doChat(message, chatId);
        // 第三轮对话
        message = "我的名字叫什么？";
        answer = dataETLAPP.doChat(message, chatId);

    }

    @Test
    void testChatWithReport() {
        String chatId = UUID.randomUUID().toString();

        // 第一轮对话
        String message = "你好，我是数据开发小星，我想要让我的SQL水平一周达到面试和日常开发水平，但我不知道该怎么准备？准备到什么程度？";
        DataETLAPP.DataETLReport report = dataETLAPP.doChatWithReport(message, chatId);
        Assertions.assertNotNull(report);
    }

    @Test
    void testChat2() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮对话
//        String message = "你好，我是小星，我想要插你";
        String message = "你好，我是小星，我今天要开始学习数据开发了，请给我一个30字以内的简介";
        DataETLAPP.DataETLReport report = dataETLAPP.doChatWithReport(message, chatId);
        Assertions.assertNotNull(report);
    }

    /**
     * 测试提示词模板
     */
    @Test
    void testPromptTemplate() {
        String chatId = UUID.randomUUID().toString();
        ClassPathResource templateResource = new ClassPathResource("templates/data-modeling-specialized-template.st");

        Message systemMessage = getMessage(templateResource);

        // 4. 构建用户查询
        Message userMessage = new UserMessage("如何设计支持订单分析的维度模型？");

        // 5. 组合为完整Prompt
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String doChat = dataETLAPP.doChat(String.valueOf(userMessage), chatId);
        Assertions.assertNotNull(doChat);
    }

    private static Message getMessage(ClassPathResource templateResource) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(templateResource);
        // 2. 准备测试变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("who", "资深的数仓工程师");
        variables.put("what", "各种业务的维度建模，建设出可复用的数据模型，支持后续业务操作");
        variables.put("context", "用户的业务领域、数据规模、目标受众");
        variables.put("status", "当前使用的数据源、已有的数据表、存在的业务系统");
        variables.put("requirements", "期望的结果");
        // 生成系统消息
        Message systemMessage = systemPromptTemplate.createMessage(variables);
        // 构建用户消息
        Message userMessage = new UserMessage("{user_query}");
        // 组合为完整Prompt
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        // 3. 创建系统消息
        return systemPromptTemplate.createMessage(variables);
    }
}


