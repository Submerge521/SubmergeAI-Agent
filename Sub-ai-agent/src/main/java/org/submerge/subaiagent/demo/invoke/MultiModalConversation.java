package org.submerge.subaiagent.demo.invoke;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;

/**
 * 测试多模态对话
 */
public class MultiModalConversation {
    public static void simpleMultiModalConversationCall()
            throws ApiException, NoApiKeyException, UploadFileException {
        com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation conv = new com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation();
        MultiModalMessage systemMessage = MultiModalMessage.builder().role(Role.SYSTEM.getValue())
                .content(List.of(
                        Collections.singletonMap("text", "You are a helpful assistant."))).build();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", "https://inews.gtimg.com/om_bt/O95FiheDRqWdiXGP6LQ1WFuOfZ4onzYmFUW9EjEkLMf5gAA/641"),
                        Collections.singletonMap("text", "图中描绘的是什么景象?"))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                 // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(TestApiKey.API_KEY)
                .model("qwen-vl-max-latest")  // 此处以qwen-vl-max-latest为例，可按需更换模型名称。模型列表：https://help.aliyun.com/model-studio/getting-started/models
                .messages(Arrays.asList(systemMessage, userMessage))
                .build();
        MultiModalConversationResult result = conv.call(param);
        System.out.println(result.getOutput().getChoices().getFirst().getMessage().getContent().getFirst().get("text"));
    }
    public static void main(String[] args) {
        try {
            simpleMultiModalConversationCall();
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
}