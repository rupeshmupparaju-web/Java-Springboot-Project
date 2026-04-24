package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiExtractionService {

    @Value("${GEMINI_API_KEY:}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public JobData extractData(String rawText) {
        if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
            throw new RuntimeException("Gemini API Key is missing. Please add GEMINI_API_KEY to your .env file.");
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

        String prompt = "Extract the following keys from this text and return it as a pure JSON object: jobDescription, location, vendorCompanyName, contactNumber, status, jobId. Do not include markdown formatting or tags like ```json. Here is the text:\n" + rawText;

        try {
            // Build the body for Gemini API
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", prompt);

            Map<String, Object> parts = new HashMap<>();
            parts.put("parts", List.of(textPart));

            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("contents", List.of(parts));
            
            // Add generationConfig to enforce JSON response
            Map<String, Object> genConfig = new HashMap<>();
            genConfig.put("responseMimeType", "application/json");
            requestBodyMap.put("generationConfig", genConfig);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBodyMap, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String extractedJsonText = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // The output is the raw JSON string that Gemini produced
            JsonNode extractedDataNode = objectMapper.readTree(extractedJsonText.trim());

            JobData jobData = new JobData();
            jobData.setJobDescription(extractedDataNode.path("jobDescription").asText(null));
            jobData.setLocation(extractedDataNode.path("location").asText(null));
            jobData.setVendorCompanyName(extractedDataNode.path("vendorCompanyName").asText(null));
            jobData.setContactNumber(extractedDataNode.path("contactNumber").asText(null));
            jobData.setStatus(extractedDataNode.path("status").asText(null));
            jobData.setJobId(extractedDataNode.path("jobId").asText(null));

            return jobData;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to extract data using AI: " + e.getMessage());
        }
    }
}
