import { By, Builder, until, Select, Browser } from 'selenium-webdriver';
import assert from 'assert';
import path from 'path';
import { fileURLToPath } from 'url';
import { Options } from 'selenium-webdriver/chrome.js';

const BASE_URL = 'http://localhost:5173/';
const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const baseDir = path.resolve(__dirname, '../public/file_test');

/**
 * Function to clear and input data into a field
 */
async function inputField(element, data) {
    await element.clear();
    await element.sendKeys(data);
}

/**
 * Function to check if an element has the required attribute
 */
async function isRequired(element) {
    try {
        let required = await element.getAttribute("required");
        return required !== null;
    } catch (error) {
        console.error(`⚠️ Error checking required attribute: ${error.message}`);
        return false;
    }
}

/**
 * Kiểm thử toàn bộ form đăng nhập
 */
async function testLoginForm(driver) {
    try {
        const user = await driver.findElementById(By.xpath('//*[@id="userDropdown"]/text()'))
        console.log(`User already logged in!`);

        return user != null;
    } catch (error) {
        console.log("User is not logged in!");
    }

    // Test cases gồm cả username & password
    const testCases = [
        // { username: "", password: "", expectedError: "Yêu cầu nhập tên đăng nhập và mật khẩu." },
        // { username: "", password: "wrongpass", expectedError: "Yêu cầu nhập tên đăng nhập và mật khẩu." },
        // { username: "user@#$%", password: "", expectedError: "Yêu cầu nhập tên đăng nhập và mật khẩu." },
        { username: "user@#$%", password: "short", expectedError: "Tên đăng nhập hoặc mật khẩu không đúng" },
        { username: "admin", password: "wrongpass", expectedError: "Tên đăng nhập hoặc mật khẩu không đúng" },
        { username: "Khang", password: "123", expected: "Đăng nhập thành công!" }
    ];

    // Chờ đến trang đăng nhập
    await driver.wait(until.elementLocated(
        By.xpath('//*[@id="app"]/div/div[1]/div/div/div[3]/div/div[1]/li/a[2]/button')),
        5000
    ).click();

    for (let test of testCases) {
        console.log(`🔄 Testing: Username = "${test.username}", Password = "${test.password}"`);

        let usernameInput = await driver.wait(until.elementLocated(By.id("username")), 5000);
        let passwordInput = await driver.findElement(By.id("password"));
        let loginButton = await driver.findElement(By.xpath('//button[@type="submit"]'));

        await driver.sleep(1000);
        await inputField(usernameInput, test.username);
        await driver.sleep(500);
        await inputField(passwordInput, test.password);
        await loginButton.click();

        await driver.sleep(500); // Chờ xử lý validation

        // if (await isRequired(usernameInput) && test.username === '') {
        //     assert.strictEqual(actualErrorMessage, test.expectedError, `❌ Expected "${test.expectedError}", but got "${actualErrorMessage}"`);
        //     console.log(`✅ Not Passed: ${actualErrorMessage}`);
        //     continue;
        // }

        // if (await isRequired(passwordInput) && test.password === '') {
        //     assert.strictEqual(actualErrorMessage, test.expectedError, `❌ Expected "${test.expectedError}", but got "${actualErrorMessage}"`);
        //     console.log(`✅ Not Passed: ${actualErrorMessage}`);
        //     continue;
        // }

        try {
            let errorMessageElement = await driver.findElement(By.css(".alert"));
            let actualErrorMessage = await errorMessageElement.getText();

            let errorElement = await driver.wait(
                until.elementLocated(
                    By.xpath('/html/body/div/div/div[3]/div/form/div[4]'),
                    3000
                )
            );
            let actualMessage = await errorElement.getText();

            if (test.expectedError) {
                assert.strictEqual(actualErrorMessage, test.expectedError, `❌ Expected "${test.expectedError}", but got "${actualErrorMessage}"`);
                console.log(`✅ Not Passed: ${actualErrorMessage}`);
            } else {
                assert.strictEqual(actualErrorMessage, test.expected, `❌ Expected "${test.expected}", but got "${actualErrorMessage}"`);
                console.log(`✅ Passed: ${actualErrorMessage}`);
            }
        } catch (error) {
            console.error(`❌ Failed: ${error.message}`);
        }
    }

    return true;
}

