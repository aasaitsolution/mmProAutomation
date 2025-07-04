package GSMBOfficer;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class RequestMining {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";
    private static final String SIGNIN_URL = BASE_URL + "/signin";
    private static final String DASHBOARD_URL = BASE_URL + "/gsmb/dashboard";
    private static final String USERNAME = "nimal";
    private static final String PASSWORD = "12345678";

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void testUserLogin() {
        driver.get(SIGNIN_URL);

        try {
            // Enter credentials
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            usernameField.clear();
            usernameField.sendKeys(USERNAME);

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
            passwordField.clear();
            passwordField.sendKeys(PASSWORD);

            // Click sign in button
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-btn-primary")));
            signInButton.click();

            // Verify successful login
            wait.until(ExpectedConditions.urlToBe(DASHBOARD_URL));
            Assert.assertEquals(driver.getCurrentUrl(), DASHBOARD_URL, "User should be redirected to dashboard after successful login");

            System.out.println("Login test passed successfully");

        } catch (Exception e) {
            Assert.fail("Login test failed: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void testNavigateToRequestMining() {
        driver.get(SIGNIN_URL);

        try {
            // Login first
            performLogin();

            // Navigate to Request Mining tab
            WebElement mlTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[5]")));
            mlTab.click();

            // Wait for table to load and verify navigation
            WebElement requestMiningTable = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div[2]/div/div/div/div/div[2]/table")));

            Assert.assertTrue(requestMiningTable.isDisplayed(), "Request Mining table should be visible after navigation");

            System.out.println("Navigation to Request Mining test passed successfully");

        } catch (Exception e) {
            Assert.fail("Navigation to Request Mining test failed: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void testViewButtonFunctionality() {
        driver.get(SIGNIN_URL);

        try {
            // Setup: Login and navigate
            performLogin();
            navigateToRequestMiningTab();

            // Click view button
            WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div[2]/div/div/div/div/div[2]/table/tbody/tr[2]/td[8]/div/button[1]")));
            viewButton.click();

            // Verify view dialog opens
            WebElement viewDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/div[2]/div/div[2]/div")));
            Assert.assertTrue(viewDialog.isDisplayed(), "View dialog should be displayed");

            // Close the dialog
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div/div[2]/div/div[1]/div/button")));
            closeButton.click();

            System.out.println("View button functionality test passed successfully");

        } catch (Exception e) {
            Assert.fail("View button functionality test failed: " + e.getMessage());
        }
    }

    @Test(priority = 4)
    public void testScheduleButtonFunctionality() {
        driver.get(SIGNIN_URL);

        try {
            // Setup: Login, navigate, and open view dialog
            performLogin();
            navigateToRequestMiningTab();
            openViewDialog();

            // Click schedule button using JavaScript to avoid interception
            WebElement scheduleButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(@class, 'ant-btn') and .//span[text()='Schedule']]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", scheduleButton);

            // Verify schedule dialog opens
            WebElement scheduleDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'ant-modal-content')]")));
            Assert.assertTrue(scheduleDialog.isDisplayed(), "Schedule dialog should be displayed");

            System.out.println("Schedule button functionality test passed successfully");

        } catch (Exception e) {
            Assert.fail("Schedule button functionality test failed: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void testDatePickerFunctionality() {
        driver.get(SIGNIN_URL);

        try {
            // Setup: Open schedule dialog
            performLogin();
            navigateToRequestMiningTab();
            openViewDialog();
            openScheduleDialog();

            // Test date picker
            WebElement datePickerInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("date")));
            datePickerInput.click();

            // Wait for calendar and select date
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-picker-panel-container")));

            WebElement dateToSelect = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//td[@title='2025-07-31']/div[@class='ant-picker-cell-inner']")));
            dateToSelect.click();

            // Verify date selection
            String selectedValue = datePickerInput.getAttribute("value");
            Assert.assertNotNull(selectedValue, "Selected date should not be null");
            Assert.assertFalse(selectedValue.isEmpty(), "Selected date should not be empty");

            System.out.println("Date picker functionality test passed - Selected date: " + selectedValue);

        } catch (Exception e) {
            Assert.fail("Date picker functionality test failed: " + e.getMessage());
        }
    }

    @Test(priority = 6)
    public void testLocationFieldFunctionality() {
        driver.get(SIGNIN_URL);

        try {
            // Setup: Open schedule dialog
            performLogin();
            navigateToRequestMiningTab();
            openViewDialog();
            openScheduleDialog();

            // Test location field
            String testLocation = "GSMB Head Office";
            WebElement locationField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("location")));
            locationField.clear();
            locationField.sendKeys(testLocation);

            // Verify location input
            String enteredLocation = locationField.getAttribute("value");
            Assert.assertEquals(enteredLocation, testLocation, "Location field should contain the entered text");

            System.out.println("Location field functionality test passed - Location: " + enteredLocation);

        } catch (Exception e) {
            Assert.fail("Location field functionality test failed: " + e.getMessage());
        }
    }

    @Test(priority = 7)
    public void testPurposeFieldFunctionality() {
        driver.get(SIGNIN_URL);

        try {
            // Setup: Open schedule dialog
            performLogin();
            navigateToRequestMiningTab();
            openViewDialog();
            openScheduleDialog();

            // Test purpose field
            String testPurpose = "Bring documents";
            WebElement purposeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notes")));
            purposeField.clear();
            purposeField.sendKeys(testPurpose);

            // Verify purpose input
            String enteredPurpose = purposeField.getAttribute("value");
            Assert.assertEquals(enteredPurpose, testPurpose, "Purpose field should contain the entered text");

            System.out.println("Purpose field functionality test passed - Purpose: " + enteredPurpose);

        } catch (Exception e) {
            Assert.fail("Purpose field functionality test failed: " + e.getMessage());
        }
    }

//    @Test(priority = 8)
//    public void testCompleteScheduleCreation() {
//        driver.get(SIGNIN_URL);
//
//        try {
//            // Setup: Open schedule dialog
//            performLogin();
//            navigateToRequestMiningTab();
//            openViewDialog();
//            openScheduleDialog();
//
//            // Fill all required fields
//            fillDateField();
//            fillLocationField();
//            fillPurposeField();
//
//            // Submit the schedule
//            WebElement scheduleSubmitButton = wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.xpath("/html/body/div[3]/div/div[2]/div/div[1]/div/div[3]/button[2]")));
//
//            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", scheduleSubmitButton);
//
//            // Verify schedule creation (dialog should close or show success message)
//            Thread.sleep(2000);
//            System.out.println("Complete schedule creation test passed successfully");
//
//        } catch (Exception e) {
//            Assert.fail("Complete schedule creation test failed: " + e.getMessage());
//        }
//    }

    // Helper methods for reusable actions
    private void performLogin() {
        try {
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            usernameField.clear();
            usernameField.sendKeys(USERNAME);

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
            passwordField.clear();
            passwordField.sendKeys(PASSWORD);

            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-btn-primary")));
            signInButton.click();

            wait.until(ExpectedConditions.urlToBe(DASHBOARD_URL));
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    private void navigateToRequestMiningTab() {
        try {
            WebElement mlTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[5]")));
            mlTab.click();

            // Wait for table to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div[2]/div/div/div/div/div[2]/table")));
        } catch (Exception e) {
            throw new RuntimeException("Navigation to Request Mining failed: " + e.getMessage());
        }
    }

    private void openViewDialog() {
        try {
            WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div[2]/div/div/div/div/div[2]/table/tbody/tr[2]/td[8]/div/button[1]")));
            viewButton.click();

            // Wait for dialog to open
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("/html/body/div[2]/div/div[2]/div")));
        } catch (Exception e) {
            throw new RuntimeException("Opening view dialog failed: " + e.getMessage());
        }
    }

    private void openScheduleDialog() {
        try {
            WebElement scheduleButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(@class, 'ant-btn') and .//span[text()='Schedule']]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", scheduleButton);

            // Wait for schedule dialog to open
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class, 'ant-modal-content')]")));
        } catch (Exception e) {
            throw new RuntimeException("Opening schedule dialog failed: " + e.getMessage());
        }
    }

    private void fillDateField() {
        try {
            WebElement datePickerInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("date")));
            datePickerInput.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-picker-panel-container")));

            WebElement dateToSelect = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//td[@title='2025-06-28']/div[@class='ant-picker-cell-inner']")));
            dateToSelect.click();
        } catch (Exception e) {
            throw new RuntimeException("Filling date field failed: " + e.getMessage());
        }
    }

    private void fillLocationField() {
        try {
            WebElement locationField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("location")));
            locationField.clear();
            locationField.sendKeys("GSMB Head Office");
        } catch (Exception e) {
            throw new RuntimeException("Filling location field failed: " + e.getMessage());
        }
    }

    private void fillPurposeField() {
        try {
            WebElement purposeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notes")));
            purposeField.clear();
            purposeField.sendKeys("Bring documents");
        } catch (Exception e) {
            throw new RuntimeException("Filling purpose field failed: " + e.getMessage());
        }
    }
}