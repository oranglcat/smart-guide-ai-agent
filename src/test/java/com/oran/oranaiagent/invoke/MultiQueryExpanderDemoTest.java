package com.oran.oranaiagent.invoke;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MultiQueryExpanderDemoTest {

    @Resource
    private MultiQueryExpanderDemo multiQueryExpanderDemo;

    @Test
    void queryExpander() {
        List<Query> queries = multiQueryExpanderDemo.QueryExpander("是谁住在深海的大菠萝里");
        Assertions.assertNotNull(queries);
    }
}