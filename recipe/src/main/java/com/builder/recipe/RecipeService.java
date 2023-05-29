package com.builder.recipe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecipeService {
    public String getMealIdea(String ingredients) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("API_KEY");
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "system");
        userMessage.put("content", "Based on the ingredients " + ingredients + ", suggest a meal idea, and list preparation steps in numbers");
        messages.add(userMessage);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("messages", messages);
        requestData.put("max_tokens", 2000);
        requestData.put("model", "gpt-3.5-turbo");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestData, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity("https://api.openai.com/v1/chat/completions", request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String apiResponse = response.getBody();

            return extractMealIdea(apiResponse);
        } else {

            return "Error occurred while fetching meal idea.";
        }
    }
    private String extractMealIdea(String apiResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(apiResponse);


            JsonNode choicesNode = responseJson.get("choices");
            if (choicesNode != null && choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode firstChoice = choicesNode.get(0);
                JsonNode messageNode = firstChoice.get("message");
                if (messageNode != null && messageNode.isObject()) {
                    JsonNode contentNode = messageNode.get("content");
                    if (contentNode != null) {
                        return contentNode.asText();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Unable to extract meal idea from API response.";
    }
}
