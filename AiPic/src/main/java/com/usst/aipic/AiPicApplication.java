package com.usst.aipic;

import com.usst.aipic.MCPserver.PexelsImageSearchTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiPicApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPicApplication.class, args);
    }

    @Bean
    ToolCallbackProvider imageTool(PexelsImageSearchTool pexelsImageSearchTool){
        return MethodToolCallbackProvider.builder()
                .toolObjects(pexelsImageSearchTool)
                .build();
    }
}
