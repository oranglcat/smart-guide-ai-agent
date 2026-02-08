package com.oran.oranaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.lang.ref.PhantomReference;
import java.util.List;

@Component
public class MyKeyWordRicher {

    @Resource
    private ChatModel dashscopeChatModel;

    public List<Document> keyWordRicher(List<Document> documents){
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashscopeChatModel,5);

        return keywordMetadataEnricher.apply(documents);
    }

}
