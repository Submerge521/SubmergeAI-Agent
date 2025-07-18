package org.submerge.subaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataETLAppVectorStoreConfig {

    @Resource
    private DataETLAppDocumentLoader loveAppDocumentLoader;
    
    @Bean
    VectorStore dataEtlAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        // 创建向量存储（内置的基于内存的向量存储）
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 加载文档
        List<Document> documents = loveAppDocumentLoader.LoadDataEtlAppDocuments();
        simpleVectorStore.add(documents);
        return simpleVectorStore;
    }
}
