package GSMBOfficer;

import io.opentelemetry.sdk.metrics.internal.state.PooledHashMap;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class AddMiningLicense {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        driver = new ChromeDriver(options);
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

            // Find and click the "+" button in the ML Owner table
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div/div/table/tbody/tr[1]/td[7]/div/a/button/span")));
            addButton.click();

            // Wait for form to load
            Thread.sleep(500);

            // Fill out license details
            fillLicenseForm();

            uploadFiles();

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

    private void fillLicenseForm() throws InterruptedException {
        // Wait for license number field and enter value
        WebElement explorationlicenseField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"exploration_nb\"]")
        ));
        explorationlicenseField.clear();
        explorationlicenseField.sendKeys("ABC1234");

        WebElement landNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"land_name\"]")
        ));
        landNameField.clear();
        landNameField.sendKeys("No 23/1" + System.currentTimeMillis() % 10000);

        WebElement googleLocationField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"land_google\"]")
        ));
        googleLocationField.clear();
        googleLocationField.sendKeys("http://maps.google.com/?q=No+23/1+Netolpitiya,+Tangalle,+Sri+Lanka");

        WebElement landOwnerNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"land_owner_name\"]")
        ));
        landOwnerNameField.clear();
        landOwnerNameField.sendKeys("Kasun Perera");

        WebElement villageNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"village_name\"]")
        ));
        villageNameField.clear();
        villageNameField.sendKeys("Netolpitiya");

        WebElement gramaNiladhariField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"grama_niladari\"]")
        ));
        gramaNiladhariField.clear();
        gramaNiladhariField.sendKeys("Netolpitiya" );

// Click on the district dropdown to open it
        WebElement districtDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select-selector') and .//input[@id='district']]")
        ));
        districtDropdown.click();

// Wait for dropdown options to appear and select one
        WebElement districtOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select-item') and contains(text(), 'Hambantota')]")
        ));
        districtOption.click();

// Click on the district dropdown to open it
        WebElement divishionalSecretaryDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select-selector') and .//input[@id='divisional_secretary_division']]")
        ));
        divishionalSecretaryDropdown.click();

// Wait for dropdown options to appear and select one
        WebElement divishionalSecretaryOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select-item') and contains(text(), 'Tangalle')]")
        ));
        divishionalSecretaryOption.click();

        WebElement licenseNumberField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"mining_license_number\"]")
        ));
        licenseNumberField.clear();
        licenseNumberField.sendKeys("PQR1234");


// Click on the date picker input field to open calendar
        WebElement datePickerInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='start_date']")
        ));
        datePickerInput.click();

// Wait for the calendar panel to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("ant-picker-panel-container")
        ));

        Thread.sleep(300);
// Example: Select June 15, 2024
        WebElement dateToSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//td[@title='2025-06-15']/div[@class='ant-picker-cell-inner']")
        ));
        dateToSelect.click();

// Verify the selected date appears in the input field
        WebElement selectedDate = driver.findElement(By.id("start_date"));
        String selectedValue = selectedDate.getAttribute("value");
        Assert.assertNotNull(selectedValue);
        Assert.assertFalse(selectedValue.isEmpty());
        System.out.println("Selected date: " + selectedValue);

