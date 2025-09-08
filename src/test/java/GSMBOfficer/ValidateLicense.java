package GSMBOfficer;

import base.BaseTest;
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

public class ValidateLicense extends BaseTest {
//    private WebDriver driver;
//    private WebDriverWait wait;
    private static final String BASE_URL = "https://mmpro.aasait.lk";
    private static final String USERNAME = "nimal";
    private static final String PASSWORD = "12345678";

//    @BeforeClass
//    public void setupClass() {
//        System.out.println("Starting License Validation Test Suite");
//    }
//
//    @BeforeMethod
//    public void setup() {
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--incognito");
//        options.addArguments("--disable-notifications");
//        options.addArguments("--disable-popup-blocking");
//
//        driver = new ChromeDriver(options);
//        driver.manage().window().maximize();
//        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//    }

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
    @Test(priority = 2, groups = {"smoke", "navigation"})
    public void testNavigateToMiningLicenseTab() {
        try {
            performLogin();
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));
            waitABit();

            // Navigate to Request Mining tab
            WebElement mlTab = wait.until(ExpectedConditions.elementToBeClickable(
                   By.xpath("//div[@role='tab' and text()='Request Mining']")));
            mlTab.click();

            waitABit();

            // Verify navigation success by checking for "Request Subject" column
            boolean isRequestSubjectColumnPresent = driver.findElements(
                By.xpath("//thead//th[text()='Request Subject']")
            ).size() > 0;

            Assert.assertTrue(isRequestSubjectColumnPresent,
                    "❌ 'Request Subject' column not found - navigation to Mining License tab may have failed");

            System.out.println("✅ Navigation to Mining License tab test passed");
        } catch (Exception e) {
            System.out.println("❌ Navigation test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 3, groups = {"smoke", "modal"}, dependsOnMethods = {"testNavigateToMiningLicenseTab"})
    public void testOpenLicenseValidationModal() {
        try {
            navigateToLicenseValidationModal();

            // Verify modal is opened by checking modal title
            WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[@class='ant-modal-title' and text()='License Validation']")));
            Assert.assertTrue(modalTitle.isDisplayed(), "License Validation modal is not displayed");

            // Verify modal elements are present
            Assert.assertTrue(driver.findElements(By.xpath("//button[contains(@class, 'ant-btn-primary') and .//span[text()='Validate']]")).size() > 0,
                    "Validate button not found in modal");

            System.out.println("✅ License Validation modal opening test passed");
        } catch (Exception e) {
            System.out.println("❌ Modal opening test failed: " + e.getMessage());
            throw e;
        }
    }

    // ==================== VALIDATION WORKFLOW TESTS ====================
    @Test(priority = 4, groups = {"functional", "validation"})
    public void testLicenseValidationWorkflow() {
        try {
            navigateToLicenseValidationModal();

            // Click Validate button
            WebElement validateButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class, 'ant-btn-primary') and .//span[text()='Validate']]")));

            validateButton.click();

            waitABit();

            // Check if validation was successful (you may need to adjust this based on actual behavior)
            // This could be checking for a success message, modal closure, or status change
            System.out.println("✅ License validation workflow test passed");
        } catch (Exception e) {
            System.out.println("❌ Validation workflow test failed: " + e.getMessage());
            throw e;
        }
    }

   @Test(priority = 5, groups = {"functional", "modal-close"})
   public void testCloseValidationModal() {
       try {
           navigateToLicenseValidationModal();

           // Click close button (X button)
           WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                   By.xpath("//button[@aria-label='Close' and contains(@class, 'ant-modal-close')]")));
           closeButton.click();

           waitABit();

           // Verify modal is closed
           Assert.assertTrue(driver.findElements(By.xpath("/html/body/div[2]/div/div[2]/div/div[1]/div/button")).size() == 0,
                   "Modal should be closed but is still visible");

           System.out.println("✅ License validation modal close test passed");
       } catch (Exception e) {
           System.out.println("❌ Modal close test failed: " + e.getMessage());
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

    private void navigateToLicenseValidationModal() {
        performLogin();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));
        waitABit();

        // Navigate to request Mining  tab
        WebElement mlTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@role='tab' and text()='Request Mining']")));
        mlTab.click();

        waitABit();

        // Click the filter dropdown button
        WebElement filterDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select') and .//span[text()='Filter by status']]")));
        filterDropdown.click();

        // Wait for dropdown options to appear and select "ME Approved"
        WebElement physicalMeetingOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select-item-option') and text()='ME Approved']")));
        physicalMeetingOption.click();

        waitABit();

        // Click the validate the license button in the table
        WebElement validateLicenseButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class, 'ant-btn-primary') and .//span[text()='Validate the license']]")));

        validateLicenseButton.click();

        // Wait for modal to appear
       wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.xpath("//div[contains(@class, 'ant-modal-title') and normalize-space(text())='License Validation']")));
    }

    private void waitABit() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
        System.out.println("License Validation Test Suite completed");
        if (driver != null) {
            driver.quit();
        }
    }
}