package com.oran.oranaiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
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


    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();

        String message = "西安的特色美食有哪些？哪里能吃到正宗小吃？";

        guideApp.doChatWithRag(message,chatId);
    }

    @Test
    void doChatWithRagCloud() {
        String chatId = UUID.randomUUID().toString();

        String message = "西安的特色美食有哪些？哪里能吃到正宗小吃？";

        guideApp.doChatWithRagCloud(message,chatId);
    }

    @Test
    void doChatWithRagPGVector() {
        String chatId = UUID.randomUUID().toString();

        String message = "西安的特色美食有哪些？哪里能吃到正宗小吃？";

        guideApp.doChatWithRagPGVector(message,chatId);
    }

    @Test
    void doChatWithTools() {

        //testMessage("周末想带女朋友去上海约会，帮我在网上搜索推荐几个适合情侣的小众打卡地？");


       // testMessage("最近想去西安旅游，请你帮我在https://www.toutiao.com/article/7409941057254769186/?wid=1770891438952推荐几个景点");


        //testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");


        //testMessage("使用终端执行script.py脚本来生成数据分析报告");


        //testMessage("保存我的旅游档案为txt文本文件");

       // testMessage("我想在用户表中插入一条用户的信息，用户名为oran，年龄为18，邮箱为242952855@gamil.com");

        testMessage("我想从数据库中查找一个用户的信息，用户的姓名为oran,请你将他的信息生成一份‘Oran’个人信息PDF给我");

        //testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = guideApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMCP() {
        //测试地图 MCP
        String chatId = UUID.randomUUID().toString();
        String message = "我的另一半住在西安雁塔区，请你帮我找到五公里内合适的约会地点";
        String result = guideApp.doChatWithMCP(message, chatId);
        Assertions.assertNotNull(result);
    }
}