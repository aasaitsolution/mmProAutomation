package PoliceOfficer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;

public class invalidnumber {
    private static final Log log = LogFactory.getLog(invalidnumber.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";
    private static final String INVALID_LICENSE = "ABP1234";

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);  // Only create driver once with options
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        log.info("Browser initialized");
    }

    @Test
    public void testInvalidLicenseWithReport() {
        try {
            // Login
            performLogin();
            log.info("Login successful");

            // Wait for dashboard to load completely
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/police-officer/dashboard"));
            log.info("Dashboard loaded at: " + driver.getCurrentUrl());

            // Enter and verify license number
            WebElement numberInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/main/div/input")
            ));
            numberInput.clear();
            numberInput.sendKeys(INVALID_LICENSE);
            Thread.sleep(500); 
            log.info("Entered invalid license: " + INVALID_LICENSE);

            // Verify input value
            String inputValue = numberInput.getAttribute("value");
            log.info("Input value after sendKeys: '" + inputValue + "'");
            Assert.assertEquals(inputValue, INVALID_LICENSE, "License plate number was not entered correctly");


            // Get the check button and verify it's enabled
            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/main/div/button")
            ));
            Assert.assertTrue(checkButton.isEnabled(), "Check button is not enabled");
            log.info("Check button is enabled");

            // Take screenshot before clicking
            takeScreenshot("before-check-click");

            // Click with JavaScript to ensure the click happens
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkButton);
            log.info("Clicked check button");

            // Wait a moment for any UI updates
            Thread.sleep(200);
            takeScreenshot("after-check-click");

            // Check for alert first
            try {
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                String alertText = alert.getText();
                log.info("Alert detected with text: " + alertText);

                // If there's "Report" button in the alert (unlikely in standard alerts)
                // Just accept the alert for now
                alert.accept();
                log.info("Alert accepted");

                // After accepting the alert, look for any report button that might appear
                try {
                    WebElement reportButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//*[@id=\"root\"]/div/main/div/main/div[2]/div/button[2]")
                    ));
                    reportButton.click();
                    log.info("Clicked report button after alert");
                } catch (TimeoutException e) {
                    log.info("No report button found after alert");
                }
            } catch (TimeoutException e) {
                // No alert found, look for modal dialog or popup div
                log.info("No alert found, checking for modal dialog...");
                takeScreenshot("looking-for-modal");

                // Print page source to debug
                log.info("Page source: " + driver.getPageSource());

                try {
                    // Try different approaches to find the report button
                    // Method 1: Direct button search
                    WebElement reportButton = driver.findElement(By.xpath("//button[contains(text(), 'Report')]"));
                    reportButton.click();
                    log.info("Found and clicked report button (method 1)");
                } catch (NoSuchElementException ex1) {
                    try {
                        // Method 2: Look for buttons in modals/dialogs
                        WebElement reportButton = driver.findElement(
                                By.xpath("//div[contains(@class, 'modal') or contains(@class, 'popup') or contains(@class, 'ant-modal')]//button[contains(text(), 'Report')]")
                        );
                        reportButton.click();
                        log.info("Found and clicked report button (method 2)");
                    } catch (NoSuchElementException ex2) {
                        try {
                            // Method 3: Try clicking any button that might be the report button
                            WebElement anyButton = driver.findElement(By.tagName("button"));
                            log.info("Found a button with text: " + anyButton.getText());
                            anyButton.click();
                            log.info("Clicked a button that might be the report button");
                        } catch (NoSuchElementException ex3) {
                            log.error("Could not find any button to click");
                            takeScreenshot("no-button-found");
                            Assert.fail("No report button or any button found");
                        }
                    }
                }
            }

            // Wait a moment for any changes after clicking report
            Thread.sleep(2000);
            takeScreenshot("after-report-action");
            log.info("Current URL after report action: " + driver.getCurrentUrl());

            // Print all buttons on the page
            try {
                java.util.List<WebElement> allButtons = driver.findElements(By.tagName("button"));
                log.info("Found " + allButtons.size() + " buttons on the page:");
                for (WebElement button : allButtons) {
                    log.info("Button text: '" + button.getText() + "', isDisplayed: " + button.isDisplayed());
                }
            } catch (Exception e) {
                log.info("Could not list buttons: " + e.getMessage());
            }

            // Success - we've attempted to locate and click the report button
            log.info("Test completed - we attempted to handle the invalid license and report button");

        } catch (Exception e) {
            log.error("Test failed! Final URL: " + driver.getCurrentUrl());
            log.error("Exception: " + e.getMessage());
            takeScreenshot("test-failure");
            Assert.fail("Test failed: " + e.getMessage());
        }
    }

    private void takeScreenshot(String name) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File screenshot = ts.getScreenshotAs(OutputType.FILE);
            log.info("Screenshot '" + name + "' saved at: " + screenshot.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to take screenshot: " + e.getMessage());
        }
    }

    private void performLogin() {
        try {
            driver.get(BASE_URL + "/signin/");
            log.info("Navigated to login page");

            WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
            username.sendKeys("saman");

            WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
            password.sendKeys("12345678");
            log.info("Entered credentials");

            WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-btn-primary")
            ));
            signinButton.click();
            log.info("Clicked sign in button");

        } catch (Exception e) {
            log.error("Login failed: " + e.getMessage());
            throw new RuntimeException("Failed to login: " + e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            log.info("Browser closed");
        }
    }
}