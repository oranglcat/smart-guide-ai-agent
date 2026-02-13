package com.oran.oranaiagent.tools;

import com.oran.oranaiagent.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class toolRegistration {

    @Value("${search-api.api-key}")
    private String API_KEY;

    @Resource
    private UserMapper userMapper;

    @Bean
    public ToolCallback[] allTools(){
        BaiduSearchTool baiduSearchTool = new BaiduSearchTool(API_KEY);
        FileOperationTool fileOperationTool = new FileOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        DatabaseTool databaseTool = new DatabaseTool(userMapper);
        return ToolCallbacks.from(
                baiduSearchTool,
                fileOperationTool,
                pdfGenerationTool,
                resourceDownloadTool,
                webScrapingTool,
                terminalOperationTool,
                databaseTool
        );
    }
}
