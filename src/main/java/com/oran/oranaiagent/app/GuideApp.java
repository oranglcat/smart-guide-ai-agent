package com.oran.oranaiagent.app;

import com.oran.oranaiagent.advisor.MyLoggerAdvisor;
import com.oran.oranaiagent.chatMemory.FileBasedChatMemory;
import com.oran.oranaiagent.invoke.QueryReWriter;
import com.oran.oranaiagent.rag.GuideAppRagCustomAdvisor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

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

        String content = chatResponse.getResult().getOutput().getText();

        return content;
    }

    /*
     * AI 基础对话方法  -- 支持多轮对话记忆 -- SSE流式输出
     * */
    public Flux<String> doChatWithStream(String message, String chatId) {

        return client.prompt()
                .user(message)
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                .stream()
                .content();
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

        return chatResponse;
    }

    //应用本地Rag知识库的Ai旅游规划大师
    @Resource
    private VectorStore guideAppVectorStore;

    @Resource
    private QueryReWriter queryReWriter;

    public String doChatWithRag(String message, String chatId){

        String reWriteMessage = queryReWriter.doReWrite(message);

        ChatResponse chatResponse = client.prompt()
                .user(reWriteMessage)
                .system(SYSTEM_TEXT)
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                //自定义日志拦截器
                .advisors(new MyLoggerAdvisor())
                //开启RAG知识库
                .advisors(QuestionAnswerAdvisor.builder(guideAppVectorStore).build())
                //运用自定义的RAG检索增强服务 （文档查询器 + 上下文增强器）
                //.advisors(GuideAppRagCustomAdvisor.createAdvisor(guideAppVectorStore,"西安"))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content： {}",content);

        return content;
    }


    //应用云服务RAG
    @Resource
    private Advisor guideAppRagCloudAdvisor;

    public String doChatWithRagCloud(String message, String chatId){

        ChatResponse chatResponse = client.prompt()
                .user(message)
                .system(SYSTEM_TEXT)
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                //自定义日志拦截器
                .advisors(new MyLoggerAdvisor())
                //开启云服务RAG知识库
                .advisors(guideAppRagCloudAdvisor)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content： {}",content);

        return content;
    }


    //RAG检索增强 （通过PGVector数据库实现）


    @Resource
    VectorStore pgVectorVectorStore;

    public String doChatWithRagPGVector(String message, String chatId){

        ChatResponse chatResponse = client.prompt()
                .user(message)
                .system(SYSTEM_TEXT)
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                //自定义日志拦截器
                .advisors(new MyLoggerAdvisor())
                //开启RAG知识库(通过PGVector数据库)
                .advisors(QuestionAnswerAdvisor.builder(pgVectorVectorStore).build())
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content： {}",content);

        return content;
    }

    @Resource
    private ToolCallback[] allTools;

    /*
     * AI 工具调用
     * */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse chatResponse = client.prompt()
                .user(message)
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                //开启日志拦截器
                .advisors(new MyLoggerAdvisor())
                //开启工具调用
                .toolCallbacks(allTools)
                .call().chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        return content;
    }


    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    /*
     * AI MCP 工具调用
     * */
    public String doChatWithMCP(String message, String chatId) {
        ChatResponse chatResponse = client.prompt()
                .user(message)
                .advisors(a -> a.param(CONVERSATION_ID, chatId))
                //开启日志拦截器
                .advisors(new MyLoggerAdvisor())
                //开启工具调用
                .toolCallbacks(toolCallbackProvider)
                .call().chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        return content;
    }




}