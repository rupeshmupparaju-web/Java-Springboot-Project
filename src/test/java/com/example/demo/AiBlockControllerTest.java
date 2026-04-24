package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AiBlockControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AiExtractionService extractionService;

    @Mock
    private JobDataRepository jobDataRepository;

    @InjectMocks
    private AiBlockController aiBlockController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(aiBlockController).build();
    }

    @Test
    public void testValidExtractionRequest() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("text", "Sample job desc here");

        JobData mockJobData = new JobData();
        mockJobData.setId(1L);
        mockJobData.setJobDescription("Sample job desc");
        mockJobData.setStatus("Open");

        Mockito.when(extractionService.extractData(anyString())).thenReturn(mockJobData);
        Mockito.when(jobDataRepository.save(Mockito.any(JobData.class))).thenReturn(mockJobData);

        mockMvc.perform(post("/api/ai-block/extract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("Open"));
    }

    @Test
    public void testEmptyPayload() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("text", "");

        mockMvc.perform(post("/api/ai-block/extract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Text payload is empty"));
    }
}