async function testUploadForm(driver) {
    // Chờ đến trang upload
    await driver.wait(until.elementLocated(
        By.xpath('//*[@id="app"]/div/div[1]/div/div/div[3]/div/div[1]/li/a/button')),
        10000
    ).click();

    // Test cases
    const testCases = [
        // Missing title
        {
            title: "", category: 1,
            filename: path.join(baseDir, "document.pdf"), description: "No title test",
            expectedError: "Vui lòng điền đầy đủ thông tin."
        },

        // Missing category
        {
            title: "No Category", category: null,
            filename: path.join(baseDir, "document.pdf"), description: "No category test",
            expectedError: "Vui lòng điền đầy đủ thông tin."
        },

        // Missing file
        {
            title: "No File", category: 2, filename: "",
            description: "No file selected",
            expectedError: "Vui lòng điền đầy đủ thông tin."
        },

        // Special characters in title
        {
            title: "@#$%^&*Title", category: 2,
            filename: path.join(baseDir, "document2.pdf"),
            description: "Special characters in title", expectedError: "Vui lòng nhập tiêu đề hợp lệ."
        },

        // Title too long
        {
            title: "a".repeat(300), category: 1, filename: path.join(baseDir, "document2.pdf"),
            description: "Long title test", expectedError: "Vui lòng nhập tiêu đề hợp lệ."
        },

        // File in an unsupported format (e.g., .exe)
        {
            title: "Invalid File", category: 1,
            filename: path.join(baseDir, "toolbox-installer.exe"),
            description: "", expectedError: "Chỉ cho phép upload file PDF."
        },

        // File in an unsupported format (e.g., .jpg)
        {
            title: "Invalid Image File", category: 4,
            filename: path.join(baseDir, "image.jpg"),
            description: "Image file test", expectedError: "Chỉ cho phép upload file PDF."
        },

        // File in an unsupported format (e.g., .docx)
        {
            title: "Invalid Word File", category: 2,
            filename: path.join(baseDir, "document.docx"),
            description: "Word file test", expectedError: "Chỉ cho phép upload file PDF."
        },

        // File without an extension
        {
            title: "No Extension", category: 3,
            filename: path.join(baseDir, "file_without_extension"),
            description: "No extension test", expectedError: "Chỉ cho phép upload file PDF."
        },

        {
            title: "Valid PDF", category: 1,
            filename: path.join(baseDir, "document1.pdf"),
            description: "Test upload PDF", expectedError: "Tài liệu đã được upload thành công!"
        },

        // File with uppercase extension
        {
            title: "Uppercase Extension", category: 3,
            filename: path.join(baseDir, "document3.pdf"),
            description: "Uppercase extension test", expectedError: "Tài liệu đã được upload thành công!"
        },
    ];
    // Locate form elements  
    let titleInput = await driver.wait(until.elementLocated(By.id("title")), 5000);
    let categoryDropdown = await driver.findElement(By.id("categories"));
    let fileInput = await driver.findElement(By.id("formFile"));
    let descriptionInput = await driver.findElement(By.xpath('//*[@id="app-content"]/div/div[2]/div/form/div/div[4]/textarea'));
    let submitButton = await driver.findElement(By.xpath('//*[@id="app-content"]/div/div[2]/div/form/div/div[5]/button'));

    for (let test of testCases) {
        await driver.sleep(2000);
        console.log(`🔄 Testing file: "${test.filename}"`);

        // Cuộn đến phần tử để đảm bảo nó nằm trong khung nhìn
        await driver.executeScript("arguments[0].scrollIntoView(true);", submitButton);

        await driver.sleep(500);
        await titleInput.clear();
        await descriptionInput.clear();

        // Nhập thông tin
        await titleInput.sendKeys(test.title);
        // Nhập mô tả  
        await driver.executeScript("arguments[0].value = '';", descriptionInput);
        await descriptionInput.sendKeys(test.description);
        // Chọn danh mục  
        await driver.executeScript("arguments[0].selectedIndex = 0;", categoryDropdown);
        if (test.category !== null && test.category !== undefined) {
            let categoryOption = await categoryDropdown.findElement(By.css(`option[value='${test.category}']`));
            await categoryOption.click();
        }

        // Xử lý upload file  
        await driver.executeScript("arguments[0].value = '';", fileInput);

        if (test.filename) {
            await fileInput.sendKeys(test.filename);
            try {
                let alert = await driver.switchTo().alert();
                let alertText = await alert.getText();
                console.log("⚠️ Alert xuất hiện");
                assert.strictEqual(alertText, test.expectedError, `❌ Expected "${test.expectedError}", but got "${alertText}"`);
                await alert.accept();
                console.log("✅ Đã đóng alert.");
                console.log(`✅ Not Passed: ${test.expectedError}`);
            } catch (error) {
            }
        } else {
            console.log("⚠️ No file selected, skipping file input.");
        }

        await driver.sleep(1000);

        // Nhấn submit
        await submitButton.click();
        await driver.sleep(1000); // Chờ xác nhận

        let messageElement = await driver.findElement(By.css(".alert"));
        let actualErrorMessage = await messageElement.getText();

        if (await isRequired(titleInput) && test.title === '') {
            assert.strictEqual(actualErrorMessage, test.expectedError, `❌ Expected "${test.expectedError}", but got "${actualErrorMessage}"`);
            console.log("✅ Not Passed: Tiêu đề không được để trống.");
            continue;
        }

        if (test.expectedError === "Vui lòng nhập tiêu đề hợp lệ.") {
            assert.strictEqual(actualErrorMessage, test.expectedError, `❌ Expected "${test.expectedError}", but got "${actualErrorMessage}"`);
            console.log("✅ Not Passed: Tiêu đề không hợp lệ.");
            continue;
        }

        if (test.category === null || test.category === undefined) {
            assert.strictEqual(actualErrorMessage, test.expectedError, `❌ Expected "${test.expectedError}", but got "${actualErrorMessage}"`);
            console.log("✅ Not Passed: Vui lòng chọn danh mục.");
            continue;
        }

        if (test.filename === undefined || test.filename === '' || test.filename === null) {
            assert.strictEqual(actualErrorMessage, test.expectedError, `❌ Expected "${test.expectedError}", but got "${actualErrorMessage}"`);
            console.log("✅ Not Passed: Vui lòng chọn file để upload.");
            continue;
        }

        // Kiểm tra thông báo thành công
        try {
            let successMessage = await driver.wait(
                until.elementLocated(By.css(".modal-content p")),
                5000
            );
            let successText = await successMessage.getText();

            // Kiểm tra nội dung thông báo
            assert.strictEqual(successText, test.expectedError);
            console.log(`✅ Passed: ${test.expectedError}`);

            // Tìm và đóng modal
            let modalButtons = await driver.findElements(By.xpath('//*[@id="app-content"]/div/div[3]/div/div/button'));

            if (modalButtons.length > 0 && await modalButtons[0].isDisplayed()) {
                // Nếu là testcase cuối cùng, trở về trang Home
                if (testCases.indexOf(test) === testCases.length - 1) {
                    let homeButton = await driver.findElements(By.xpath('//*[@id="app-content"]/div/div[3]/div/div/a/button'));
                    if (homeButton.length > 0) await homeButton[0].click();
                }

                await modalButtons[0].click();
            }
        } catch (error) {
            console.error(`❌ Failed: ${error.message}`);
        }

        await driver.sleep(3000);
    }
}

/**
 * Hàm chạy chính
 */
async function main() {
    let driver, isLoggedIn;
    try {
        driver = await new Builder()
            .forBrowser(Browser.FIREFOX)
            .build();
        await driver.get(BASE_URL);
        await driver.manage().window().maximize();

        // Kiểm thử form đăng nhập
        isLoggedIn = await testLoginForm(driver);
    } catch (e) {
        console.error("❌ Test failed:", e);
    }

    await driver.sleep(5000);

    if (isLoggedIn) {
        console.log("🔄 Test upload document!");
        await testUploadForm(driver);
    }
}

// Chạy test
main();