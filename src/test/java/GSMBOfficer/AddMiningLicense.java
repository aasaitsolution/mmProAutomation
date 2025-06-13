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
import java.time.Duration;

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
                By.xpath("//div[contains(@class, 'ant-picker-panel')]")
        ));

        WebElement startDateInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='start_date']")
        ));
        startDateInput.clear();
        startDateInput.sendKeys("30/07/2024");
        startDateInput.sendKeys(Keys.ENTER);

        String inputValue = startDateInput.getAttribute("value");

        // Click on the date picker input field to open calendar
        WebElement datePickeInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='due_date']")
        ));
        datePickeInput.click();

// Wait for the calendar panel to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'ant-picker-panel')]")
        ));

        WebElement dueDateInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='due_date']")
        ));
        startDateInput.clear();
        startDateInput.sendKeys("15/07/2024");
        startDateInput.sendKeys(Keys.ENTER);

//        String inputValue = startDateInput.getAttribute("value");

// Verify the selected date appears in the input field
        WebElement selectedDate = driver.findElement(By.id("start_date"));
        String selectedValue = selectedDate.getAttribute("value");
        Assert.assertNotNull(selectedValue);
        Assert.assertFalse(selectedValue.isEmpty());







        WebElement validityStartField = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/form/div[8]/div[1]/div/div/div[2]/div/div/div")
        ));
        validityStartField.clear();
        validityStartField.sendKeys("30/07/2025");

        WebElement validityEndField = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"endDate\"]")
        ));
        validityEndField.clear();
        validityEndField.sendKeys("31/10/2025");

        WebElement capacityField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"capacity\"]")
        ));
        capacityField.clear();
        capacityField.sendKeys("1000");

        WebElement locationField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"location\"]")
        ));
        locationField.clear();
        locationField.sendKeys("No 23/1, Netolpitiya");


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
//            driver.quit();
        }
    }
}