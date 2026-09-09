package com.fitness.aiservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getRecommendation(prompt);
        log.info("RESPONSE FROM AI {}",aiResponse);
        return processAIResponse(activity,aiResponse);
    }
    private Recommendation processAIResponse(
            Activity activity,
            String aiResponse
    ) {

        try {

            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode stepsNode = rootNode.path("steps");

            String jsonText = null;

            for (JsonNode step : stepsNode) {

                if ("model_output".equals(
                        step.path("type").asText()
                )) {

                    JsonNode contentNode = step.path("content");

                    if (contentNode.isArray()
                            && !contentNode.isEmpty()) {

                        jsonText = contentNode
                                .get(0)
                                .path("text")
                                .asText();

                        break;
                    }
                }
            }

            if (jsonText == null || jsonText.isBlank()) {
                throw new RuntimeException(
                        "No AI output found"
                );
            }

            // Parse Gemini's JSON
            JsonNode recommendationNode =
                    mapper.readTree(jsonText);

            String recommendation =
                    recommendationNode
                            .path("recommendation")
                            .asText();

            List<String> improvements =
                    mapper.convertValue(
                            recommendationNode.path("improvements"),
                            new TypeReference<List<String>>() {}
                    );

            List<String> suggestions =
                    mapper.convertValue(
                            recommendationNode.path("suggestions"),
                            new TypeReference<List<String>>() {}
                    );

            List<String> safety =
                    mapper.convertValue(
                            recommendationNode.path("safety"),
                            new TypeReference<List<String>>() {}
                    );

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .recommendation(recommendation)
                    .type(activity.getType().toString())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {

            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType().toString())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue with your current routine"))
                .suggestions(Collections.singletonList("Conside consulting a fitness consultant"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }


    private String createPromptForActivity(Activity activity) {
        return String.format(
                "Analyze the following fitness activity and provide a personalized recommendation. " +
                        "Activity type: %s, Duration: %s minutes, Calories burned: %s, Additional metrics: %s. " +
                        "Based on this information, provide useful feedback and suggestions for the user.",
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
