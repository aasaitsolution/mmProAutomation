package GSMBOfficer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class GsmbOfficerSignin {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://mmpro.aasait.lk";
    private static final String SIGNIN_URL = BASE_URL + "/signin";

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(100)); // Increased timeout
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    public void testNavigateToSigninPage() {
        driver.get(SIGNIN_URL);

        // Verify the signin page loads correctly
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, SIGNIN_URL, "Failed to navigate to signin page");

        // Verify signin form elements are present
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("sign-in_username")));
        WebElement passwordField = driver.findElement(By.id("sign-in_password"));
        WebElement signInButton = driver.findElement(By.cssSelector(".ant-btn-primary"));

        Assert.assertTrue(usernameField.isDisplayed(), "Username field is not visible");
        Assert.assertTrue(passwordField.isDisplayed(), "Password field is not visible");
        Assert.assertTrue(signInButton.isDisplayed(), "Sign in button is not visible");

        System.out.println("Signin page loaded successfully with all required elements");
    }

    @Test(priority = 2)
    public void testValidLogin() {
        driver.get(SIGNIN_URL);

        try {
            // Enter valid credentials
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            usernameField.clear();
            usernameField.sendKeys("nimal");

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
            passwordField.clear();
            passwordField.sendKeys("12345678");

            // Click sign in button
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-btn-primary")));
            signInButton.click();

            // Verify successful login by checking dashboard
            WebElement dashboardElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-card-body h3")));
            Assert.assertTrue(dashboardElement.isDisplayed(), "Dashboard not loaded after login");

            System.out.println("Valid login test passed successfully");

        } catch (Exception e) {
            Assert.fail("Valid login test failed: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void testInvalidUsernameLogin() {
        driver.get(SIGNIN_URL);

        try {
            // Enter invalid username with valid password
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            usernameField.clear();
            usernameField.sendKeys("invaliduser");

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
            passwordField.clear();
            passwordField.sendKeys("12345678");

            // Click sign in button
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-btn-primary")));
            signInButton.click();

            // Wait for error message or verify login failed
            Thread.sleep(2000); // Allow time for error message to appear

            // Verify we're still on signin page (login failed)
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("signin"), "Should remain on signin page after invalid login");

            System.out.println("Invalid username test passed - login appropriately rejected");

        } catch (Exception e) {
            Assert.fail("Invalid username test failed: " + e.getMessage());
        }
    }

    @Test(priority = 4)
    public void testInvalidPasswordLogin() {
        driver.get(SIGNIN_URL);

        try {
            // Enter valid username with invalid password
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            usernameField.clear();
            usernameField.sendKeys("nimal");

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
            passwordField.clear();
            passwordField.sendKeys("wrongpassword");

            // Click sign in button
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-btn-primary")));
            signInButton.click();

            // Wait for error handling
            Thread.sleep(2000);

            // Verify we're still on signin page (login failed)
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("signin"), "Should remain on signin page after invalid login");

            System.out.println("Invalid password test passed - login appropriately rejected");

        } catch (Exception e) {
            Assert.fail("Invalid password test failed: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void testEmptyFieldsLogin() {
        driver.get(SIGNIN_URL);

        try {
            // Click sign in button without entering credentials
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-btn-primary")));
            signInButton.click();

            // Wait for validation messages
            Thread.sleep(2000);

            // Verify we're still on signin page
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("signin"), "Should remain on signin page when fields are empty");

            System.out.println("Empty fields test passed - form validation working");

        } catch (Exception e) {
            Assert.fail("Empty fields test failed: " + e.getMessage());
        }
    }

    @Test(priority = 6)
    public void testSigninFormElementsInteraction() {
        driver.get(SIGNIN_URL);

        try {
            // Test username field interaction
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            Assert.assertTrue(usernameField.isEnabled(), "Username field should be enabled");

            usernameField.click();
            usernameField.sendKeys("test");
            usernameField.clear();

            // Test password field interaction
            WebElement passwordField = driver.findElement(By.id("sign-in_password"));
            Assert.assertTrue(passwordField.isEnabled(), "Password field should be enabled");

            passwordField.click();
            passwordField.sendKeys("test");
            passwordField.clear();

            // Test sign in button
            WebElement signInButton = driver.findElement(By.cssSelector(".ant-btn-primary"));
            Assert.assertTrue(signInButton.isEnabled(), "Sign in button should be enabled");

            System.out.println("Form elements interaction test passed");

        } catch (Exception e) {
            Assert.fail("Form elements interaction test failed: " + e.getMessage());
        }
    }
}