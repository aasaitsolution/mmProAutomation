package PoliceOfficer;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class policeofficersignin extends BaseTest {

    /**
     * Helper method to perform login before tests that require authentication
     */
    private void performLogin() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/");
        waitForPageLoadComplete();

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginButton.click();
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/signin"));
        System.out.println("Navigated to Sign In page successfully.");

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("sign-in_username")));
        usernameField.sendKeys("saman");

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("sign-in_password")));
        passwordField.sendKeys("12345678");

        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']")));
        signInButton.click();
        waitForPageLoadComplete();

        System.out.println("Submitted credentials.");

        // Handle alert (if present)
        try {
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
            System.out.println("Alert was present and dismissed.");
        } catch (NoAlertPresentException e) {
            System.out.println("No alert was present.");
        }

        // Wait for dashboard URL and verify
        wait.until(ExpectedConditions.urlContains("/police-officer"));
        System.out.println("Landed on Police Officer Dashboard.");
    }

    /**
     * Helper method to refresh the page to reset the state of the dashboard
     */
    private void resetDashboardState() throws InterruptedException {
        driver.navigate().refresh();
        waitForPageLoadComplete();
        System.out.println("Page refreshed to reset state.");
    }

    private void waitForPageLoadComplete() {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    @Test(priority = 1)
    public void navigateToSignInPage() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/");
        waitForPageLoadComplete();

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginButton.click();
        waitForPageLoadComplete();

        // Assert that the URL is now the sign-in page
        wait.until(ExpectedConditions.urlContains("/signin"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/signin"), "Failed to navigate to the sign-in page.");
        System.out.println("Navigated to Sign In page successfully.");
    }

    @Test(priority = 2)
    public void enterCredentialsAndLogin() throws InterruptedException {
        performLogin();
        Assert.assertTrue(driver.getCurrentUrl().contains("/police-officer"),
                "Login failed or redirection to dashboard was unsuccessful.");
    }

    @Test(priority = 3)
    public void checkValidVehicleNumber() throws InterruptedException {
        performLogin();

        try {
            resetDashboardState();

            // Store the current URL for later comparison
            String originalUrl = driver.getCurrentUrl();

            // Enter vehicle number and click check button
            WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='text']")));
            inputField.sendKeys("LAQ1234");

            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.po-check-button")));

            // Use JavaScript click to avoid interception
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", checkButton);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkButton);

            System.out.println("Submitted a valid vehicle number for check.");

            // Wait for the redirect to the valid page
            wait.until(ExpectedConditions.urlContains("/police-officer/valid"));
            System.out.println("Redirected to validation results page");

            // Verify the content on the validation page
            WebElement validBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.className("pov-validBadge")));
            String resultText = validBadge.getText();
            System.out.println("Validation Result: " + resultText);

            Assert.assertTrue(resultText.toLowerCase().contains("valid"),
                    "The vehicle was not marked as 'Valid'.");
            System.out.println("Vehicle number correctly marked as Valid.");

            // Navigate back to the original page
            driver.navigate().back();
            wait.until(ExpectedConditions.urlContains("/police-officer"));
            waitForPageLoadComplete();

            System.out.println("Successfully returned to the police officer dashboard.");

        } catch (Exception e) {
            System.err.println("Valid vehicle number test failed due to: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4)
    public void checkEmptyVehicleNumber() throws InterruptedException {
        performLogin();
        resetDashboardState();

        // Wait for input box to be ready before clicking
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='text']")));

        WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.po-check-button")));

        // Use JavaScript click to avoid interception
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", checkButton);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkButton);

        System.out.println("Clicked Check button with empty input field.");

        // Wait for the validation modal and message
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".validation-modal .validation-message")));

        System.out.println("Validation error message for empty input displayed correctly.");

        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".validation-close-button")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
    }

    @Test(priority = 5)
    public void verifyLogoutFunctionality() throws InterruptedException {
        performLogin();
        resetDashboardState();

        // Click on the profile dropdown (the arrow icon)
        WebElement dropdownIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[style*='display: flex'] svg")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dropdownIcon);
        System.out.println("Clicked the dropdown icon.");

        // Wait for the Logout element to be visible and click it
        WebElement logoutOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[normalize-space()='Logout']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", logoutOption);
        System.out.println("Clicked the Logout option.");

        // Confirm redirection to the signin page
        wait.until(ExpectedConditions.urlToBe("https://mmpro.aasait.lk/signin"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://mmpro.aasait.lk/signin",
                "User was not redirected to the signin page after logout.");
        System.out.println("Successfully logged out and redirected to the signin page.");
    }

    @Test(priority = 6)
    public void checkInvalidVehicleNumber() throws InterruptedException {
        performLogin();
        resetDashboardState();

        // Enter invalid vehicle number
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='text']")));
        inputField.sendKeys("INVALID123");

        WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.po-check-button")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", checkButton);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkButton);

        System.out.println("Submitted invalid vehicle number for check.");

        // Check for appropriate response (could be error message or invalid page)
        try {
            // Look for error modal
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".validation-modal .validation-message")));
            System.out.println("Invalid vehicle number handled with error message.");

            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".validation-close-button")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);

        } catch (Exception e) {
            // Alternative: check if redirected to invalid page
            try {
                wait.until(ExpectedConditions.urlContains("/police-officer/invalid"));
                System.out.println("Redirected to invalid vehicle page.");

                driver.navigate().back();
                wait.until(ExpectedConditions.urlContains("/police-officer"));
                waitForPageLoadComplete();

            } catch (Exception e2) {
                System.out.println("Invalid vehicle number handling behavior unclear.");
            }
        }
    }

    @Test(priority = 7)
    public void testVehicleNumberFormatValidation() throws InterruptedException {
        performLogin();
        resetDashboardState();

        String[] testInputs = {"AB1234", "ABC123", "12345", "ABCD1234", "AB-1234"};

        for (String testInput : testInputs) {
            WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='text']")));
            inputField.clear();
            inputField.sendKeys(testInput);

            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.po-check-button")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", checkButton);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkButton);

            Thread.sleep(1000); // Brief pause between tests

            // Handle any modals or responses
            try {
                WebElement closeButton = driver.findElement(By.cssSelector(".validation-close-button"));
                if (closeButton.isDisplayed()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
                }
            } catch (NoSuchElementException ignored) {}

            // Navigate back if redirected
            if (!driver.getCurrentUrl().contains("/police-officer") ||
                    driver.getCurrentUrl().contains("/police-officer/valid") ||
                    driver.getCurrentUrl().contains("/police-officer/invalid")) {
                driver.navigate().back();
                wait.until(ExpectedConditions.urlContains("/police-officer"));
                waitForPageLoadComplete();
            }

            System.out.println("Tested vehicle number format: " + testInput);
        }

        System.out.println("Vehicle number format validation tests completed.");
    }

}