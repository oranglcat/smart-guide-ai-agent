package com.oran.oranaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class dashScopeEmbeddingConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String API_KEY;

    @Bean
    EmbeddingModel dashScopeEmbeddingModel(){
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(API_KEY)
                .build();

        DashScopeEmbeddingModel dashScopeEmbeddingModel = new DashScopeEmbeddingModel(
                dashScopeApi,
                MetadataMode.EMBED,
                DashScopeEmbeddingOptions.builder()
                        .model(DashScopeModel.EmbeddingModel.EMBEDDING_V2.getValue())
                        .textType(DashScopeModel.EmbeddingTextType.DOCUMENT.getValue())
                        .build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE);

        return dashScopeEmbeddingModel;
    }
}
