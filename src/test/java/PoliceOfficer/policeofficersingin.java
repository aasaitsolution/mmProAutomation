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

public class policeofficersingin {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    public void testNavigationFlow() {
        try {
            // Start from homepage
            driver.get(BASE_URL);

            // Click on Login link from homepage
            WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Login")));
            loginLink.click();

            // Verify navigation to signin page
            wait.until(ExpectedConditions.urlContains("/signin"));
            Assert.assertTrue(driver.getCurrentUrl().contains("/signin"),
                    "Navigation to signin page failed");

            // Perform login
            WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
            username.sendKeys("police");

            WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
            password.sendKeys("1234abcd");

            WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-btn-primary")
            ));
            signinButton.click();

            // Verify navigation to dashboard
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/police-officer/dashboard"));
            Assert.assertEquals(driver.getCurrentUrl(), BASE_URL + "/police-officer/dashboard",
                    "Failed to navigate to police officer dashboard");

            // Print success message
            System.out.println("Navigation flow completed successfully");
            System.out.println("Final URL: " + driver.getCurrentUrl());

        } catch (Exception e) {
            System.out.println("Test failed! Current URL: " + driver.getCurrentUrl());
            System.out.println("Exception: " + e.getMessage());
            System.out.println("Page source: " + driver.getPageSource());
            throw e;
        }
    }

//    @AfterMethod
//    public void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
}
