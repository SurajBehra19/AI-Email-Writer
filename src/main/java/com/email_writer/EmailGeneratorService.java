package com.email_writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public EmailGeneratorService(WebClient.Builder webClientBuilder,
                                 @Value("${gemini.api.url}") String baseUrl,
                                 @Value("${gemini.api.key}") String geminiApiKey) {
        this.apiKey = geminiApiKey;
        this.objectMapper = new ObjectMapper();
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public String generateEmailReply(EmailRequest emailRequest) {
        String prompt = buildPrompt(emailRequest);

        // Escape JSON properly to avoid issues with quotes and special characters
        String escapedPrompt = escapeJsonString(prompt);

        String requestBody = String.format("""
                {
                    "contents": [
                      {
                        "parts": [
                          {
                            "text": "%s"
                          }
                        ]
                      }
                    ]
                  }
                """, escapedPrompt);

        try {
            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1beta/models/gemini-2.0-flash:generateContent")
                            .build())
                    .header("x-goog-api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30)) // Add timeout
                    .block();

            return extractResponseContent(response);

        } catch (WebClientResponseException e) {
            System.err.println("API Error - Status: " + e.getStatusCode() + ", Body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Unexpected error calling Gemini API: " + e.getMessage());
            throw new RuntimeException("Failed to generate email", e);
        }
    }

    private String extractResponseContent(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);

            // Check if the response has the expected structure
            JsonNode candidates = root.path("candidates");
            if (candidates.isEmpty()) {
                throw new RuntimeException("No candidates found in Gemini API response");
            }

            JsonNode firstCandidate = candidates.get(0);
            JsonNode content = firstCandidate.path("content");
            JsonNode parts = content.path("parts");

            if (parts.isEmpty()) {
                throw new RuntimeException("No parts found in Gemini API response");
            }

            String text = parts.get(0).path("text").asText();

            if (text == null || text.trim().isEmpty()) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            return text.trim();

        } catch (JsonProcessingException e) {
            System.err.println("Failed to parse Gemini API response: " + e.getMessage());
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();

        // Fixed prompt for email generation (not replying to existing emails)
        prompt.append("You are a professional email writer. Write a complete, well-structured email based on the following requirements. ");
        prompt.append("Do not ask any questions or request clarification. Write the email directly.\n\n");

        // Use getContent() to match the corrected EmailRequest class
        String content = emailRequest.getContent();
        if (content == null) {
            content = emailRequest.getEmailContent(); // Fallback for backward compatibility
        }

        prompt.append("Email Requirements: ").append(content).append("\n");

        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Tone: ").append(emailRequest.getTone()).append("\n");
        }

        prompt.append("\nWrite a complete professional email including:\n");
        prompt.append("- Subject line (start with 'Subject: ')\n");
        prompt.append("- Appropriate greeting\n");
        prompt.append("- Clear and concise main content\n");
        prompt.append("- Professional closing\n");
        prompt.append("- Signature placeholder [Your Name]\n\n");
        prompt.append("Write the email now:");

        return prompt.toString();
    }

    // Helper method to properly escape JSON strings
    private String escapeJsonString(String input) {
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}