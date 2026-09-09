package com.fitness.aiservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.url}")
    private String geminiUrl;

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getRecommendation(String details) {

        Map<String, Object> schema = Map.of(
                "type", "object",

                "properties", Map.of(
                        "recommendation", Map.of(
                                "type", "string"
                        ),

                        "improvements", Map.of(
                                "type", "array",
                                "items", Map.of(
                                        "type", "string"
                                )
                        ),

                        "suggestions", Map.of(
                                "type", "array",
                                "items", Map.of(
                                        "type", "string"
                                )
                        ),

                        "safety", Map.of(
                                "type", "array",
                                "items", Map.of(
                                        "type", "string"
                                )
                        )
                ),

                "required", new String[]{
                        "recommendation",
                        "improvements",
                        "suggestions",
                        "safety"
                }
        );
        Map<String, Object> responseFormat = Map.of(
                "type", "text",
                "mime_type", "application/json",
                "schema", schema
        );

        Map<String, Object> requestBody = Map.of(
                "model", "gemini-3.8-flash",
                "input", details,
                "response_format", responseFormat
        );


        return webClient.post()
                .uri(geminiUrl)
                .header("Content-Type", "application/json")
                .header("X-goog-api-key", geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}