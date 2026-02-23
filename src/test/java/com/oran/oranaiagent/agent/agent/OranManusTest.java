package com.oran.oranaiagent.agent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OranManusTest {
    @Resource
    private OranManus oranManus;

    @Test
    public void run(){
        String prompt = "我的另一半居住西安雁塔，请帮我找到 5 公里内合适的约会地点，  \n" +
                "                并结合一些网络图片，制定一份详细的约会计划，  \n" +
                "                并以 PDF 格式输出";
        String result = oranManus.run(prompt);
        Assertions.assertNotNull(result);
    }
}