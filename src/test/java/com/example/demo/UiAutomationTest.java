package com.example.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UiAutomationTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Headless mode so it doesn't intrude on screen
        options.addArguments("--remote-allow-origins=*");
        this.driver = new ChromeDriver(options);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAiBlockPageHandlesEmptySubmission() {
        driver.get("http://localhost:" + 8080 + "/ai-block.html");

        assertTrue(driver.getTitle().contains("AI Data Extractor"));

        WebElement extractBtn = driver.findElement(By.id("extractBtn"));
        WebElement statusMsg = driver.findElement(By.id("statusMsg"));

        // Click Extract without entering any text
        extractBtn.click();

        // Wait for the status message to be visible
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(statusMsg));

        assertTrue(statusMsg.getText().contains("Please paste some text first"));
    }
}
