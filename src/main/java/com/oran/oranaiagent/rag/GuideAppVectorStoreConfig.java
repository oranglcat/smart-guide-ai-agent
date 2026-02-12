package com.oran.oranaiagent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class GuideAppVectorStoreConfig {

    @Resource
    private GuideAppDocumentLoader guideAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter tokenTextSplitter;

    @Resource
    private MyKeyWordRicher keyWordRicher;

    @Bean
    VectorStore GuideAppVectorStore(EmbeddingModel dashscopeEmbeddingModel){
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        List<Document> documentList = guideAppDocumentLoader.LoaderAllMarkDown();
        //文档切分器
        //  List<Document> spiltDocuments = tokenTextSplitter.splitCustomized(documentList);
        //自动补充关键词元信息
       // List<Document> richerDocuments = keyWordRicher.keyWordRicher(documentList);
        vectorStore.add(documentList);
        return vectorStore;
    }

}
