package com.oran.oranaiagent.agent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oran.oranaiagent.agent.model.AgentState;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ToolCallAgent extends ReActAgent{

    private final ToolCallback[] availableTools;

    private ChatResponse toolCallResponse;

    private final ToolCallingManager toolCallingManager;

    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools){
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder().internalToolExecutionEnabled(false).build();
    }


    @Override
    public boolean think() {
        //1.校验提示词，拼接提示词
        if(StrUtil.isNotBlank(getNextStepPrompt())){
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList,chatOptions);
        try {
            //2.调用 AI 大模型，获取响应结果
            ChatResponse chatResponse = getClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();

            this.toolCallResponse = chatResponse;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            //获取响应结果
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            log.info(getName() + "的思考：" + result);
            log.info(getName() + "调用了" + toolCalls.size() + "个工具");
            //3.解析响应结果，获取工具调用信息
            String toolCallInfo = toolCalls.stream()
                    .map(toolCall -> String.format("工具调用名：%s 参数：%s ", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);

            if(toolCalls.isEmpty()){
                getMessageList().add(assistantMessage);
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {
            log.error(getName() + "的思考出现了问题：" + e.getMessage());
            messageList.add(new AssistantMessage("处理时遇到错误" + e.getMessage()));
            return false;
        }
    }

    @Override
    public String act() {
        //1.判断是否调用工具
        if(!toolCallResponse.hasToolCalls()){
            return "没有工具调用";
        }
        Prompt prompt = new Prompt(getMessageList(),chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallResponse);
        setMessageList(toolExecutionResult.conversationHistory());

        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());

        //判断是否调用了终止工具
        boolean doTerminate = toolResponseMessage.getResponses().stream()
                .anyMatch(toolResponse -> toolResponse.name().equals("doTerminate"));
        if(doTerminate){
            setState(AgentState.FINISHED);
        }

        String results= toolResponseMessage.getResponses().stream()
                .map(toolResponse -> "工具" + toolResponse.name() + "完成了任务：" + formatSearchResult(toolResponse.responseData()))
                .collect(Collectors.joining("\n"));
        log.info(results);
        return results;
    }

    /**
     * 将搜索工具的原始 JSON 响应转换为易读的文本
     */
    private static String formatSearchResult(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode node = objectMapper.readTree(json);
            // 提取关键字段，如果字段不存在则给默认值
            String title = node.has("title") ? node.get("title").asText() : " ";
            String snippet = node.has("snippet") ?  node.get("snippet").asText() : " ";
            String source = node.has("displayed_link") ? node.get("displayed_link").asText() : node.has("link") ? node.get("link").asText() : " ";
            String date = node.has("date") ? node.get("date").asText() : "";

            // 构建易读的格式 (支持 Markdown 更佳)
            StringBuilder sb = new StringBuilder();
            sb.append(title).append("\n");
            sb.append(source);
            if (!date.isEmpty()) {
                sb.append(" | 📅 时间：").append(date);
            }
            sb.append("\n");
            sb.append(snippet);

            return sb.toString();
        } catch (Exception e) {
            // 如果解析失败，返回原始内容以防丢失信息
            return json;
        }
    }
}
