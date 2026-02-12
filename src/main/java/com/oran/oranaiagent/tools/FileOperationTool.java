package com.oran.oranaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.oran.oranaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/*
* 文件操作工具
* */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "read content from a file ")
    public String readFile(@ToolParam(description = "Name of the file being read") String fileName){
        String filePath = FILE_DIR + "/" + fileName;

        try {
            String s = FileUtil.readUtf8String(filePath);
            return s;
        }catch (Exception e){
            return "read file operation failed:" + e.getMessage();
        }
    }


    @Tool(description = "write content to a file ")
    public String writeFile(@ToolParam(description = "Name of the file being write") String fileName,
                            @ToolParam(description = "The content to be written to the file") String content){
        String filePath = FILE_DIR + "/" + fileName;

        try {
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content,filePath);
            return "write content operation successful";
        } catch (Exception e) {
            return "write operation failed" + e.getMessage();
        }
    }

}
