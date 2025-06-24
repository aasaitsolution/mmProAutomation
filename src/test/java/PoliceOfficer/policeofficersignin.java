package PoliceOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
        driver.get("http://localhost:5173/");
    }

    @Test(priority = 1)
    public void navigateToSignInPage() {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginButton.click();
        System.out.println("✅ Navigated to Sign In page");
    }

    @Test(priority = 2)
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
        System.out.println("✅ Submitted credentials");

        // Handle alert (if present)
        try {
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
            System.out.println("⚠️ Alert dismissed");
        } catch (NoAlertPresentException e) {
            System.out.println("ℹ️ No alert to handle");
        }

        wait.until(ExpectedConditions.urlContains("/police-officer"));
        System.out.println("✅ Landed on Police Officer Dashboard");
    }

    @Test(priority = 3)
    public void checkInvalidVehicleNumber() {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='text']")));
        inputField.sendKeys("ABX1234");

        WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.po-check-button")));
        checkButton.click();
        System.out.println("✅ Submitted vehicle number for check");

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("pov-validBadge")));
        String modalText = modal.getText();
        System.out.println("ℹ️ Modal Text: " + modalText);

        if (modalText.toLowerCase().contains("valid")) {
            System.out.println("✅ Vehicle number marked as Valid as expected.");
        } else {
            System.out.println("❌ Unexpected modal message: " + modalText);
        }
    }

    @AfterClass
    public void tearDown() {
        try {
            Thread.sleep(3000); // small pause for final visibility
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();
        System.out.println("🔒 Browser closed");
    }
}