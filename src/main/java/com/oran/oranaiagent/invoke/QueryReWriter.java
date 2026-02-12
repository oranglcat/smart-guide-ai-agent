package com.oran.oranaiagent.invoke;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

@Component
public class QueryReWriter {

    private RewriteQueryTransformer rewriteQueryTransformer;

    public QueryReWriter(ChatModel dashscopeChatModel){

        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);

        rewriteQueryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
    }

    public String doReWrite(String prompt){
        Query query = new Query(prompt);
        Query transformed = rewriteQueryTransformer.transform(query);
        return transformed.text();
    }
}
