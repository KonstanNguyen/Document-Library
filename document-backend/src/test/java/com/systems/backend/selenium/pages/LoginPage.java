package com.systems.backend.selenium.pages;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Slf4j
public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(how = How.ID, using = "username")
    private WebElement usernameField;
    @FindBy(how = How.ID, using = "password")
    private WebElement passwordField;
    @FindBy(how = How.XPATH, using = "//button[@type='submit']")
    private WebElement loginButton;
    @FindBy(how = How.CSS, using = ".alert")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
        log.info("Test: LoginPage initialized");
    }

    public void login(String username, String password) {
        log.info("Testing: Username = {}, Password = {}", username, password);
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            inputField(usernameField, username);
            wait.until(ExpectedConditions.visibilityOf(passwordField));
            inputField(passwordField, password);
            wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            loginButton.click();
            log.info("Login attempt completed");
        } catch (Exception e) {
            log.error("Failed to perform login: {}", e.getMessage());
            throw new RuntimeException("Login failed", e);
        }
    }

    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            String message = errorMessage.getText();
            log.info("Error message retrieved: {}", message);
            return message;
        } catch (Exception e) {
            log.info("No error message found, likely successful login or timeout");
            return "";
        }
    }

    public boolean isLoginSuccessful() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/dashboard"),
                    ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.xpath("//*[@id='userDropdown']")),
                    ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.cssSelector(".success"))
            ));
            log.info("Login successful, redirect or success element found. Current URL: {}", driver.getCurrentUrl());
            return true;
        } catch (Exception e) {
            log.info("Login not successful, no success indicator found: {}. Current URL: {}", e.getMessage(), driver.getCurrentUrl());
            return false;
        }
    }

    public void verifyErrorMessage(String expected, String actual) {
        Assertions.assertEquals(expected, actual, "❌ Expected \"" + expected + "\", but got \"" + actual + "\"");
        log.info("✅ Verified message: {}", actual);
    }

    private void inputField(WebElement element, String data) {
        element.clear();
        element.sendKeys(data);
        log.info("Input data: {}", data);
    }

    private boolean isRequired(WebElement element) {
        try {
            String required = element.getAttribute("required");
            return required != null;
        } catch (Exception e) {
            log.error("⚠️ Error checking required attribute: {}", e.getMessage());
            return false;
        }
    }
}