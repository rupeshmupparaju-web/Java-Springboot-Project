package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JobDataRepositoryTest {

    @Autowired
    private JobDataRepository jobDataRepository;

    @Test
    public void testSaveAndRetrieveJobData() {
        JobData jobData = new JobData();
        jobData.setJobDescription("Test Description");
        jobData.setLocation("Remote");
        jobData.setJobId("J123");
        
        JobData savedJobData = jobDataRepository.save(jobData);
        
        assertNotNull(savedJobData.getId(), "Database should generate an ID");
        
        JobData retrieved = jobDataRepository.findById(savedJobData.getId()).orElse(null);
        assertNotNull(retrieved);
        assertEquals("Test Description", retrieved.getJobDescription());
        assertEquals("Remote", retrieved.getLocation());
        assertEquals("J123", retrieved.getJobId());
    }
}
