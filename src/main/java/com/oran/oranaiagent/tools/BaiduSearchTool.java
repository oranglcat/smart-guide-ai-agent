package com.oran.oranaiagent.tools;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaiduSearchTool {

    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public BaiduSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }


    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");

        try {
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            JSONObject jsonObject = JSONUtil.parseObj(response);

            // ✅ 安全获取 organic_results：支持字段缺失、类型不符、null 等情况
            JSONArray organicResults = jsonObject.getByPath("organic_results", JSONArray.class);
            if (organicResults == null || organicResults.isEmpty()) {
                return "No organic results found for query: '" + query + "'";
            }

            //  安全截取前 N 条（防 toIndex > size）
            int endIndex = Math.min(5, organicResults.size());
            List<Object> objects = organicResults.subList(0, endIndex);

            //转字符串
            StringBuilder sb = new StringBuilder();
            for (Object obj : objects) {
                JSONObject tmpJSONObject = (JSONObject) obj;
                sb.append(tmpJSONObject.toString());
            }
            return sb.toString();

        } catch (Exception e) {
            // 记录详细错误（方便调试），不要只返回 message
            String errorMsg = "Error searching Baidu for query '" + query + "': " + e.getMessage();
            System.err.println(errorMsg); // 或用 log.error(...)
            e.printStackTrace(); // 开发期建议保留，生产可关
            return errorMsg;
        }
    }
}