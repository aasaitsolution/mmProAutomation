package PoliceOfficer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;
import java.util.List;


public class invalidnumber {
    private static final Log log = LogFactory.getLog(invalidnumber.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://mmpro.aasait.lk";
    private static final String INVALID_LICENSE = "ABP1234";

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);  // Only create driver once with options
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        System.out.println("✅ Browser started");
    }

    @Test(priority = 1)
    public void loginToPoliceDashboard() {
        try {
            driver.get(BASE_URL + "/signin/");
            log.info("Navigated to login page");

            WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
            username.sendKeys("saman");

            WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
            password.sendKeys("12345678");

            WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-btn-primary")));
            signinButton.click();
            log.info("Clicked sign in button");

            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/police-officer/dashboard"));
            System.out.println("🔐 Logged in successfully");
        } catch (Exception e) {
            System.out.println("❌ Login failed: " + e.getMessage());
            Assert.fail("Login failed");
        }
    }

    @Test(priority = 2, dependsOnMethods = {"loginToPoliceDashboard"})
public void enterInvalidLicenseNumber() {
    try {
        WebElement numberInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input.po-input-box")));
        numberInput.clear();
        numberInput.sendKeys(INVALID_LICENSE);

        String inputValue = numberInput.getAttribute("value");
        Assert.assertEquals(inputValue, INVALID_LICENSE, "License plate number was not entered correctly");

        WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.po-check-button")));
        Assert.assertTrue(checkButton.isEnabled(), "Check button is not enabled");

        takeScreenshot("before-check-click");
        checkButton.click();
        takeScreenshot("after-check-click");

            System.out.println("🚗 Entered invalid license and clicked check");

    } catch (Exception e) {
            System.out.println("❌ License input failed: " + e.getMessage());
        takeScreenshot("input-field-failure");
        Assert.fail("Input field interaction failed");
    }
}

    @Test(priority = 3, dependsOnMethods = {"enterInvalidLicenseNumber"})
    public void clickCheckButtonAndCapture() {
        try {
            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/main/div/button")));
            Assert.assertTrue(checkButton.isEnabled());

            takeScreenshot("before-click-check");

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkButton);
            Thread.sleep(500);

            takeScreenshot("after-click-check");
            log.info("Clicked check and captured screenshots");

        } catch (Exception e) {
            log.error("Check button interaction failed: " + e.getMessage());
            Assert.fail("Check button failed");
        }
    }

    @Test(priority = 4, dependsOnMethods = {"clickCheckButtonAndCapture"})
    public void handleAlertOrFindReportButton() {
        try {
            // Alert handling
            try {
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                log.info("Alert Text: " + alert.getText());
                alert.accept();
                log.info("Alert accepted");
            } catch (TimeoutException e) {
                log.info("No alert found");
            }

            // Try clicking report button
            boolean reportClicked = false;
            try {
                WebElement reportBtn = driver.findElement(By.xpath("//button[contains(text(), 'Report')]"));
                reportBtn.click();
            System.out.println("📢 Report button clicked");
                reportClicked = true;
            } catch (NoSuchElementException ignored) {}

            if (!reportClicked) {
                try {
                    WebElement modalReportBtn = driver.findElement(By.xpath("//div[contains(@class, 'modal') or contains(@class, 'popup')]//button[contains(text(), 'Report')]"));
                    modalReportBtn.click();
                    log.info("Clicked report button (modal)");
                    reportClicked = true;
                } catch (NoSuchElementException ignored) {}
            }

            if (!reportClicked) {
                log.warn("No report button found. Attempting fallback click on first visible button.");
                List<WebElement> buttons = driver.findElements(By.tagName("button"));
                for (WebElement button : buttons) {
                    if (button.isDisplayed()) {
                        log.info("Fallback button clicked: " + button.getText());
                        button.click();
                        reportClicked = true;
                        break;
                    }
                }
            }

            if (!reportClicked) {
                takeScreenshot("no-report-found");
                Assert.fail("No report button found and clicked");
            }

            Thread.sleep(1000);
            takeScreenshot("after-report-action");

        } catch (Exception e) {
            takeScreenshot("error-during-report");
            log.error("Failed to handle alert or report button: " + e.getMessage());
            Assert.fail("Failed in report step");
        }
    }

    @Test(priority = 5, dependsOnMethods = {"handleAlertOrFindReportButton"})
    public void printAllButtonsOnPage() {
        try {
            List<WebElement> allButtons = driver.findElements(By.tagName("button"));
            log.info("Total buttons found: " + allButtons.size());
            for (WebElement btn : allButtons) {
                log.info("Button Text: '" + btn.getText() + "', Displayed: " + btn.isDisplayed());
            }
        } catch (Exception e) {
            log.warn("Unable to print buttons: " + e.getMessage());
        }
    }

    private void takeScreenshot(String name) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File screenshot = ts.getScreenshotAs(OutputType.FILE);
            log.info("Screenshot [" + name + "] saved at: " + screenshot.getAbsolutePath());
        } catch (Exception e) {
            log.warn("Screenshot failed: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            log.info("Browser closed");
        }
    }
}