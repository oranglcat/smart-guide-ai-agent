package com.oran.oranaiagent.invoke;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import dev.langchain4j.community.model.dashscope.QwenChatModel;

public class LangChainAiInvoke {

    public static void main(String[] args) {
        QwenChatModel model = QwenChatModel.builder()
                .apiKey(ApiKey.API_KEY)
                .modelName("qwen-plus")
                .build();

        String result = model.chat("你是谁");
        System.out.println(result);
    }

}