// Set end date directly via JavaScript
        WebElement endDateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("end_date")));
        ((JavascriptExecutor) driver).executeScript(
    "const el = arguments[0];" +
    "const value = '2025-06-25';" +
    "const nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
    "nativeInputValueSetter.call(el, value);" +
    "el.dispatchEvent(new Event('input', { bubbles: true }));" +
    "el.dispatchEvent(new Event('change', { bubbles: true }));",
    endDateInput
);


        WebElement fullCapacityField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"full_capacity\"]")
        ));
        fullCapacityField.clear();
        fullCapacityField.sendKeys("1000");

        WebElement monthlyCapacityField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"monthly_capacity\"]")
        ));
        monthlyCapacityField.clear();
        monthlyCapacityField.sendKeys("80");

        WebElement usedCapacityField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"used\"]")
        ));
        usedCapacityField.clear();
        usedCapacityField.sendKeys("10");

        WebElement remainingCapacityField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"remaining\"]")
        ));
        remainingCapacityField.clear();
        remainingCapacityField.sendKeys("10");

    }

    private void clickCreateButton() {
    try {
        WebElement createButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/form/div[13]/div/div/div/div/button")
        ));

        System.out.println("➡ Clicking the 'Create' button...");

        // Try regular click first
        try {
            createButton.click();
        } catch (ElementClickInterceptedException e) {
            System.out.println("⚠ Regular click failed, using JavaScript click...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", createButton);
        }

        System.out.println("✅ Clicked. Waiting to observe what happens...");

        // Wait for either success popup or error message
        Thread.sleep(3000); // Adjust as needed

        // Print current URL
        System.out.println("🔍 Current URL after submit: " + driver.getCurrentUrl());

        // Check DOM changes
        List<WebElement> toasts = driver.findElements(By.xpath("//div[contains(@class, 'ant-notification') or contains(@class, 'success') or contains(@class, 'ant-message-success')]"));
        if (!toasts.isEmpty()) {
            for (WebElement toast : toasts) {
                System.out.println("✅ Success popup: " + toast.getText());
            }
        } else {
            System.out.println("❌ No success popup found.");
        }

        // Optional: Look for errors
        List<WebElement> errors = driver.findElements(By.xpath("//div[contains(@class, 'error') or contains(@class, 'alert')]"));
        for (WebElement err : errors) {
            System.out.println("⚠ Error message found: " + err.getText());
        }

    } catch (Exception e) {
        System.out.println("❌ Failed to click Create button: " + e.getMessage());
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
            WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.xpath("//div[contains(@class, 'success') or contains(@class, 'ant-notification') or contains(@class, 'ant-message-success')]")
));
System.out.println("Success message appeared: " + successMessage.getText());
Assert.assertTrue(successMessage.isDisplayed(), "Success message is not visible.");

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

    // Replace the file upload section in your second code with this improved version:

    private void uploadFiles() throws InterruptedException {
        // Define file paths
        String projectPath = System.getProperty("user.dir");
        String deedPlanPath = projectPath + "/src/test/resources/test_images/deedPlan.pdf";
        String minePlanPath = projectPath + "/src/test/resources/test_images/minePlan.pdf";
        String viabilityReportPath = projectPath + "/src/test/resources/test_images/viabilityReport.pdf";
        String boundarySurveyPath = projectPath + "/src/test/resources/test_images/boundarySurvey.pdf";

        // Create dummy files if they don't exist (for testing)
        createDummyFileIfNotExists(deedPlanPath);
        createDummyFileIfNotExists(minePlanPath);
        createDummyFileIfNotExists(viabilityReportPath);
        createDummyFileIfNotExists(boundarySurveyPath);

        try {
            // Upload Deed Plan
            WebElement deedPlan = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("Deed_plan")));
            if (!deedPlan.isDisplayed()) {
                // If element is not visible, try to make it visible using JavaScript
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'block';", deedPlan);
            }
            deedPlan.sendKeys(deedPlanPath);
            System.out.println("Deed plan uploaded successfully");

            // Upload Mine Plan
            WebElement minePlan = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("detailed_mine_plan")));
            if (!minePlan.isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'block';", minePlan);
            }
            minePlan.sendKeys(minePlanPath);
            System.out.println("Mine plan uploaded successfully");

            // Upload Viability Report
            WebElement viabilityReport = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("economic_viability_report")));
            if (!viabilityReport.isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'block';", viabilityReport);
            }
            viabilityReport.sendKeys(viabilityReportPath);
            System.out.println("Viability report uploaded successfully");

            // Upload Boundary Survey
            WebElement boundarySurvey = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("license_boundary_survey")));
            if (!boundarySurvey.isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'block';", boundarySurvey);
            }
            boundarySurvey.sendKeys(boundarySurveyPath);
            System.out.println("Boundary survey uploaded successfully");

            // Wait a moment for uploads to process
            Thread.sleep(1000);

        } catch (Exception e) {
            System.out.println("File upload failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Add this helper method to create dummy files for testing
    private void createDummyFileIfNotExists(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                // Create a simple PDF-like file for testing
                java.nio.file.Files.write(file.toPath(), "Dummy PDF content for testing".getBytes());
                System.out.println("Created dummy file: " + path);
            }
        } catch (Exception e) {
            System.out.println("Failed to create dummy file: " + path);
            e.printStackTrace();
        }
    }

    // Alternative approach if the above doesn't work - using JavaScript to set file input values
    private void uploadFilesUsingJavaScript() {
        String projectPath = System.getProperty("user.dir");

        // This approach uses JavaScript to trigger file upload
        String script =
                "var fileInput = arguments[0];" +
                        "var filePath = arguments[1];" +
                        "fileInput.style.display = 'block';" +
                        "fileInput.style.visibility = 'visible';" +
                        "fileInput.style.opacity = '1';" +
                        "fileInput.removeAttribute('hidden');";

        try {
            // Upload each file using JavaScript to make elements visible first
            WebElement deedPlan = driver.findElement(By.id("Deed_plan"));
            ((JavascriptExecutor) driver).executeScript(script, deedPlan, "");
            deedPlan.sendKeys(projectPath + "/src/test/resources/test_images/deedPlan.pdf");

            WebElement minePlan = driver.findElement(By.id("detailed_mine_plan"));
            ((JavascriptExecutor) driver).executeScript(script, minePlan, "");
            minePlan.sendKeys(projectPath + "/src/test/resources/test_images/minePlan.pdf");

            WebElement viabilityReport = driver.findElement(By.id("economic_viability_report"));
            ((JavascriptExecutor) driver).executeScript(script, viabilityReport, "");
            viabilityReport.sendKeys(projectPath + "/src/test/resources/test_images/viabilityReport.pdf");

            WebElement boundarySurvey = driver.findElement(By.id("license_boundary_survey"));
            ((JavascriptExecutor) driver).executeScript(script, boundarySurvey, "");
            boundarySurvey.sendKeys(projectPath + "/src/test/resources/test_images/boundarySurvey.pdf");

        } catch (Exception e) {
            System.out.println("JavaScript file upload failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
                 try {
            // Wait 3 seconds before quitting so you can observe the result
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //    driver.quit();
        }
    }
}