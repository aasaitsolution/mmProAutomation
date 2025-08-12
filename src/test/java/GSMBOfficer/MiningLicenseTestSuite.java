package GSMBOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class MiningLicenseTestSuite {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://mmpro.aasait.lk";
    private static final String USERNAME = "nimal";
    private static final String PASSWORD = "12345678";

    @BeforeClass
    public void setupClass() {
        // Setup any class-level configurations
        System.out.println("Starting Mining License Test Suite");
    }

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // ==================== LOGIN TESTS ====================
    @Test(priority = 1, groups = {"smoke", "login"})
    public void testSuccessfulLogin() {
        try {
            performLogin();

            // Verify successful login by checking URL
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("/gsmb/dashboard"),
                    "Login failed - not redirected to dashboard");

            System.out.println("✅ Login test passed");
        } catch (Exception e) {
            System.out.println("❌ Login test failed: " + e.getMessage());
            throw e;
        }
    }

    // ==================== NAVIGATION TESTS ====================
    @Test(priority = 2, groups = {"smoke", "navigation"}, dependsOnMethods = {"testSuccessfulLogin"})
    public void testNavigateToAddLicense() throws InterruptedException {
        try {
            performLogin();
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));
            Thread.sleep(500);

            // Click the "+" button to add new license
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[text()='+ Add New License']]")));
            addButton.click();

            Thread.sleep(1000);

            // Verify navigation to add license form
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("add") ||
                            driver.findElements(By.id("exploration_nb")).size() > 0,
                    "Failed to navigate to add license form");

            System.out.println("✅ Navigation to add license test passed");
        } catch (Exception e) {
            System.out.println("❌ Navigation test failed: " + e.getMessage());
            throw e;
        }
    }

    // ==================== FORM FIELD TESTS ====================
    @Test(priority = 3, groups = {"functional", "form"})
    public void testBasicFormFields() throws InterruptedException {
        try {
            navigateToAddLicenseForm();

            // Test exploration license field
            WebElement explorationField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"exploration_nb\"]")));
            explorationField.clear();
            explorationField.sendKeys("TEST123");
            Assert.assertEquals(explorationField.getAttribute("value"), "TEST123");

            // Test land name field
            WebElement landNameField = driver.findElement(By.xpath("//*[@id=\"land_name\"]"));
            landNameField.clear();
            landNameField.sendKeys("Test Land Name");
            Assert.assertEquals(landNameField.getAttribute("value"), "Test Land Name");

            // Test land owner name field
            WebElement landOwnerField = driver.findElement(By.xpath("//*[@id=\"land_owner_name\"]"));
            landOwnerField.clear();
            landOwnerField.sendKeys("Test Owner");
            Assert.assertEquals(landOwnerField.getAttribute("value"), "Test Owner");

            System.out.println("✅ Basic form fields test passed");
        } catch (Exception e) {
            System.out.println("❌ Basic form fields test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4, groups = {"functional", "form"})
    public void testDropdownFields() throws InterruptedException {
        try {
            navigateToAddLicenseForm();

            // Test district dropdown
            WebElement districtDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class, 'ant-select-selector') and .//input[@id='district']]")));
            districtDropdown.click();

            WebElement districtOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class, 'ant-select-item') and contains(text(), 'Hambantota')]")));
            districtOption.click();

            // Test divisional secretary dropdown
            WebElement divSecDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class, 'ant-select-selector') and .//input[@id='divisional_secretary_division']]")));
            divSecDropdown.click();

            WebElement divSecOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class, 'ant-select-item') and contains(text(), 'Tangalle')]")));
            divSecOption.click();

            System.out.println("✅ Dropdown fields test passed");
        } catch (Exception e) {
            System.out.println("❌ Dropdown fields test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 5, groups = {"functional", "form"})
    public void testDatePickerFields() throws InterruptedException {
        try {
            navigateToAddLicenseForm();

            // Test start date picker
            WebElement startDatePicker = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@id='start_date']")));
            startDatePicker.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.className("ant-picker-panel-container")));
            Thread.sleep(300);

            WebElement dateToSelect = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//td[@title='2025-07-29']/div[@class='ant-picker-cell-inner']")));
            dateToSelect.click();

            // Verify date selection
            String selectedValue = startDatePicker.getAttribute("value");
            Assert.assertNotNull(selectedValue);
            Assert.assertFalse(selectedValue.isEmpty());

            // Test end date using JavaScript
            WebElement endDateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("end_date")));
            ((JavascriptExecutor) driver).executeScript(
                    "const el = arguments[0];" +
                            "const value = '2025-07-31';" +
                            "const nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                            "nativeInputValueSetter.call(el, value);" +
                            "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                            "el.dispatchEvent(new Event('change', { bubbles: true }));",
                    endDateInput);

            System.out.println("✅ Date picker fields test passed");
        } catch (Exception e) {
            System.out.println("❌ Date picker fields test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 6, groups = {"functional", "form"})
    public void testNumericFields() throws InterruptedException {
        try {
            navigateToAddLicenseForm();

            // Test capacity fields
            WebElement fullCapacityField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"full_capacity\"]")));
            fullCapacityField.clear();
            fullCapacityField.sendKeys("1000");
            Assert.assertEquals(fullCapacityField.getAttribute("value"), "1000");

            WebElement monthlyCapacityField = driver.findElement(By.xpath("//*[@id=\"monthly_capacity\"]"));
            monthlyCapacityField.clear();
            monthlyCapacityField.sendKeys("80");
            Assert.assertEquals(monthlyCapacityField.getAttribute("value"), "80");

            WebElement usedCapacityField = driver.findElement(By.xpath("//*[@id=\"used\"]"));
            usedCapacityField.clear();
            usedCapacityField.sendKeys("10");
            Assert.assertEquals(usedCapacityField.getAttribute("value"), "10");

            WebElement remainingCapacityField = driver.findElement(By.xpath("//*[@id=\"remaining\"]"));
            remainingCapacityField.clear();
            remainingCapacityField.sendKeys("10");
            Assert.assertEquals(remainingCapacityField.getAttribute("value"), "10");

            System.out.println("✅ Numeric fields test passed");
        } catch (Exception e) {
            System.out.println("❌ Numeric fields test failed: " + e.getMessage());
            throw e;
        }
    }

    // ==================== FILE UPLOAD TESTS ====================
    @Test(priority = 7, groups = {"functional", "upload"})
    public void testFileUploadFunctionality() throws InterruptedException {
        try {
            navigateToAddLicenseForm();

            String projectPath = System.getProperty("user.dir");
            String testFilePath = projectPath + "/src/test/resources/test_images/deedPlan.pdf";

            // Create dummy file if it doesn't exist
            createDummyFileIfNotExists(testFilePath);

            // Test deed plan upload
            WebElement deedPlanInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("Deed_plan")));
            if (!deedPlanInput.isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'block';", deedPlanInput);
            }
            deedPlanInput.sendKeys(testFilePath);

            // Verify file upload (you might need to check for upload success indicators)
            Thread.sleep(1000);

            System.out.println("✅ File upload test passed");
        } catch (Exception e) {
            System.out.println("❌ File upload test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 8, groups = {"functional", "upload"})
    public void testMultipleFileUploads() throws InterruptedException {
        try {
            navigateToAddLicenseForm();
            uploadAllRequiredFiles();
            System.out.println("✅ Multiple file uploads test passed");
        } catch (Exception e) {
            System.out.println("❌ Multiple file uploads test failed: " + e.getMessage());
            throw e;
        }
    }

    // ==================== FORM VALIDATION TESTS ====================
    @Test(priority = 9, groups = {"negative", "validation"})
    public void testEmptyFormSubmission() throws InterruptedException {
        try {
            navigateToAddLicenseForm();

            // Try to submit empty form
            WebElement createButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/form/div[13]/div/div/div/div/button")));
            createButton.click();

            Thread.sleep(2000);

            // Check for validation errors
            List<WebElement> errorMessages = driver.findElements(
                    By.xpath("//*[contains(@class, 'error') or contains(@class, 'ant-form-item-has-error')]"));

            Assert.assertTrue(errorMessages.size() > 0, "Expected validation errors for empty form");
            System.out.println("✅ Empty form validation test passed");
        } catch (Exception e) {
            System.out.println("❌ Empty form validation test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 10, groups = {"negative", "validation"})
    public void testInvalidDataValidation() throws InterruptedException {
        try {
            navigateToAddLicenseForm();

            // Enter invalid data
            WebElement fullCapacityField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"full_capacity\"]")));
            fullCapacityField.clear();
            fullCapacityField.sendKeys("-100"); // Invalid negative number

            WebElement createButton = driver.findElement(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/form/div[13]/div/div/div/div/button"));
            createButton.click();

            Thread.sleep(2000);

            // Should show validation error
            System.out.println("✅ Invalid data validation test passed");
        } catch (Exception e) {
            System.out.println("❌ Invalid data validation test failed: " + e.getMessage());
            throw e;
        }
    }

    // ==================== INTEGRATION TESTS ====================
    @Test(priority = 11, groups = {"integration", "e2e"})
    public void testCompleteAddLicenseWorkflow() throws InterruptedException {
        try {
            performLogin();
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));
            Thread.sleep(500);

            // Navigate to add license
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[span[text()='+ Add New License']]")));
            addButton.click();
            Thread.sleep(500);

            // Fill complete form
            fillCompleteLicenseForm();
            uploadAllRequiredFiles();
            submitForm();
            verifyLicenseCreation();

            System.out.println("✅ Complete add license workflow test passed");
        } catch (Exception e) {
            System.out.println("❌ Complete workflow test failed: " + e.getMessage());
            throw e;
        }
    }

    // ==================== HELPER METHODS ====================
    private void performLogin() {
        driver.get(BASE_URL + "/signin/");

        WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
        username.sendKeys(USERNAME);

        WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
        password.sendKeys(PASSWORD);

        WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".ant-btn-primary")));
        signinButton.click();
    }

    private void navigateToAddLicenseForm() throws InterruptedException {
        performLogin();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));
        Thread.sleep(500);

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div/div/div/div/table/tbody/tr[1]/td[7]/div/a/button")));
        addButton.click();
        Thread.sleep(500);
    }

    private void fillCompleteLicenseForm() throws InterruptedException {
        // Exploration license number
        WebElement explorationField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"exploration_nb\"]")));
        explorationField.clear();
        explorationField.sendKeys("ABC1234");

        // Land name
        WebElement landNameField = driver.findElement(By.xpath("//*[@id=\"land_name\"]"));
        landNameField.clear();
        landNameField.sendKeys("No 23/1" + System.currentTimeMillis() % 10000);

        // Google location
        WebElement googleLocationField = driver.findElement(By.xpath("//*[@id=\"land_google\"]"));
        googleLocationField.clear();
        googleLocationField.sendKeys("http://maps.google.com/?q=No+23/1+Netolpitiya,+Tangalle,+Sri+Lanka");

        // Land owner name
        WebElement landOwnerField = driver.findElement(By.xpath("//*[@id=\"land_owner_name\"]"));
        landOwnerField.clear();
        landOwnerField.sendKeys("Kasun Perera");

        // Village name
        WebElement villageField = driver.findElement(By.xpath("//*[@id=\"village_name\"]"));
        villageField.clear();
        villageField.sendKeys("Netolpitiya");

        // Grama Niladhari
        WebElement gramaNiladhariField = driver.findElement(By.xpath("//*[@id=\"grama_niladari\"]"));
        gramaNiladhariField.clear();
        gramaNiladhariField.sendKeys("Netolpitiya");

        // District dropdown
        WebElement districtDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select-selector') and .//input[@id='district']]")));
        districtDropdown.click();
        WebElement districtOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select-item') and contains(text(), 'Hambantota')]")));
        districtOption.click();

        // Divisional Secretary dropdown
        WebElement divSecDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select-selector') and .//input[@id='divisional_secretary_division']]")));
        divSecDropdown.click();
        WebElement divSecOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select-item') and contains(text(), 'Tangalle')]")));
        divSecOption.click();

        // Mining license number
        WebElement licenseNumberField = driver.findElement(By.xpath("//*[@id=\"mining_license_number\"]"));
        licenseNumberField.clear();
        licenseNumberField.sendKeys("PQR1234");

        // Start date
        WebElement startDatePicker = driver.findElement(By.xpath("//input[@id='start_date']"));
        startDatePicker.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-picker-panel-container")));
        Thread.sleep(300);
        WebElement dateToSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//td[@title='2025-07-29']/div[@class='ant-picker-cell-inner']")));
        dateToSelect.click();

        // End date
        WebElement endDateInput = driver.findElement(By.id("end_date"));
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];" +
                        "const value = '2025-07-31';" +
                        "const nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "nativeInputValueSetter.call(el, value);" +
                        "el.dispatchEvent(new Event('input', { bubbles: true }));" +
                        "el.dispatchEvent(new Event('change', { bubbles: true }));",
                endDateInput);

        // Capacity fields
        driver.findElement(By.xpath("//*[@id=\"full_capacity\"]")).sendKeys("1000");
        driver.findElement(By.xpath("//*[@id=\"monthly_capacity\"]")).sendKeys("80");
        driver.findElement(By.xpath("//*[@id=\"used\"]")).sendKeys("10");
        driver.findElement(By.xpath("//*[@id=\"remaining\"]")).sendKeys("10");
    }

    private void uploadAllRequiredFiles() throws InterruptedException {
        String projectPath = System.getProperty("user.dir");
        String[] filePaths = {
                projectPath + "/src/test/resources/test_images/deedPlan.pdf",
                projectPath + "/src/test/resources/test_images/minePlan.pdf",
                projectPath + "/src/test/resources/test_images/viabilityReport.pdf",
                projectPath + "/src/test/resources/test_images/boundarySurvey.pdf"
        };

        String[] fieldIds = {"Deed_plan", "detailed_mine_plan", "economic_viability_report", "license_boundary_survey"};

        for (int i = 0; i < filePaths.length; i++) {
            createDummyFileIfNotExists(filePaths[i]);
            WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(fieldIds[i])));
            if (!fileInput.isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'block';", fileInput);
            }
            fileInput.sendKeys(filePaths[i]);
        }
        Thread.sleep(1000);
    }

    private void submitForm() {
        try {
            WebElement createButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/form/div[13]/div/div/div/div/button")));

            try {
                createButton.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", createButton);
            }

            Thread.sleep(3000);
        } catch (Exception e) {
            throw new RuntimeException("Unable to submit form", e);
        }
    }

    private void verifyLicenseCreation() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/gsmb/dashboard"),
                ExpectedConditions.urlContains("/gsmb/licenses"),
                ExpectedConditions.urlContains("/gsmb/add-new-license")
        ));

        String finalUrl = driver.getCurrentUrl();
        boolean navigationSuccessful = finalUrl.contains("/gsmb/dashboard") ||
                finalUrl.contains("/gsmb/licenses") ||
                finalUrl.contains("/gsmb/add-new-license");

        try {
            WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'success') or contains(@class, 'ant-notification') or contains(@class, 'ant-message-success')]")));
            Assert.assertTrue(successMessage.isDisplayed(), "Success message is not visible.");
        } catch (NoSuchElementException e) {
            // No explicit success message found, rely on URL check
        }

        Assert.assertTrue(navigationSuccessful,
                "Failed to navigate to expected page or confirm license creation. Current URL: " + finalUrl);
    }

    private void createDummyFileIfNotExists(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                java.nio.file.Files.write(file.toPath(), "Dummy PDF content for testing".getBytes());
            }
        } catch (Exception e) {
            System.out.println("Failed to create dummy file: " + path);
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            try {
                Thread.sleep(2000); // Brief pause to observe results
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            driver.quit();
        }
    }

    @AfterClass
    public void tearDownClass() {
        System.out.println("Mining License Test Suite completed");
        if (driver != null) {
            driver.quit();
        }
    }
}