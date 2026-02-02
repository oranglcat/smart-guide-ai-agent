package com.oran.oranaiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GuideAppTest {

    @Resource
    private GuideApp guideApp;

    @Test
    void doChat() {

        String chatId = UUID.randomUUID().toString();

        guideApp.doChat("你是谁",chatId);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();

        String message = "你好，我是oran，我下周想一个人去西安旅行，我的预算是5000元，我想去一些景色优美有历史古韵的地方";

        GuideApp.guideReport guideReport = guideApp.doChatWithReport(message, chatId);
    }
}