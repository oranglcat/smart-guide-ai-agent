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
//@Configuration
public class GuideAppVectorStoreConfig {

    @Resource
    private GuideAppDocumentLoader guideAppDocumentLoader;

    //@Bean
    VectorStore GuideAppVectorStore(EmbeddingModel dashscopeEmbeddingModel){
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        List<Document> documentList = guideAppDocumentLoader.LoaderAllMarkDown();
        vectorStore.add(documentList);
        return vectorStore;
    }

}
