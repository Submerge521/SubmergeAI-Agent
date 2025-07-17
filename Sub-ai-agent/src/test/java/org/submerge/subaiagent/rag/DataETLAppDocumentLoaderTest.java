package org.submerge.subaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.submerge.subaiagent.app.DataETLAPP;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: DataETLAppDocumentLoaderTest
 * Package: org.submerge.subaiagent.rag
 * Description:
 *
 * @Author Submerge--WangDong
 * @Create 2025/7/16 22:31
 * @Version 1.0
 */

@SpringBootTest
class DataETLAppDocumentLoaderTest {

    @Resource
    private DataETLAPP dataETLAPP;

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我刚开始入门大数据，如何开始学习呢？";
        String content = dataETLAPP.doChatWithRag(message, chatId);
        Assertions.assertNotNull(content);
    }

    @Test
    void doChatWithCloudRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我刚开始入门大数据，如何开始学习呢？";
        String content = dataETLAPP.doChatWithCloudRag(message, chatId);
        Assertions.assertNotNull(content);
    }

}