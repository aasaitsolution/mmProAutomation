package Home;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Register {

    @Test
    public void register() {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize(); // Maximize window for better visibility
        driver.get("http://localhost:5173/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Increased timeout

        try {
            // Navigate to sign-in
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/signin'] button")
            ));
            loginButton.click();

            // Click sign-in with email
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#sign-in > div:nth-child(6) > div > div > div > div > button > span")
            ));
            signInButton.click();

            // Select actor type
            WebElement actorButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("body > div:nth-child(4) > div > div.ant-modal-wrap.ant-modal-centered > div > div:nth-child(1) > div > div.ant-modal-body > div > button:nth-child(1)")
            ));
            actorButton.click();

            // Wait for form and ensure it's interactive
            WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("form.ant-form")
            ));

            // Add small delay to ensure all form elements are ready
            Thread.sleep(1000);

            // Fill form with explicit waits for each field
            fillField(driver, wait, "firstName", "John");
            fillField(driver, wait, "lastName", "Doe");
            fillField(driver, wait, "username", "johndoe" + System.currentTimeMillis());
            fillField(driver, wait, "email", "john.doe" + System.currentTimeMillis() + "@example.com");
            fillField(driver, wait, "nic", "123456789V");
            fillField(driver, wait, "mobile", "0712345678");
            fillField(driver, wait, "designation", "Software Engineer");
            fillField(driver, wait, "password", "Password@123");
            fillField(driver, wait, "confirmPassword", "Password@123");

            // Handle file uploads - use actual file paths
            String projectPath = System.getProperty("user.dir");
            String nicFrontPath = projectPath + "/src/test/resources/test_images/nic_front.jpg";
            String nicBackPath = projectPath + "/src/test/resources/test_images/nic_back.jpg";
            String workIdPath = projectPath + "/src/test/resources/test_images/work_id.jpg";

            WebElement nicFront = driver.findElement(By.id("nicFront"));
            nicFront.sendKeys(nicFrontPath);

            WebElement nicBack = driver.findElement(By.id("nicBack"));
            nicBack.sendKeys(nicBackPath);

            WebElement workId = driver.findElement(By.id("workId"));
            workId.sendKeys(workIdPath);

//            // Create dummy files if they don't exist (for testing)
//            createDummyFileIfNotExists(nicFrontPath);
//            createDummyFileIfNotExists(nicBackPath);
//            createDummyFileIfNotExists(workIdPath);
//
//            fillField(driver, wait, "nicFront", nicFrontPath);
//            fillField(driver, wait, "nicBack", nicBackPath);
//            fillField(driver, wait, "workId", workIdPath);

            // Scroll to submit button and click
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit']")
            ));
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
            submitButton.click();

            // Wait for either success or validation errors
            try {
                // Wait for success condition
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.urlContains("success"),
                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-notification-notice-success"))
                ));
                System.out.println("Registration successful!");
            } catch (Exception e) {
                // Check for validation errors
                List<WebElement> errors = driver.findElements(By.cssSelector(".ant-form-item-explain-error"));
                if (!errors.isEmpty()) {
                    System.out.println("Validation errors found:");
                    errors.forEach(error -> System.out.println(error.getText()));
                } else {
                    System.out.println("Unknown registration issue");
                }
                // Take screenshot for debugging
                File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(screenshot, new File("registration_error.png"));
            }

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            driver.quit();
        }
    }

    private void fillField(WebDriver driver, WebDriverWait wait, String id, String value) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        element.clear();
        element.sendKeys(value);
        // Trigger change event if needed
        ((JavascriptExecutor)driver).executeScript("arguments[0].dispatchEvent(new Event('change'))", element);
    }

    private void createDummyFileIfNotExists(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }
}