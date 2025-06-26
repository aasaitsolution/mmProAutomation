//Done
package PoliceOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class validnumber {
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
        long start = System.currentTimeMillis();

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/signin"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/signin"), "Failed to navigate to the sign-in page.");
        System.out.println("✅ Navigated to Sign In page successfully.");

        long end = System.currentTimeMillis();
        System.out.println("⏱ Test Duration: " + (end - start) + " ms");
    }

    @Test(priority = 2, description = "Enter valid credentials and log in as a police officer")
    public void enterCredentialsAndLogin() {
        long start = System.currentTimeMillis();

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

        try {
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
            System.out.println("⚠️ Alert was present and dismissed.");
        } catch (NoAlertPresentException e) {
            System.out.println("ℹ️ No alert was present.");
        }

        wait.until(ExpectedConditions.urlContains("/police-officer"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/police-officer"),
                "Login failed or redirection to dashboard was unsuccessful.");
        System.out.println("✅ Landed on Police Officer Dashboard.");

        long end = System.currentTimeMillis();
        System.out.println("⏱ Test Duration: " + (end - start) + " ms");
    }

    @Test(priority = 3, description = "Check for a valid vehicle number", dependsOnMethods = "enterCredentialsAndLogin")
    public void checkValidVehicleNumber() {
        try {
            // Ensure we are on the dashboard page
            wait.until(ExpectedConditions.urlContains("/police-officer"));

            // Wait for the input field to be visible - using a broad but robust selector
            WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='text']")));
            wait.until(ExpectedConditions.elementToBeClickable(inputField));

            inputField.clear();
            inputField.sendKeys("CBA4321");
            System.out.println("✅ Entered vehicle number.");

            // Click the check button
            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.po-check-button")));
            checkButton.click();
            System.out.println("✅ Submitted a valid vehicle number for check.");

            // Wait for redirect to results page
            wait.until(ExpectedConditions.urlContains("/police-officer/valid"));
            System.out.println("ℹ️ Redirected to validation results page");

            // Validate that the result badge confirms validity
            WebElement validBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.className("pov-validBadge")));
            String resultText = validBadge.getText();
            System.out.println("ℹ️ Validation Result: " + resultText);

            Assert.assertTrue(resultText.toLowerCase().contains("valid"),
                    "The vehicle was not marked as 'Valid'.");
            System.out.println("✅ Vehicle number correctly marked as Valid.");

            // Return to dashboard
            driver.navigate().to("https://mmpro.aasait.lk/police-officer");
            wait.until(ExpectedConditions.urlContains("/police-officer"));
            System.out.println("✅ Successfully returned to the police officer dashboard.");

        } catch (TimeoutException e) {
            System.err.println("❌ Timed out: couldn't find expected element or URL change.");
            System.out.println("🔍 Debug URL: " + driver.getCurrentUrl());
            System.out.println("🔍 Page Source Snippet: " + driver.getPageSource().substring(0, 1000));
            Assert.fail("Timeout waiting for element or URL: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Test failed due to: " + e.getMessage());
            Assert.fail("Exception: " + e.toString());
        }
    }


    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("🔚 Browser session ended.");
        }
    }
}
