package org.submerge.subaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: DataETLAppDocumentLoader
 * Package: org.submerge.subaiagent.rag
 * Description: 数据ETL应用 RAG 知识库 读取
 *
 * @Author Submerge--WangDong
 * @Create 2025/7/16 22:01
 * @Version 1.0
 */
@Component
@Slf4j
public class DataETLAppDocumentLoader {

    // 负责解析资源模式的解析器，用于查找和加载资源
    private final ResourcePatternResolver resourcePatternResolver;

    /**
     * DataETLAppDocumentLoader类的构造函数
     * 初始化资源模式解析器
     *
     * @param resourcePatternResolver 资源模式解析器，用于查找和加载资源
     */
    public DataETLAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载数据ETL应用的相关文档
     * 该方法从类路径下的document目录中加载所有Markdown格式的文档，并将其转换为Document对象列表
     *
     * @return 包含所有加载的文档的列表
     */
    public List<Document> LoadDataEtlAppDocuments() {
        ArrayList<Document> allDocuments = new ArrayList<>();
        // 获取数据ETL应用相关的文档
        try {
            Resource[] documents = resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : documents) {
                // 获取文件名
                String fileName = resource.getFilename();
                // 配置Markdown文档阅读器，定制文档处理规则
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        // 水平线
                        .withHorizontalRuleCreateDocument(true)
                        // 代码块
                        .withIncludeCodeBlock(false)
                        // 引用块
                        .withIncludeBlockquote(false)
                        // 元信息
                        .withAdditionalMetadata("filename", fileName)
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error(" MarkDown 文件加载失败", e);
        }
        return allDocuments;
    }


}
