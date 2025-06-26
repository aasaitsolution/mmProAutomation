//package PoliceOfficer;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.Alert;
//import org.openqa.selenium.By;
//import org.openqa.selenium.NoAlertPresentException;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.Test;
//import java.time.Duration;
//
//public class policeofficersignin {
// private WebDriver driver;
//    private WebDriverWait wait;
//
//    @BeforeClass
//    public void setup() {
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--incognito");
//        driver = new ChromeDriver(options);
//        driver.manage().window().maximize();
//        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//        driver.get("https://mmpro.aasait.lk/");
//    }
//
//    @Test(priority = 1)
//    public void navigateToSignInPage() {
//        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
//                By.cssSelector("a[href='/signin'] button")));
//        loginButton.click();
//        System.out.println("✅ Navigated to Sign In page");
//    }
//
//    @Test(priority = 2)
//    public void enterCredentialsAndLogin() {
//        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                By.id("sign-in_username")));
//        usernameField.sendKeys("saman");
//
//        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                By.id("sign-in_password")));
//        passwordField.sendKeys("12345678");
//
//        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(
//                By.cssSelector("button[type='submit']")));
//        signInButton.click();
//        System.out.println("✅ Submitted credentials");
//
//        // Handle alert (if present)
//        try {
//            Alert alert = driver.switchTo().alert();
//            alert.dismiss();
//            System.out.println("⚠️ Alert dismissed");
//        } catch (NoAlertPresentException e) {
//            System.out.println("ℹ️ No alert to handle");
//        }
//
//        wait.until(ExpectedConditions.urlContains("/police-officer"));
//        System.out.println("✅ Landed on Police Officer Dashboard");
//    }
//
//    @Test(priority = 3)
//    public void checkInvalidVehicleNumber() {
//        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                By.cssSelector("input[type='text']")));
//        inputField.sendKeys("ABX1234");
//
//        WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
//                By.cssSelector("button.po-check-button")));
//        checkButton.click();
//        System.out.println("✅ Submitted vehicle number for check");
//
//        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                By.className("pov-validBadge")));
//        String modalText = modal.getText();
//        System.out.println("ℹ️ Modal Text: " + modalText);
//
//        if (modalText.toLowerCase().contains("valid")) {
//            System.out.println("✅ Vehicle number marked as Valid as expected.");
//        } else {
//            System.out.println("❌ Unexpected modal message: " + modalText);
//        }
//    }
//
//    @AfterClass
//    public void tearDown() {
//        try {
//            Thread.sleep(3000); // small pause for final visibility
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        driver.quit();
//        System.out.println("🔒 Browser closed");
//    }
//}

package PoliceOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class policeofficersignin {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("https://mmpro.aasait.lk/");
        System.out.println("🚀 Browser launched and navigated to the homepage.");
    }

    @Test(priority = 1, description = "Navigate from homepage to the Sign In page")
    public void navigateToSignInPage() {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginButton.click();

        // Assert that the URL is now the sign-in page
        wait.until(ExpectedConditions.urlContains("/signin"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/signin"), "Failed to navigate to the sign-in page.");
        System.out.println("✅ Navigated to Sign In page successfully.");
    }

    @Test(priority = 2, description = "Enter valid credentials and log in as a police officer")
    public void enterCredentialsAndLogin() {
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("sign-in_username")));
        usernameField.sendKeys("saman");

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("sign-in_password")));
        passwordField.sendKeys("12345678");

        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']")));
        signInButton.click();
        System.out.println("✅ Submitted credentials.");

        // Handle alert (if present)
        try {
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
            System.out.println("⚠️ Alert was present and dismissed.");
        } catch (NoAlertPresentException e) {
            // This is the expected path, no action needed.
            System.out.println("ℹ️ No alert was present.");
        }

        // Wait for dashboard URL and verify
        wait.until(ExpectedConditions.urlContains("/police-officer"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/police-officer"), "Login failed or redirection to dashboard was unsuccessful.");
        System.out.println("✅ Landed on Police Officer Dashboard.");
    }

    @Test(priority = 3, description = "Check for a valid vehicle number", dependsOnMethods = "enterCredentialsAndLogin")
    public void checkValidVehicleNumber() {
        try {
            resetDashboardState(); // Ensure a clean start for the test

            // Store the current URL for later comparison
            String originalUrl = driver.getCurrentUrl();

            // Enter vehicle number and click check button
            WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='text']")));
            inputField.sendKeys("ABX1234");

            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.po-check-button")));
            checkButton.click();
            System.out.println("✅ Submitted a valid vehicle number for check.");

            // Wait for the redirect to the valid page
            wait.until(ExpectedConditions.urlContains("/police-officer/valid"));
            System.out.println("ℹ️ Redirected to validation results page");

            // Verify the content on the validation page
            WebElement validBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.className("pov-validBadge")));
            String resultText = validBadge.getText();
            System.out.println("ℹ️ Validation Result: " + resultText);

            Assert.assertTrue(resultText.toLowerCase().contains("valid"),
                    "The vehicle was not marked as 'Valid'.");
            System.out.println("✅ Vehicle number correctly marked as Valid.");

            // Navigate back to the original page
            driver.navigate().to("https://mmpro.aasait.lk/police-officer");

            // Verify we're back on the original page
            wait.until(ExpectedConditions.urlToBe(originalUrl));
            System.out.println("✅ Successfully returned to the police officer dashboard.");
        } catch (Exception e) {
            System.err.println("❌ Test failed due to: " + e.getMessage());
            Assert.fail("Test failed due to exception: " + e.toString());
        }
    }


    @Test(priority = 5, description = "Check form validation for empty vehicle number", dependsOnMethods = "enterCredentialsAndLogin")
    public void checkEmptyVehicleNumber() {
        resetDashboardState();
        WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.po-check-button")));
        checkButton.click();
        System.out.println("✅ Clicked Check button with empty input field.");

        // Look for the Ant Design validation error message
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@role='alert' and contains(text(), 'Please input a vehicle number')]")));

        Assert.assertEquals(errorMessage.getText(), "Please input a vehicle number", "Validation error message for empty field is incorrect or not found.");
        System.out.println("✅ Validation error message for empty input displayed correctly.");
    }

    @Test(priority = 6, description = "Verify the logout functionality", dependsOnMethods = "enterCredentialsAndLogin")
    public void verifyLogoutFunctionality() {
        resetDashboardState();
        // The logout button is often just a button with the text "Logout"
        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button/span[text()='Logout']/..")));
        logoutButton.click();
        System.out.println("✅ Clicked the Logout button.");

        // After logout, user should be redirected to the homepage
        wait.until(ExpectedConditions.urlToBe("https://mmpro.aasait.lk/"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://mmpro.aasait.lk/", "User was not redirected to the homepage after logout.");
        System.out.println("✅ Successfully logged out and redirected to the homepage.");
    }

    @AfterClass
    public void tearDown() {
        try {
            System.out.println("⏳ Waiting for 3 seconds before closing...");
            Thread.sleep(3000); // small pause for final visibility
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();
        System.out.println("🔒 Browser closed.");
    }

    /**
     * Helper method to refresh the page to reset the state of the dashboard
     * before each test that runs on it.
     */
    private void resetDashboardState() {
        // A simple refresh is often enough to reset the form state
        driver.navigate().refresh();
        System.out.println("🔄 Page refreshed to reset state.");
    }
}