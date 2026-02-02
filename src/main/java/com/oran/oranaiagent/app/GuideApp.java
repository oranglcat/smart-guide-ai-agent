package com.oran.oranaiagent.app;

import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import com.oran.oranaiagent.advisor.MyLoggerAdvisor;
import com.oran.oranaiagent.advisor.ReReadingAdvisor;
import com.oran.oranaiagent.chatMemory.FileBasedChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.alibaba.dashscope.app.AppKeywords.TOP_K;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Component
@Slf4j
public class GuideApp {


    private final ChatClient client;

    private final String SYSTEM_TEXT = "你是一个旅游规划助手，专业提供旅游建议和规划服务。\n" +
            "你可以通过逐步提问，引导用户提供旅游地点和预算，根据用户输入的城市，推荐著名的旅游景点,根据用户的预算，调整旅游规划和建议。\n" +
            "在对话一开始，首先表明自己的身份，随后逐步询问用户的旅游城市和预算，随后根据城市名称，列举出著名的旅游景点并根据用户预算调整住宿、餐饮、交通等建议，确保整体费用在预算范围内";


    /*
     * 初始化Ai客户端
     * */
    public GuideApp(ChatModel dashscopeChatModel) {
        //基于文件的对话持久化
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        //基于内存的对话记忆
//        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        client = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_TEXT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()
                    ,new MyLoggerAdvisor()
//                        ,new ReReadingAdvisor()
                )
                .build();
    }

    /*
     * AI 基础对话方法  -- 支持多轮对话记忆
     * */
    public String doChat(String message, String chatId) {

        ChatResponse chatResponse = client.prompt()
                .user(message)
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                .call().chatResponse();

        String content = chatResponse.getResult().getOutput().toString();
        log.info("content: {}", content);
        return content;
    }

    record guideReport(String title, List<String> suggestions){}

    /*
     * AI 旅游计划报告功能  -- 实战结构化输出
     * */
    public guideReport doChatWithReport(String message, String chatId) {

        guideReport chatResponse = client.prompt()
                .user(message)
                .system(SYSTEM_TEXT + "每次对话后都要生成规划结果，标题名为{用户旅游城市}旅游规划，内容为包括每日行程、推荐景点、交通方式、餐饮建议等的规划报告")
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                .call()
                .entity(guideReport.class);
        log.info("guideReport: {}", chatResponse);
        return chatResponse;
    }
}