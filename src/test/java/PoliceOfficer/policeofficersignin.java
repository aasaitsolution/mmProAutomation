package PoliceOfficer;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class policeofficersignin extends BaseTest {

    @BeforeMethod
    public void navigateToHome() {
        // Use driver from BaseTest
        driver.get("https://mmpro.aasait.lk/");
        System.out.println("🚀 Browser launched and navigated to the homepage.");
    }

    @Test(priority = 1, description = "Navigate from homepage to the Sign In page")
    public void navigateToSignInPage() {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginButton.click();

        wait.until(ExpectedConditions.urlContains("/signin"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/signin"),
                "Failed to navigate to the sign-in page.");
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

        // Handle alert if it appears
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
    }

    @Test(priority = 3, description = "Check form validation for empty vehicle number", dependsOnMethods = "enterCredentialsAndLogin")
    public void checkEmptyVehicleNumber() {
        driver.navigate().refresh();
        System.out.println("🔄 Page refreshed to reset state.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='text']")));

        WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.po-check-button")));
        checkButton.click();
        System.out.println("✅ Clicked Check button with empty input field.");

        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".validation-modal .validation-message")));

        String expectedMessage = "Invalid Vehicle Number Format!";
        Assert.assertEquals(errorMessage.getText().trim(), expectedMessage,
                "Validation error message for empty field is incorrect.");
        System.out.println("✅ Validation error message displayed correctly.");

        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".validation-close-button")));
        closeButton.click();
    }

    @Test(priority = 4, description = "Verify the logout functionality", dependsOnMethods = "enterCredentialsAndLogin")
    public void verifyLogoutFunctionality() {
        driver.navigate().refresh();
        System.out.println("🔄 Page refreshed to reset state.");

        WebElement dropdownIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[style*='display: flex'] svg")));
        dropdownIcon.click();
        System.out.println("✅ Clicked the dropdown icon.");

        WebElement logoutOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[normalize-space()='Logout']")));
        logoutOption.click();
        System.out.println("✅ Clicked the Logout option.");

        wait.until(ExpectedConditions.urlToBe("https://mmpro.aasait.lk/signin"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://mmpro.aasait.lk/signin",
                "User was not redirected to the sign-in page after logout.");
        System.out.println("✅ Successfully logged out and redirected to the sign-in page.");
    }
}
