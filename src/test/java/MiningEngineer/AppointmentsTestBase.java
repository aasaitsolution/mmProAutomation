package MiningEngineer;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppointmentsTestBase {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "http://localhost:5173";
    protected static final String SCREENSHOT_DIR = "test-output/screenshots/";

    @BeforeClass
    public void setUp() {
        try {
            // Setup WebDriverManager to handle browser driver
            WebDriverManager.chromedriver().setup();

            // Configure Chrome options
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--incognito");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--remote-allow-origins=*");
            options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

            // Initialize driver with enhanced capabilities
            driver = new ChromeDriver(options);

            // Configure wait with polling
            wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

            // Create screenshots directory if not exists
            createScreenshotDirectory();

            // Perform login
            login("amal", "12345678");

            // Verify successful navigation to dashboard
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/dashboard"),
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".user-profile"))
            ));
        } catch (Exception e) {
            captureScreenshot("setup_failure");
            throw new RuntimeException("Setup failed: " + e.getMessage(), e);
        }
    }

    protected void login(String username, String password) {
        try {
            driver.get(BASE_URL + "/signin");

            // Wait for and enter username
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("sign-in_username")));
            usernameField.clear();
            usernameField.sendKeys(username);

            // Wait for and enter password
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("sign-in_password")));
            passwordField.clear();
            passwordField.sendKeys(password);

            // Click login button
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-btn-primary")));
            signInButton.click();

            // Wait for login to complete
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/dashboard"),
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector(".user-profile"))
            ));

        } catch (Exception e) {
            captureScreenshot("login_failure");
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }

    protected void captureScreenshot(String testName) {
        try {
            // Create timestamp for unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            // Take screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Save to file
            Path path = Paths.get(SCREENSHOT_DIR + testName + "_" + timestamp + ".png");
            Files.copy(screenshot.toPath(), path);

        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

    private void createScreenshotDirectory() {
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create screenshot directory: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        try {
            if (driver != null) {
                // Capture final screenshot before quitting
                captureScreenshot("final_state");
                driver.quit();
            }
        } catch (Exception e) {
            System.err.println("Error during teardown: " + e.getMessage());
        }
    }
}