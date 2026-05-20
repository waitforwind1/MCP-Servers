package com.usst.aipic.MCPserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PexelsImageSearchTool {

    private static final String PEXELS_SEARCH_URL = "https://api.pexels.com/v1/search";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private final String pexelsApiKey = System.getenv("PEXELS_APIKEY");

    public PexelsImageSearchTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().build();
    }

    @Tool(description = "在 Pexels 平台根据关键词搜索图片，返回图片地址、作者、来源页面等信息。")
    public String searchPexelsImages(
            @ToolParam(description = "图片搜索关键词，例如：cat、nature、Shanghai skyline、game character")
            String query
    ) {
        try {
            if (query == null || query.isBlank()) {
                return "搜索失败：query 不能为空。";
            }

            String requestUrl = UriComponentsBuilder
                    .fromHttpUrl(PEXELS_SEARCH_URL)
                    .queryParam("query", query)
                    .queryParam("page", "1")
                    .queryParam("per_page", "5")
                    .queryParam("locale", "zh-CN")
                    .build()
                    .encode()
                    .toUriString();

            String response = restClient.get()
                    .uri(requestUrl)
                    .header("Authorization", pexelsApiKey)
                    .retrieve()
                    .body(String.class);

            return parsePexelsResponse(query, response);

        } catch (Exception e) {
            return "Pexels 图片搜索失败：" + e.getMessage();
        }
    }

    private String parsePexelsResponse(String query, String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode photos = root.path("photos");

        if (!photos.isArray() || photos.isEmpty()) {
            return "没有在 Pexels 搜索到与【" + query + "】相关的图片。";
        }

        StringBuilder result = new StringBuilder();

        result.append("Pexels 图片搜索结果\n");
        result.append("搜索关键词：").append(query).append("\n");
        result.append("总结果数：").append(root.path("total_results").asInt()).append("\n");
        result.append("说明：Photos provided by Pexels，使用时建议标注摄影师与 Pexels 来源。\n\n");

        int index = 1;

        for (JsonNode photo : photos) {
            String alt = photo.path("alt").asText("");
            String pageUrl = photo.path("url").asText("");
            String photographer = photo.path("photographer").asText("");
            String photographerUrl = photo.path("photographer_url").asText("");

            JsonNode src = photo.path("src");

            String original = src.path("original").asText("");
            String large = src.path("large").asText("");
            String medium = src.path("medium").asText("");
            String landscape = src.path("landscape").asText("");
            String portrait = src.path("portrait").asText("");
            String tiny = src.path("tiny").asText("");

            result.append(index++).append(". ")
                    .append(alt.isBlank() ? "Pexels 图片" : alt)
                    .append("\n");

            result.append("摄影师：").append(photographer).append("\n");
            result.append("摄影师主页：").append(photographerUrl).append("\n");
            result.append("Pexels 页面：").append(pageUrl).append("\n");
            result.append("原图：").append(original).append("\n");
            result.append("大图：").append(large).append("\n");
            result.append("中图：").append(medium).append("\n");
            result.append("横图：").append(landscape).append("\n");
            result.append("竖图：").append(portrait).append("\n");
            result.append("缩略图：").append(tiny).append("\n\n");
        }

        return result.toString();
    }
}