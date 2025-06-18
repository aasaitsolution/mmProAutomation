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
        driver.manage().window().maximize(); 
        driver.get("http://localhost:5173/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Increased timeout

        String[] roles = {"GSMB Officer", "Police", "Mining License Owner", "Mining Engineer"};


        try {
            for (int i = 0; i < roles.length; i++) {
                System.out.println("Attempting to register as: " + roles[i]);

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
            // WebElement actorButton = wait.until(ExpectedConditions.elementToBeClickable(
            //         By.cssSelector("body > div:nth-child(4) > div > div.ant-modal-wrap.ant-modal-centered > div > div:nth-child(1) > div > div.ant-modal-body > div > button:nth-child(1)")
            // ));
            // actorButton.click();
            // Wait for actor type modal

            List<WebElement> actorButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.cssSelector(".ant-modal-body > div > button")
            ));

            if (i >= actorButtons.size()) {
                System.out.println("No actor button found for role index: " + i);
                continue;
            }

            actorButtons.get(i).click();



            // Wait for form and ensure it's interactive
            WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("form.ant-form")
            ));

            // Add small delay to ensure all form elements are ready
            Thread.sleep(1000);

            // Fill form with explicit waits for each field
            fillFieldIfPresent(driver, wait, "firstName", "John");
            fillFieldIfPresent(driver, wait, "lastName", "Doe");
            fillFieldIfPresent(driver, wait, "username", "johndoe" + System.currentTimeMillis());
            fillFieldIfPresent(driver, wait, "email", "john.doe" + System.currentTimeMillis() + "@example.com");
            fillFieldIfPresent(driver, wait, "nic", "123456789V");
            fillFieldIfPresent(driver, wait, "mobile", "0712345678");
            fillFieldIfPresent(driver, wait, "password", "Password@123");
            fillFieldIfPresent(driver, wait, "confirmPassword", "Password@123");

            // Fill designation ONLY if NOT Mining License Owner (index 2)
            if (i != 2) {
                    fillFieldIfPresent(driver, wait, "designation", roles[i] + " Tester");
                } else {
                    System.out.println("Skipping designation for role: " + roles[i]);
                }

                // Handle file uploads ONLY if NOT Mining License Owner (index 2)
                if (i != 2) {
                    System.out.println("File upload fields present for role: " + roles[i]);

            // Handle file uploads - use actual file paths
                String projectPath = System.getProperty("user.dir");
                String nicFrontPath = projectPath + "/src/test/resources/test_images/nic_front.jpg";
                String nicBackPath = projectPath + "/src/test/resources/test_images/nic_back.jpg";
                String workIdPath = projectPath + "/src/test/resources/test_images/work_id.jpg";

                try {
                        WebElement nicFront = driver.findElement(By.id("nicFront"));
                        nicFront.sendKeys(nicFrontPath);
                    } catch (NoSuchElementException e) {
                        System.out.println("NIC Front upload field not found");
                    }

                    try {
                        WebElement nicBack = driver.findElement(By.id("nicBack"));
                        nicBack.sendKeys(nicBackPath);
                    } catch (NoSuchElementException e) {
                        System.out.println("NIC Back upload field not found");
                    }

                    try {
                        WebElement workId = driver.findElement(By.id("workId"));
                        workId.sendKeys(workIdPath);
                    } catch (NoSuchElementException e) {
                        System.out.println("Work ID upload field not found");
                    }
                } else {
                    System.out.println("Skipping file uploads for role: " + roles[i]);
                }

//            // Create dummy files if they don't exist (for testing)
//            createDummyFileIfNotExists(nicFrontPath);
//            createDummyFileIfNotExists(nicBackPath);
//            createDummyFileIfNotExists(workIdPath);
//
//            fillFieldIfPresent(driver, wait, "nicFront", nicFrontPath);
//            fillFieldIfPresent(driver, wait, "nicBack", nicBackPath);
//            fillFieldIfPresent(driver, wait, "workId", workIdPath);

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
                System.out.println("Registration successful for role: " + roles[i]);
            } catch (Exception e) {
                // Check for validation errors
                List<WebElement> errors = driver.findElements(By.cssSelector(".ant-form-item-explain-error"));
                if (!errors.isEmpty()) {
                    System.out.println("Validation errors for " + roles + ":");
                    for (WebElement error : errors) {
                            System.out.println("- " + error.getText());
                        }
                } else {
                    System.out.println("Unknown error occurred for role: " + roles[i]);
                
                // Take screenshot for debugging
                File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(screenshot, new File("registration_error_" + roles[i] + ".png"));
            }
        }
        // Optionally, navigate back or refresh page to start next role registration cleanly
                driver.get("http://localhost:5173/");
                Thread.sleep(2000); // small wait before next iteration
            }

        } catch (Exception e) {
            System.out.println("Exception during registration: " + e.getMessage());
            e.printStackTrace();
          } finally {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {}
            driver.quit();
        }
}

    private void fillFieldIfPresent(WebDriver driver, WebDriverWait wait, String id, String value) {
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