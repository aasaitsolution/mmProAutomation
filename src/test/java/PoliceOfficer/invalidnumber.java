package PoliceOfficer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class invalidnumber {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private static final String BASE_URL = "http://localhost:5173";
    private static final String INVALID_LICENSE = "ABC2";
    private static final String USERNAME = "police";
    private static final String PASSWORD = "1234abcd";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        js = (JavascriptExecutor) driver;
    }

    @Test
    public void testPoliceOfficerFeatures() throws InterruptedException {
        try {
            // Navigate to base URL
            driver.get(BASE_URL);
            System.out.println("Navigated to base URL: " + BASE_URL);

            // Login process
            performLogin();

            // Test language switching
            testLanguageSwitching();

            // Test invalid license check
            testInvalidLicense();

            System.out.println("All police officer tests passed successfully!");

        } catch (Exception e) {
            System.out.println("Test failed! Current URL: " + driver.getCurrentUrl());
            System.out.println("Exception: " + e.getMessage());
            throw e;
        }
    }

    private void performLogin() {
        // Click login button on home page
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")
        ));
        loginButton.click();

        // Enter username
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("sign-in_username")
        ));
        usernameField.sendKeys(USERNAME);

        // Enter password
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("sign-in_password")
        ));
        passwordField.sendKeys(PASSWORD);

        // Click sign in button
        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']")
        ));
        signInButton.click();
        System.out.println("Successfully logged in");

        // Verify redirect to police officer page
        wait.until(ExpectedConditions.urlContains("/police-officer"));
        System.out.println("Redirected to police officer home page");
    }

    private void testLanguageSwitching() throws InterruptedException {
        // Test Tamil language switch
        WebElement tamilButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='தமிழ்']]")
        ));
        tamilButton.click();
        Thread.sleep(2000);

        // Switch back to English from Tamil
        WebElement englishFromTamil = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='English']]")
        ));
        englishFromTamil.click();
        System.out.println("Tamil language switch test passed");

        // Test Sinhala language switch
        WebElement sinhalaButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='සිංහල']]")
        ));
        sinhalaButton.click();
        Thread.sleep(2000);

        // Switch back to English from Sinhala
        WebElement englishFromSinhala = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='English']]")
        ));
        englishFromSinhala.click();
        System.out.println("Sinhala language switch test passed");
    }

    private void testInvalidLicense() {
        // Wait for and locate the license input field
        WebElement licenseInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='root']/div/main/div/main/div/input")
        ));

        // Clear and enter license number using JavaScript
        js.executeScript("arguments[0].value = '';", licenseInput);
        js.executeScript("arguments[0].value = arguments[1];", licenseInput, INVALID_LICENSE);

        // Verify entered value
        String enteredValue = licenseInput.getAttribute("value");
        Assert.assertEquals(enteredValue, INVALID_LICENSE, "License number was not entered correctly");
        System.out.println("Entered license number: " + enteredValue);

        // Click check button
        WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id='root']/div/main/div/main/button")
        ));
        js.executeScript("arguments[0].click();", checkButton);
        System.out.println("Clicked check button");

        // Verify alert
        wait.until(ExpectedConditions.alertIsPresent());
        String alertText = driver.switchTo().alert().getText();
        Assert.assertTrue(alertText.contains("Invalid license"), "Expected alert about invalid license");
        driver.switchTo().alert().accept();
        System.out.println("Invalid license test passed");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed");
        }
    }
}