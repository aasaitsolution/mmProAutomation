//package PoliceOfficer;
//
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.testng.Assert;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//import java.time.Duration;
//
//public class policeofficersingin {
//    private WebDriver driver;
//    private WebDriverWait wait;
//    private static final String BASE_URL = "http://localhost:5173";
//
//    @BeforeMethod
//    public void setup() {
//        driver = new ChromeDriver();
//        driver.manage().window().maximize();
//        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//    }
//
//    @Test
//    public void testNavigationFlow() {
//        try {
//            // Start from homepage
//            driver.get(BASE_URL);
//
//            // Click on Login link from homepage
//            WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Login")));
//            loginLink.click();
//
//            // Verify navigation to signin page
//            wait.until(ExpectedConditions.urlContains("/signin"));
//            Assert.assertTrue(driver.getCurrentUrl().contains("/signin"),
//                    "Navigation to signin page failed");
//
//            // Perform login
//            WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
//            username.sendKeys("police");
//
//            WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
//            password.sendKeys("1234abcd");
//
//            WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
//                    By.cssSelector(".ant-btn-primary")
//            ));
//            signinButton.click();
//
//            // Verify navigation to dashboard
//            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/police-officer/dashboard"));
//            Assert.assertEquals(driver.getCurrentUrl(), BASE_URL + "/police-officer/dashboard",
//                    "Failed to navigate to police officer dashboard");
//
//            // Print success message
//            System.out.println("Navigation flow completed successfully");
//            System.out.println("Final URL: " + driver.getCurrentUrl());
//
//        } catch (Exception e) {
//            System.out.println("Test failed! Current URL: " + driver.getCurrentUrl());
//            System.out.println("Exception: " + e.getMessage());
//            System.out.println("Page source: " + driver.getPageSource());
//            throw e;
//        }
//    }
//
////    @AfterMethod
////    public void tearDown() {
////        if (driver != null) {
////            driver.quit();
////        }
////    }
//}
package PoliceOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import io.qameta.allure.*;

@Epic("User Authentication")
@Feature("Police Officer License Validation")
public class policeofficersignin {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";
    private static final String VALID_LICENSE = "LA4565";

    @BeforeMethod
    @Step("Setup WebDriver and Open Browser")
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(100)); // Increased timeout
    }

    @Test
    @Story("Validate License Plate Entry and Navigation")
    @Description("This test verifies if a valid license plate number allows navigation to the correct page")
    @Severity(SeverityLevel.CRITICAL)
    public void testValidLicensePlate() throws InterruptedException {
        try {
            // Login
            performLogin();

            // Wait for dashboard to load completely
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/police-officer/dashboard"));

            Allure.step("Navigated to Police Officer Dashboard");

            // Add a small delay to ensure page is fully loaded
            Thread.sleep(2000);

            // Enter and verify license number
            WebElement numberInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/main/div/input")
            ));
            numberInput.clear();
            numberInput.sendKeys(VALID_LICENSE);
            Allure.step("Entered license plate number: " + VALID_LICENSE);

            // Verify input value
            Assert.assertEquals(numberInput.getAttribute("value"), VALID_LICENSE,
                    "License plate number was not entered correctly");

            // Get the check button and verify it's enabled
            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/main/button")
            ));
            Assert.assertTrue(checkButton.isEnabled(), "Check button is not enabled");
            Allure.step("Check button is enabled");

            // Click with JavaScript to ensure the click happens
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkButton);
            Allure.step("Clicked on Check button");

            // Wait for navigation to the correct page
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/police-officer/valid"));
            String finalUrl = driver.getCurrentUrl();
            Assert.assertEquals(finalUrl, BASE_URL + "/police-officer/valid",
                    "Failed to navigate to valid page");
            Allure.step("Successfully navigated to the valid license page");

        } catch (Exception e) {
            Allure.addAttachment("Error Screenshot", new ByteArrayInputStream(
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES)));
            throw e;
        }
    }

    @Step("Perform Login with Valid Credentials")
    private void performLogin() {
        driver.get(BASE_URL + "/signin/");
        Allure.step("Opened Sign-in Page");

        WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
        username.sendKeys("police");
        Allure.step("Entered Username");

        WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
        password.sendKeys("1234abcd");
        Allure.step("Entered Password");

        WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".ant-btn-primary")
        ));
        signinButton.click();
        Allure.step("Clicked Sign-in Button");
    }

    @AfterMethod
    @Step("Close Browser")
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
