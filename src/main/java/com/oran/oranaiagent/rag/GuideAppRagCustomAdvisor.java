package com.oran.oranaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;


/*
* 创建自定义检索增强顾问的工厂
* */
@Slf4j
public class GuideAppRagCustomAdvisor {

    public static Advisor createAdvisor(VectorStore vectorStore,String city){
        Filter.Expression expression = new FilterExpressionBuilder().eq("city",city).build();

        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression)
                .topK(3)
                .similarityThreshold(0.5)
                .build();

        ContextualQueryAugmenter queryAugmenter = GuideAppContextualQueryAugmentFactory.createInstance();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(queryAugmenter)
                .build();
    }
}
