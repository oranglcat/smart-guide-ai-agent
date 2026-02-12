package com.oran.oranaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ResourceDownloadToolTest {

    @Test
    public void testDownloadResource() {
        ResourceDownloadTool tool = new ResourceDownloadTool();
        String url = "https://docs.spring.io/spring-ai/reference/_/img/spring-logo.svg";
        String fileName = "spring-logo.svg";
        String result = tool.downloadResource(url, fileName);
        assertNotNull(result);
    }
}