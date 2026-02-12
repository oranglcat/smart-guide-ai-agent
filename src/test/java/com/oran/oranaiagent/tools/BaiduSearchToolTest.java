package com.oran.oranaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BaiduSearchToolTest {

    @Value("${search-api.api-key}")
    private String API_KEY;

    @Test
    void baiduSearch() {
        BaiduSearchTool baiduSearchTool = new BaiduSearchTool(API_KEY);
        String result = baiduSearchTool.searchWeb("编程导航 程序员鱼皮");
        Assertions.assertNotNull(result);
    }
}