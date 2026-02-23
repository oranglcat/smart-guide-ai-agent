package com.oran.oranaiagent.agent.agent;


import cn.hutool.core.util.StrUtil;
import com.oran.oranaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public abstract class BaseAgent {

    private String name;

    private String SystemPrompt;
    private String nextStepPrompt;

    private AgentState state = AgentState.IDLE;

    private int current_step = 0;
    private int max_step = 10;

    private ChatClient client;

    private List<Message> messageList = new ArrayList<>();

    /*
    * 运行控制
    * */
    public String run(String userPrompt){
        //校验
        if(this.state != AgentState.IDLE){
            throw new RuntimeException("Cannot run agent from state:" + this.state);
        }
        if(StrUtil.isBlank(userPrompt)){
            throw  new RuntimeException("Cannot run with empty user prompt");
        }
        //开始执行，更改状态
        state = AgentState.RUNNING;
        //将用户输入添加到上下文
        messageList.add(new UserMessage(userPrompt));
        //定义保存结果列表
        List<String> results = new ArrayList<>();
        try {
            //执行循环
            for (int i = 0; i < max_step && state != AgentState.FINISHED; i++) {
                int stepNumber = i + 1;
                current_step = stepNumber;
                log.info("Executing step " + stepNumber + "/" + max_step);
                /*
                * 单步执行
                * */
                String stepResult = step();
                String result = "Step" + stepNumber + ":" + stepResult;
                results.add(result);

                if(current_step >= max_step){
                    state = AgentState.FINISHED;
                    results.add("Terminated: Reached max steps (" + max_step + ")");
                }
            }
        } catch (RuntimeException e) {
            state = AgentState.ERROR;
            log.error("Error executing agent", e);
            return "执行错误" + e.getMessage();
        }finally {
            //清理资源
            this.cleanup();
        }

        return String.join("\n",results);
    }

    /*
    * 具体执行步骤 -- 子类实现
    * */
    public abstract String step();

    /*
    * 清理资源
    * */
    protected void cleanup(){}
}
