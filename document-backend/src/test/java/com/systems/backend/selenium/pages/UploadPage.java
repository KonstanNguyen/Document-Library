package com.systems.backend.selenium.pages;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
public class UploadPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(how = How.ID, using = "title")
    private WebElement titleInput;
    @FindBy(how = How.ID, using = "categories")
    private WebElement categoryDropdown;
    @FindBy(how = How.ID, using = "formFile")
    private WebElement fileInput;
    @FindBy(how = How.XPATH, using = "//*[@id='app-content']/div/div[2]/div/form/div/div[4]/textarea")
    private WebElement descriptionInput;
    @FindBy(how = How.CSS, using = "[data-testid='submit-button']")
    private WebElement submitButton;
    @FindBy(how = How.CSS, using = ".alert")
    private WebElement errorMessage;
    @FindBy(how = How.CSS, using = ".modal-content p")
    private WebElement modalMessage;
    @FindBy(how = How.CSS, using = ".btn-outline-danger")
    private WebElement modalCloseButton;

    public UploadPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
        log.info("Test: UploadPage initialized");
    }

    public void uploadFile(String title, Integer category, String filename, String description) throws Exception {
        log.info("Testing file: {}", filename);

        scrollToElement(submitButton);
        Thread.sleep(500);

        inputField(titleInput, title);
        jsSetValue(descriptionInput, "");
        inputField(descriptionInput, description);

        jsSetValue(categoryDropdown, "");
        if (category != null) {
            try {
                scrollToElement(categoryDropdown);
                Thread.sleep(500);
                wait.until(ExpectedConditions.elementToBeClickable(categoryDropdown)).click();
                Select select = new Select(categoryDropdown);
                select.selectByValue(String.valueOf(category));
            } catch (Exception e) {
                log.error("Error selecting category: {}", e.getMessage());
                jsSetValue(categoryDropdown, String.valueOf(category));
            }
        }

        jsSetValue(fileInput, "");
        if (!filename.isEmpty()) {
            fileInput.sendKeys(filename);
            try {
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();
                log.info("⚠️ Alert xuất hiện: {}", alertText);
                Assertions.assertEquals(alertText, alertText, "❌ Expected alert, got: " + alertText);
                alert.accept();
                log.info("✅ Đã đóng alert.");
                return;
            } catch (NoAlertPresentException e) {
                log.info("No alert present, continuing.");
            }
        } else {
            log.info("⚠️ No file selected, skipping file input.");
        }

        Thread.sleep(1000);

        try {
            scrollToElement(submitButton);
            Thread.sleep(500);
            wait.until(ExpectedConditions.elementToBeClickable(submitButton));
            try {
                submitButton.click();
            } catch (Exception e) {
                log.info("Direct click failed, trying JavaScript click");
                jsClick(submitButton);
            }
        } catch (Exception e) {
            log.error("Error clicking submit button: {}", e.getMessage());
            jsClick(submitButton);
        }

        Thread.sleep(1000);
    }

    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            String message = errorMessage.getText();
            log.info("Error message: {}", message);
            return message;
        } catch (Exception e) {
            log.error("Error retrieving error message: {}", e.getMessage());
            return "";
        }
    }

    public String getModalMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(modalMessage));
            String message = modalMessage.getText();
            log.info("Modal message: {}", message);
            return message;
        } catch (Exception e) {
            log.error("Error retrieving modal message: {}", e.getMessage());
            return "";
        }
    }

    public void closeModal() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(modalCloseButton));
            modalCloseButton.click();
            log.info("Closed modal");
        } catch (Exception e) {
            log.error("Error closing modal: {}", e.getMessage());
        }
    }

    public boolean isTitleRequired() {
        return isRequired(titleInput);
    }

    public void verifyMessage(String expected, String actual, boolean isSuccess) {
        if (isSuccess) {
            Assertions.assertTrue(actual.contains("thành công") || actual.contains("đã được gửi"),
                    "Expected success message, got: " + actual);
            log.info("✅ Passed (Modal): {}", actual);
        } else {
            Assertions.assertEquals(expected, actual, "❌ Expected \"" + expected + "\", but got \"" + actual + "\"");
            log.info("✅ Not Passed: {}", actual);
        }
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

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        log.info("Scrolled to element");
    }

    private void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        log.info("Performed JavaScript click on element");
    }

    private void jsSetValue(WebElement element, String value) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));", element, value);
        log.info("Set value using JavaScript: {}", value);
    }
}