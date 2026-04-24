package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai-block")
public class AiBlockController {

    @Autowired
    private AiExtractionService extractionService;

    @Autowired
    private JobDataRepository jobDataRepository;

    @PostMapping("/extract")
    public ResponseEntity<?> extractAndSave(@RequestBody Map<String, String> payload) {
        String rawText = payload.get("text");
        if (rawText == null || rawText.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Text payload is empty"));
        }

        try {
            // Extract entities using Gemini API
            JobData extractedData = extractionService.extractData(rawText);

            // Save the extracted data into PostgreSQL
            JobData savedData = jobDataRepository.save(extractedData);

            return ResponseEntity.ok(savedData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
