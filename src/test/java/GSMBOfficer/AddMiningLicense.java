package GSMBOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class AddMiningLicense {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(100)); // Increased timeout
    }

    @Test
    public void testAddMLOwnerLicense() throws InterruptedException {
        try {
            // Login
            performLogin();

            // Wait for dashboard to load completely
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));

            // Add a small delay to ensure page is fully loaded
            Thread.sleep(200);

            // Navigate to ML Owner tab (using most reliable method)
            WebElement mlOwnerTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='rc-tabs-1-tab-MLOWNER']")));
            mlOwnerTab.click();

            // Add a small wait after clicking tab
            Thread.sleep(500);

            // Find and click the "+" button in the ML Owner table
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div/div/table/tbody/tr[1]/td[8]/a/button")));
            addButton.click();

            // Wait for form to load
            Thread.sleep(500);

            // Fill out license details
            fillLicenseForm();

            // Click Create button with more robust handling
            clickCreateButton();

            // Verify successful creation
            verifyLicenseCreation();

        } catch (Exception e) {
            System.out.println("Test failed! Final URL: " + driver.getCurrentUrl());
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void fillLicenseForm() {
        // Wait for license number field and enter value
        WebElement licenseNumberField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='licenseNumber']")
        ));
        licenseNumberField.clear();
        licenseNumberField.sendKeys("LA" + System.currentTimeMillis() % 10000);

        WebElement capacityField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='capacity']")
        ));
        capacityField.clear();
        capacityField.sendKeys("1000");

        WebElement locationField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='location']")
        ));
        locationField.clear();
        locationField.sendKeys("Madampe");

        WebElement validityStartField = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='validityStart']")
        ));
        validityStartField.clear();
        validityStartField.sendKeys("06/03/2025");

        WebElement validityEndField = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='endDate']")
        ));
        validityEndField.clear();
        validityEndField.sendKeys("31/03/2025");
    }

    private void clickCreateButton() {
        try {
            // Try multiple approaches to click Create button
            WebElement createButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/form/div/div[9]/div/div/div/div/div/div/button[1]")
            ));

            // Try regular click first
            try {
                createButton.click();
            } catch (ElementClickInterceptedException e) {
                // If regular click fails, use JavaScript click
                JavascriptExecutor executor = (JavascriptExecutor) driver;
                executor.executeScript("arguments[0].click();", createButton);
            }

            // Wait for potential loading or navigation
            Thread.sleep(1000);

            // Check for any error messages
            try {
                WebElement errorMessage = driver.findElement(By.xpath("//div[contains(@class, 'error') or contains(@class, 'alert')]"));
                throw new RuntimeException("Error occurred: " + errorMessage.getText());
            } catch (NoSuchElementException e) {
                // No error message found, continue
            }
        } catch (Exception e) {
            System.out.println("Failed to click Create button: " + e.getMessage());
            throw new RuntimeException("Unable to click Create button", e);
        }
    }

    private void verifyLicenseCreation() {
        // More flexible navigation check with multiple possible success scenarios
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/gsmb/dashboard"),
                ExpectedConditions.urlContains("/gsmb/licenses"),
                ExpectedConditions.urlContains("/gsmb/add-new-license")
        ));

        // Verify successful navigation or presence of success message
        String finalUrl = driver.getCurrentUrl();
        boolean navigationSuccessful = finalUrl.contains("/gsmb/dashboard") ||
                finalUrl.contains("/gsmb/licenses") ||
                finalUrl.contains("/gsmb/add-new-license");

        try {
            // Optional: Check for a success message or toast notification
            WebElement successMessage = driver.findElement(By.xpath("//div[contains(@class, 'success') or contains(@class, 'notification')]"));
            navigationSuccessful = true;
        } catch (NoSuchElementException e) {
            // No explicit success message found, rely on URL check
        }

        Assert.assertTrue(navigationSuccessful,
                "Failed to navigate to expected page or confirm license creation. Current URL: " + finalUrl);
    }

    private void performLogin() {
        driver.get(BASE_URL + "/signin/");

        WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
        username.sendKeys("nimal");

        WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
        password.sendKeys("12345678");

        WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".ant-btn-primary")
        ));
        signinButton.click();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}