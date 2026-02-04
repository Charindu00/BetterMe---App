package com.betterme.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ LEARNING POINT: Gemini API Service ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║ This service handles HTTP communication with Google's Gemini AI API. ║
 * ║ ║
 * ║ Key concepts: ║
 * ║ 1. @Value injects values from application.yml ║
 * ║ 2. RestTemplate is Spring's HTTP client ║
 * ║ 3. We build the request body matching Gemini's expected format ║
 * ║ ║
 * ║ API Docs: https://ai.google.dev/docs ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@Service
@Slf4j
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * ─────────────────────────────────────────────────────────────────────
     * SEND PROMPT TO GEMINI
     * ─────────────────────────────────────────────────────────────────────
     * Sends a text prompt to Gemini and returns the generated response.
     * 
     * @param prompt The text prompt to send
     * @return Generated text response
     */
    public String generateContent(String prompt) {
        try {
            // Build the request URL with API key
            String url = apiUrl + "?key=" + apiKey;

            // Build request body in Gemini's expected format
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", prompt)))),
                    "generationConfig", Map.of(
                            "temperature", 0.7,
                            "maxOutputTokens", 500,
                            "topP", 0.9));

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create request entity
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Make API call
            log.info("🤖 Calling Gemini API...");
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, request, String.class);

            // Parse response
            return parseGeminiResponse(response.getBody());

        } catch (Exception e) {
            log.error("Gemini API error: {}", e.getMessage());
            return getFallbackResponse();
        }
    }

    /**
     * ─────────────────────────────────────────────────────────────────────
     * PARSE GEMINI RESPONSE
     * ─────────────────────────────────────────────────────────────────────
     * Extracts the text from Gemini's JSON response structure
     */
    private String parseGeminiResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode candidates = root.path("candidates");

            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");

                if (parts.isArray() && parts.size() > 0) {
                    String text = parts.get(0).path("text").asText();
                    log.info("✅ Gemini response received");
                    return text;
                }
            }

            log.warn("Could not parse Gemini response, using fallback");
            return getFallbackResponse();

        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage());
            return getFallbackResponse();
        }
    }

    /**
     * ─────────────────────────────────────────────────────────────────────
     * FALLBACK RESPONSE
     * ─────────────────────────────────────────────────────────────────────
     * Returns a default message when API is unavailable
     */
    private String getFallbackResponse() {
        return "Keep going! Every step forward is progress, " +
                "no matter how small. You've got this! 💪";
    }

    /**
     * Check if API is configured
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.equals("your-api-key-here");
    }
}
