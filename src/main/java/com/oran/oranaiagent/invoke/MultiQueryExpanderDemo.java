package com.oran.oranaiagent.invoke;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MultiQueryExpanderDemo {

    private final ChatClient.Builder chatClientBuilder;

    public MultiQueryExpanderDemo(ChatModel dashScopeChatModel){
        this.chatClientBuilder = ChatClient.builder(dashScopeChatModel);
    }

   public List<Query> QueryExpander(String s){
       MultiQueryExpander queryExpander = MultiQueryExpander.builder()
               .chatClientBuilder(chatClientBuilder)
               .numberOfQueries(3)
               .build();
       List<Query> queries = queryExpander.expand(new Query(s));
       return queries;
   }
}
