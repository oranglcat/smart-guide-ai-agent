package com.oran.oranaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "Oran项目.pdf";
        String content = "Oran项目 https://gitee.com/oranglcat/oran-ai-agent-backend";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}