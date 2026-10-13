package com.aiinterviewcoach.modules.ai.service;

import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;
import com.aiinterviewcoach.common.exception.AiProcessingException;

@Service
public class GeminiClient {
    private final WebClient client;
    private final String apiKey;
    private final String url;
    private final JsonMapper mapper = JsonMapper.builder().build();
    private final java.util.concurrent.Semaphore permits = new java.util.concurrent.Semaphore(4);

    public GeminiClient(WebClient.Builder builder, @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.api.url}") String url) {
        this.client = builder.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)).build();
        this.apiKey = apiKey;
        this.url = url;
    }

    public <T> T generate(String prompt, Class<T> type) {
        if (apiKey.isBlank()) throw new AiProcessingException("Set GEMINI_API_KEY to enable AI features.");
        if (!permits.tryAcquire()) throw new AiProcessingException("AI is busy. Please retry shortly.");
        try {
            String body = client.post().uri(url).header("x-goog-api-key", apiKey)
                .bodyValue(Map.of("contents", java.util.List.of(Map.of("parts", java.util.List.of(Map.of("text", prompt)))),
                    "generationConfig", Map.of("responseMimeType", "application/json", "temperature", 0.3)))
                .retrieve().bodyToMono(String.class).block(Duration.ofSeconds(90));
            var response = mapper.readTree(body);
            var parts = response.path("candidates").path(0).path("content").path("parts");
            StringBuilder content = new StringBuilder();
            for (var part : parts) if (!part.path("thought").asBoolean(false)) content.append(part.path("text").asText(""));
            if (content.isEmpty()) throw new AiProcessingException("AI returned no content. Please retry.");
            return mapper.readValue(content.toString(), type);
        } catch (AiProcessingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AiProcessingException("AI request failed or returned invalid data. Please retry.", ex);
        } finally {
            permits.release();
        }
    }
}
