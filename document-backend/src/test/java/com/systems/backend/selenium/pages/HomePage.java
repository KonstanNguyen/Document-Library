package com.systems.backend.selenium.pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@Slf4j
public class HomePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173/";

    @FindBy(how = How.CSS, using = "button.btn-login") // Precise CSS selector
    private WebElement buttonLogin;
    @FindBy(how = How.XPATH, using = "//*[@id='app']/div/div[1]/div/div/div[3]/div/div[1]/li/a/button")
    private WebElement buttonUpload;
    @FindBy(how = How.XPATH, using = "//*[@id='userDropdown']")
    private WebElement userDropdown;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
        log.info("Test: HomePage initialized");
    }

    public void navigateTo() {
        try {
            driver.get(BASE_URL);
            driver.manage().window().maximize();
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));
            log.info("Navigated to {}", BASE_URL);
        } catch (Exception e) {
            log.error("Failed to navigate to {}: {}", BASE_URL, e.getMessage());
            throw new RuntimeException("Navigation failed", e);
        }
    }

    public boolean isUserLoggedIn() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='userDropdown']")));
            log.info("User already logged in!");
            return true;
        } catch (Exception e) {
            log.info("User is not logged in!");
            return false;
        }
    }

    public LoginPage navigateToLogin() {
        try {
            // Wait for loading overlay to disappear
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loading-transition")));
            wait.until(ExpectedConditions.elementToBeClickable(buttonLogin));
            scrollToElement(buttonLogin);
            try {
                buttonLogin.click();
            } catch (Exception e) {
                log.warn("Standard click failed, attempting JavaScript click: {}", e.getMessage());
                jsClick(buttonLogin);
            }
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            log.info("Navigated to login page");
            return new LoginPage(driver);
        } catch (Exception e) {
            log.error("Failed to navigate to login page: {}", e.getMessage());
            throw new RuntimeException("Navigation to login page failed", e);
        }
    }

    public UploadPage navigateToUpload() {
        try {
            // Wait for any loading transitions to complete
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loading-transition")));
            
            // Wait for upload button and click
            wait.until(ExpectedConditions.elementToBeClickable(buttonUpload));
            scrollToElement(buttonUpload);
            try {
                buttonUpload.click();
            } catch (Exception e) {
                log.warn("Standard click failed, attempting JavaScript click: {}", e.getMessage());
                jsClick(buttonUpload);
            }

            // Wait for navigation
            Thread.sleep(1000);
            
            // Return new page object
            log.info("Navigated to upload page");
            return new UploadPage(driver);
        } catch (Exception e) {
            log.error("Failed to navigate to upload page: {}", e.getMessage());
            throw new RuntimeException("Navigation to upload page failed", e);
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