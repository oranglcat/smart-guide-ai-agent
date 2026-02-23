package com.oran.oranaisearchmcpserver.tools;

import com.oran.oranaisearchmcpserver.tools.PictureSearchTool;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PictureSearchToolTest {

    @Resource
    private PictureSearchTool pictureSearchTool;

    @Test
    void searchPhoto() {
        String result = pictureSearchTool.searchPhotos("初音未来");
    }
}