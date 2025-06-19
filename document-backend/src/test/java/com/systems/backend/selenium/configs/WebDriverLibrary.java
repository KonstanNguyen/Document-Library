package com.systems.backend.selenium.configs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.stereotype.Component;

@Component
public class WebDriverLibrary {
    public WebDriver getFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver();

        // Configure Chrome options if needed
        // WebDriverManager.chromedriver().setup();
        // ChromeOptions options = new ChromeOptions();
        // options.addArguments("--start-maximized"); // Start browser maximized
        // options.addArguments("--disable-notifications"); // Disable notifications

        // return new ChromeDriver(options);
    }
}