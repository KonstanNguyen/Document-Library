package com.systems.backend.selenium;

import com.systems.backend.selenium.configs.WebDriverLibrary;
import com.systems.backend.selenium.pages.HomePage;
import com.systems.backend.selenium.pages.LoginPage;
import com.systems.backend.selenium.pages.UploadPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeleniumTests {
    private WebDriver driver;
    private HomePage homePage;
    private LoginPage loginPage;
    private UploadPage uploadPage;

    @Autowired
    private WebDriverLibrary webDriverLibrary;

    private static final String BASE_DIR = Paths.get("src", "main", "resources", "file_test").toAbsolutePath().toString();

    @BeforeAll
    public void setUpAll() {
        log.info("Initializing single browser instance");
        driver = webDriverLibrary.getFirefoxDriver();
        driver.manage().window().maximize();
        homePage = new HomePage(driver);
        loginPage = new LoginPage(driver);
    }

    @BeforeEach
    public void setUp() {
        log.info("Resetting browser state");
        try {
            driver.get("about:blank");
            driver.manage().deleteAllCookies();
            homePage.navigateTo();
        } catch (Exception e) {
            log.error("Browser reset failed: {}", e.getMessage());
            throw new RuntimeException("Browser reset failed", e);
        }
    }

    @AfterEach
    public void cleanUp() {
        try {
            driver.manage().deleteAllCookies();
            driver.navigate().refresh();
            log.info("Browser state cleared");
        } catch (Exception e) {
            log.error("Error cleaning up browser state: {}", e.getMessage());
        }
    }

    @AfterAll
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
                log.info("Single browser instance closed");
            }
        } catch (Exception e) {
            log.error("Error closing browser: {}", e.getMessage());
        }
    }

    private void testLoginForm() throws Exception {
        Object[][] testCases = {
                {"user@#$%", "short", "Tên đăng nhập hoặc mật khẩu không đúng"},
                {"admin", "wrongpass", "Tên đăng nhập hoặc mật khẩu không đúng"},
                {"Long", "123", "Đăng nhập thành công!"}
        };

        for (Object[] test : testCases) {
            String username = (String) test[0];
            String password = (String) test[1];
            String expected = (String) test[2];

            log.info("Running login test case: username={}, expected={}", username, expected);
            homePage.navigateTo();
            LoginPage loginPage = homePage.navigateToLogin();
            loginPage.login(username, password);

            if (expected.equals("Đăng nhập thành công!")) {
                boolean isSuccessful = loginPage.isLoginSuccessful();
                Assertions.assertTrue(isSuccessful, "❌ Expected successful login, but login failed for username=" + username);
                log.info("✅ Successful login verified for username={}", username);
            } else {
                String actualErrorMessage = loginPage.getErrorMessage();
                loginPage.verifyErrorMessage(expected, actualErrorMessage);
            }

            log.info("Current URL after login: {}", driver.getCurrentUrl());
            Thread.sleep(3000);
        }
    }

    private void testUploadForm() throws Exception {
        // Initialize uploadPage only when needed
        uploadPage = homePage.navigateToUpload();
        
        Object[][] testCases = {
                {"", 1, Paths.get(BASE_DIR, "document.pdf").toString(), "No title test", "Vui lòng điền đầy đủ thông tin."},
                {"No Category", null, Paths.get(BASE_DIR, "document.pdf").toString(), "No category test", "Vui lòng điền đầy đủ thông tin."},
                {"@#$%^&*Title", 2, Paths.get(BASE_DIR, "document2.pdf").toString(), "Special characters in title", "Vui lòng nhập tiêu đề hợp lệ."},
                {"a".repeat(300), 1, Paths.get(BASE_DIR, "document2.pdf").toString(), "Long title test", "Vui lòng nhập tiêu đề hợp lệ."},
                // {"No File", 2, "", "No file selected", "Vui lòng điền đầy đủ thông tin."},
                {"Invalid File", 1, Paths.get(BASE_DIR, "toolbox-installer.exe").toString(), "", "Vui lòng điền đầy đủ thông tin."},
                {"Invalid Image File", 4, Paths.get(BASE_DIR, "image.jpg").toString(), "Image file test", "Vui lòng điền đầy đủ thông tin."},
                {"Invalid Word File", 2, Paths.get(BASE_DIR, "document.docx").toString(), "Word file test", "Vui lòng điền đầy đủ thông tin."},
                {"No Extension", 3, Paths.get(BASE_DIR, "file_without_extension").toString(), "No extension test", "Vui lòng điền đầy đủ thông tin."},
                {"Valid PDF", 1, Paths.get(BASE_DIR, "document1.pdf").toString(), "Test upload PDF", "Tài liệu đã được upload thành công!"},
                {"Uppercase Extension", 3, Paths.get(BASE_DIR, "document3.pdf").toString(), "Uppercase extension test", "Tài liệu đã được upload thành công!"}
        };

        for (Object[] test : testCases) {
            String title = (String) test[0];
            Integer category = (Integer) test[1];
            String filename = (String) test[2];
            String description = (String) test[3];
            String expectedError = (String) test[4];

            log.info("Running upload test case: title={}, filename={}", title, filename);
            homePage.navigateToUpload();
            uploadPage.uploadFile(title, category, filename, description);

            String actualMessage = uploadPage.getErrorMessage();
            if (actualMessage.isEmpty()) {
                actualMessage = uploadPage.getModalMessage();
            }

            if (uploadPage.isTitleRequired() && title.isEmpty()) {
                uploadPage.verifyMessage(expectedError, actualMessage, false);
                continue;
            }

            if (expectedError.equals("Vui lòng nhập tiêu đề hợp lệ.")) {
                uploadPage.verifyMessage(expectedError, actualMessage, false);
                continue;
            }

            if (category == null) {
                uploadPage.verifyMessage(expectedError, actualMessage, false);
                continue;
            }

            if (filename.isEmpty()) {
                uploadPage.verifyMessage(expectedError, actualMessage, false);
                continue;
            }

            uploadPage.verifyMessage(expectedError, actualMessage, expectedError.contains("thành công"));
            uploadPage.closeModal();
            Thread.sleep(3000);
        }
    }

    @Test
    public void runTests() throws Exception {
        log.info("Starting test: Login and Upload Forms");
        try {
            // Run login tests
            log.info("Running login tests");
            testLoginForm();

            // Force login for upload tests
//            log.info("Forcing login for upload tests");
//            homePage.navigateTo();
//            LoginPage loginPage = homePage.navigateToLogin();
//            loginPage.login("Long", "123"); // Replace with valid credentials if needed
            if (!loginPage.isLoginSuccessful()) {
                throw new RuntimeException("Failed to log in for upload tests");
            }

            // Run upload tests
            log.info("Navigating to upload page");
            homePage.navigateToUpload();
            testUploadForm();

            log.info("Test completed");
        } catch (Exception e) {
            log.error("Test failed: {}", e.getMessage());
            throw e;
        }
    }
}