package org.submerge.subaiagent.rag;


import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * ClassName: PgVectorVectorStoreConfigTest
 * Package: org.submerge.subaiagent.rag
 * Description:
 *
 * @Author Submerge--WangDong
 * @Create 2025/7/19 10:44
 * @Version 1.0
 */
@SpringBootTest
class PgVectorVectorStoreConfigTest {

    @Resource
    private VectorStore pgVectorVectorStore;

    @Test
    void pgVectorVectorStore() {
        List<Document> documents = List.of(
                new Document("数据仓库和数据库是不一样的，数据仓库主要是面向主题的、集成的、能够支持决策的数据集合", Map.of("meta1", "meta1")),
                new Document("数据开发需要学习SQL、Java、大数据组件如Hadoop等"),
                new Document("我真帅啊", Map.of("meta2", "meta2")));

        // Add the documents to PGVector
        pgVectorVectorStore.add(documents);

        // Retrieve documents similar to a query
        List<Document> results = this.pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("如何区分数据库和数据仓库").topK(3).build());
        Assertions.assertNotNull(results);
    }
}