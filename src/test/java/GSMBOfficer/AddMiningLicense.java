package GSMBOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class AddMiningLicense {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(100)); // Increased timeout
    }

    @Test
    public void testAddMiningLicense() throws InterruptedException {
        try {
            // Login
            performLogin();

            // Wait for dashboard to load completely
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));

            // Add a small delay to ensure page is fully loaded
            Thread.sleep(2000);

            // Click "Add New License" button
            WebElement addNewLicenseButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-btn-default > span:nth-child(1)")
            ));
            addNewLicenseButton.click();

            // Wait for license number field and enter value
            WebElement licenseNumberField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='licenseNumber']")
            ));
            licenseNumberField.sendKeys("LLL/100/111");

            // Verify license number is entered correctly
            Assert.assertEquals(licenseNumberField.getAttribute("value"), "LLL/100/111",
                    "License number was not entered correctly");

            // Enter the rest of the license details
            WebElement ownerNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='ownerName']")
            ));
            ownerNameField.sendKeys("Kasun");

            WebElement validityStartField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='validityStart']")
            ));
            validityStartField.clear();
            validityStartField.sendKeys("30/01/2025");

            WebElement validityEndField = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id='endDate']")
            ));
            validityEndField.clear();
            validityEndField.sendKeys("31/03/2025");

            WebElement capacityField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='capacity']")
            ));
            capacityField.sendKeys("1000");

            WebElement mobileField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='mobile']")
            ));
            mobileField.sendKeys("0789076745");

            WebElement locationField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='location']")
            ));
            locationField.sendKeys("Madampe");

            // Click to create the license
            WebElement createLicenseButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='root']/div/main/div/form/div/div[8]/div/div/div/div/div/div/button[1]")
            ));
            createLicenseButton.click();
            // Wait for the page to navigate to the dashboard page after successful addition
            wait.until(driver -> driver.getCurrentUrl().equals(BASE_URL + "/gsmb/dashboard"));

            // Final verification
            String finalUrl = driver.getCurrentUrl();
            Assert.assertEquals(finalUrl, BASE_URL + "/gsmb/dashboard", "Failed to navigate to the dashboard page");

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
        username.sendKeys("nimal");

        WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
        password.sendKeys("12345678");

        WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".ant-btn-primary")
        ));
        signinButton.click();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
