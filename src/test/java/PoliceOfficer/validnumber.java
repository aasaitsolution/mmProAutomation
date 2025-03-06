package PoliceOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class validnumber {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";
    private static final String VALID_LICENSE = "LA4565";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(100)); // Increased timeout
    }

    @Test
    public void testValidLicensePlate() throws InterruptedException {
        try {
            // Login
            performLogin();

            // Wait for dashboard to load completely
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/police-officer/dashboard"));

            // Add a small delay to ensure page is fully loaded
            Thread.sleep(2000);

            // Enter and verify license number
            WebElement numberInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/main/div/input")
            ));
            numberInput.clear();
            numberInput.sendKeys(VALID_LICENSE);

            // Verify input value
            Assert.assertEquals(numberInput.getAttribute("value"), VALID_LICENSE,
                    "License plate number was not entered correctly");

            // Get the check button and verify it's enabled
            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/main/div/button")
            ));
            Assert.assertTrue(checkButton.isEnabled(), "Check button is not enabled");

            // Print current URL before click
            System.out.println("URL before click: " + driver.getCurrentUrl());

            // Click with JavaScript to ensure the click happens
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkButton);

            // Print URL immediately after click
            System.out.println("URL immediately after click: " + driver.getCurrentUrl());

            // Wait for a moment to see if there's any immediate change
            Thread.sleep(1000);
            System.out.println("URL 1 second after click: " + driver.getCurrentUrl());

            // Check for any error messages or loading indicators
//            try {
//                WebElement errorMessage = driver.findElement(By.className("ant-notification-notice"));
//                if (errorMessage.isDisplayed()) {
//                    System.out.println("Error message found: " + errorMessage.getText());
//                }
//            } catch (NoSuchElementException e) {
//                System.out.println("No error message found");
//            }

            // Wait for URL change with a custom condition that prints progress
            wait.until(driver -> {
                String currentUrl = driver.getCurrentUrl();
                System.out.println("Checking URL: " + currentUrl);
                return currentUrl.equals(BASE_URL + "/police-officer/valid");
            });

            // Final verification
            String finalUrl = driver.getCurrentUrl();
            Assert.assertEquals(finalUrl, BASE_URL + "/police-officer/valid",
                    "Failed to navigate to valid page");

        } catch (Exception e) {
            System.out.println("Test failed! Final URL: " + driver.getCurrentUrl());
            System.out.println("Exception: " + e.getMessage());

            // Print page source to see what's on the page
            System.out.println("Page source: " + driver.getPageSource());

            throw e;
        }
    }

    private void performLogin() {
        driver.get(BASE_URL + "/signin/");

        WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
        username.sendKeys("police");

        WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
        password.sendKeys("1234abcd");

        WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".ant-btn-primary")
        ));
        signinButton.click();
    }

//    @AfterMethod
//    public void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
}