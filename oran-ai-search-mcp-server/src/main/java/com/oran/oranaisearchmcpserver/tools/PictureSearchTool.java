package com.oran.oranaisearchmcpserver.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PictureSearchTool {
    /**
     * Pexels API 基础 URL
     */
    private static final String BASE_URL = "https://api.pexels.com/v1/search";

    /**
     * API Key（需要替换为实际的 API Key）
     */
    private static final String apiKey = "UworuhwMfCguMUtiNf7co9jG2jER5X2rfYZx8UEFVHrJToQHae6j3zMH";

    /**
     * 搜索图片并返回逗号分隔的图片链接字符串
     *
     * @param query 搜索关键词（必需）
     * @return 逗号分隔的图片链接字符串
     */
    @McpTool(name = "search-photo", description = "search photo from web")
    public String searchPhotos(@McpToolParam(description = "search photo keyword")  String query) {
        if (StrUtil.isBlank(query)) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }

        try {
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("query", query);
            params.put("per_page", 10); // 获取最多80张图片

            // 使用 hutool 发送 GET 请求
            HttpResponse response = HttpRequest.get(BASE_URL)
                    .header("Authorization", apiKey)
                    .form(params)
                    .timeout(10000) // 10秒超时
                    .execute();

            // 检查响应状态
            if (response.getStatus() != 200) {
                throw new RuntimeException("请求失败，状态码：" + response.getStatus() + "，响应：" + response.body());
            }

            // 解析 JSON 响应
            String body = response.body();
            JSONObject jsonObject = JSONUtil.parseObj(body);

            // 提取图片链接
            JSONArray photosArray = jsonObject.getJSONArray("photos");
            List<String> urls = new ArrayList<>();
            if (photosArray != null) {
                for (int i = 0; i < photosArray.size(); i++) {
                    JSONObject photoObj = photosArray.getJSONObject(i);
                    JSONObject srcObj = photoObj.getJSONObject("src");
                    if (srcObj != null) {
                        String originalUrl = srcObj.getStr("original");
                        if (StrUtil.isNotBlank(originalUrl)) {
                            urls.add(originalUrl);
                        }
                    }
                }
            }

            // 使用英文逗号连接
            return StrUtil.join(",", urls);

        } catch (Exception e) {
            throw new RuntimeException("搜索图片失败：" + e.getMessage(), e);
        }
    }
}
