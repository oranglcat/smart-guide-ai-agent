package com.oran.oranaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

public class GuideAppContextualQueryAugmentFactory {

    public static ContextualQueryAugmenter createInstance(){
        PromptTemplate promptTemplate = new PromptTemplate("你应该输出下面的内容：\n" +
                "抱歉，我只能回答旅游相关的问题，别的没办法帮到您哦，\n" +
                "有问题可以联系我 2496503479@qq.com");

        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(promptTemplate)
                .build();
    }

}
