package com.usst.aipic;

import cn.hutool.core.lang.Assert;
import com.usst.aipic.MCPserver.PexelsImageSearchTool;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PublicKey;

@SpringBootTest
public class McpTest {

    @Resource
    PexelsImageSearchTool pexelsImageSearchTool;

    @Test
    public void test(){
        String res = pexelsImageSearchTool.searchPexelsImages("forest");
        Assertions.assertNotNull(res);
    }
}
