package org.submerge.subaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.submerge.subaiagent.entity.ChatMessage;

import java.util.Date;
import java.util.UUID;

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


}