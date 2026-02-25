package com.oran.oranaiagent.Controller;

import com.oran.oranaiagent.agent.agent.OranManus;
import com.oran.oranaiagent.app.GuideApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.naming.ldap.PagedResultsControl;
import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private GuideApp guideApp;


    /*
     * 同步调用 旅游规划大师 ai应用
     * */
    @GetMapping("/guide_app/chat/sync")
    public String doChatWithGuideAppSync(String userMessage, String chatId) {
        return guideApp.doChat(userMessage, chatId);
    }


    /*
     * SSE流式调用 旅游规划大师 ai应用
     * */
    @GetMapping(value = "/guide_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithGuideAppSSE(String userMessage, String chatId) {
        return guideApp.doChatWithStream(userMessage, chatId);
    }

    @GetMapping(value = "/guide_app/chat/sse/server_sent")
    public Flux<ServerSentEvent<String>> doChatWithGuideAppServerSent(String message, String chatId) {
        return guideApp.doChatWithStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    @GetMapping(value = "/guide_app/chat/sse/emitter")
    public SseEmitter doChatWithGuideAppSseEmitter(String message, String chatId) {
        //创建一个SseEmitter对象
        SseEmitter emitter = new SseEmitter(180000L);

        //订阅流式数据实时发送给SseEmitter
        guideApp.doChatWithStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        emitter.send(chunk);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }, emitter::completeWithError, emitter::complete);
        return emitter;
    }


    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /*
    * 流式调用 Manus智能体
    * */
    @GetMapping(value = "/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        OranManus oranManus = new OranManus(allTools,dashscopeChatModel);
        return oranManus.runStream(message);
    }
}
