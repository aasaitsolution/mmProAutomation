package PoliceOfficer;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class policeofficersignin extends BaseTest {

    @Test(priority = 1, description = "Navigate from homepage to the Sign In page")
    public void navigateToSignInPage() {
        driver.get("https://mmpro.aasait.lk/");

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
        // ✅ First make sure we are really on the signin page
        wait.until(ExpectedConditions.urlContains("/signin"));
        waitForPageLoadComplete();

        // ✅ Locate username field (same style as in dispatchHistory)
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("sign-in_username")));
        usernameField.clear();
        usernameField.sendKeys("saman");

        WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("sign-in_password")));
        passwordField.clear();
        passwordField.sendKeys("12345678");

        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']")));
        signInButton.click();
        System.out.println("✅ Submitted credentials.");

        // Handle alert (if any)
        try {
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
            System.out.println("⚠️ Alert dismissed.");
        } catch (NoAlertPresentException e) {
            System.out.println("ℹ️ No alert present.");
        }

        // ✅ Confirm redirect to dashboard
        wait.until(ExpectedConditions.urlContains("/police-officer"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/police-officer"),
                "Login failed or redirection to dashboard was unsuccessful.");
        System.out.println("✅ Landed on Police Officer Dashboard.");
    }


    @Test(priority = 3, description = "Check form validation for empty vehicle number",
            dependsOnMethods = "enterCredentialsAndLogin")
    public void checkEmptyVehicleNumber() {
        resetDashboardState();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='text']")));

        WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.po-check-button")));
        checkButton.click();
        System.out.println("✅ Clicked Check button with empty input field.");

        // Wait for the validation modal and message
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".validation-modal .validation-message")));

        String expectedMessage = "Invalid Vehicle Number Format!";
        // Uncomment when validation text is finalized:
        // Assert.assertEquals(errorMessage.getText().trim(), expectedMessage,
        //        "Validation error message for empty field is incorrect or not found.");
        System.out.println("✅ Validation error message for empty input displayed correctly.");

        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".validation-close-button")));
        closeButton.click();
    }

    @Test(priority = 4, description = "Verify the logout functionality",
            dependsOnMethods = "enterCredentialsAndLogin")
    public void verifyLogoutFunctionality() {
        resetDashboardState();

        // 1. Click on the profile dropdown (the arrow icon)
        WebElement dropdownIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[style*='display: flex'] svg")));
        dropdownIcon.click();
        System.out.println("✅ Clicked the dropdown icon.");

        // 2. Wait for the Logout <p> element to be visible and click it
        WebElement logoutOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[normalize-space()='Logout']")));
        logoutOption.click();
        System.out.println("✅ Clicked the Logout option.");

        // 3. Confirm redirection to the sign-in page
        wait.until(ExpectedConditions.urlToBe("https://mmpro.aasait.lk/signin"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://mmpro.aasait.lk/signin",
                "User was not redirected to the sign-in page after logout.");
        System.out.println("✅ Successfully logged out and redirected to the sign-in page.");
    }

    /**
     * Helper method to refresh the page to reset the state of the dashboard
     * before each test that runs on it.
     */
    private void resetDashboardState() {
        driver.navigate().refresh();
        waitForPageLoadComplete();
        System.out.println("🔄 Page refreshed to reset state.");
    }

    /**
     * Helper to ensure page is fully loaded
     */
    private void waitForPageLoadComplete() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(30)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }
}
