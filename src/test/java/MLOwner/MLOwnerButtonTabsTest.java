package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import java.time.Duration;

public class MLOwnerButtonTabsTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();
    }

    @Test(priority = 1)
    public void testLogin() {
        try {
            // Navigate to application
            driver.get("https://mmpro.aasait.lk/");
            System.out.println("Navigated to application homepage");

            // Click login button
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/signin'] button")));
            loginButton.click();
            System.out.println("Clicked login button");

            // Fill login form
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("sign-in_username")));
            usernameField.sendKeys("pasindu");

            WebElement passwordField = driver.findElement(By.id("sign-in_password"));
            passwordField.sendKeys("12345678");

            WebElement signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
            signInButton.click();
            System.out.println("Submitted login credentials");

            // Verify successful login
            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            System.out.println("Successfully logged in to ML Owner dashboard");

        } catch (Exception e) {
            System.out.println("Login test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 2, dependsOnMethods = "testLogin")
    public void testViewLicensesButton() throws InterruptedException {
        try {
            System.out.println("=== Testing View Licenses Button ===");

            WebElement viewLicensesButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//span[contains(text(), 'View Licenses')]/ancestor::div[contains(@class, 'custom-card')]//button[contains(@class, 'ml-card-button')]")));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", viewLicensesButton);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewLicensesButton);

            wait.until(ExpectedConditions.urlContains("https://mmpro.aasait.lk/mlowner/home/viewlicenses"));
            System.out.println("Successfully navigated to View Licenses page");

            driver.navigate().back();
            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            System.out.println("Returned to ML Owner dashboard");

        } catch (Exception e) {
            System.out.println("View Licenses test failed: " + e.getMessage());
            throw e;
        }
    }



    @Test(priority = 3, dependsOnMethods = "testLogin")
    public void testMLRequestButton() {
        try {
            System.out.println("=== Testing ML Request Button ===");

            // Find and click the ML Request button
            WebElement mlRequestButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(), 'ML Request')]/ancestor::div[contains(@class, 'custom-card')]//button[contains(@class, 'ml-card-button')]")));
            mlRequestButton.click();
            System.out.println("Clicked ML Request button");

            // Verify navigation
            wait.until(ExpectedConditions.urlContains("/mlowner/home/mlrequest"));
            System.out.println("Successfully navigated to ML Request page");

            // Return to dashboard
            driver.navigate().back();
            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            System.out.println("Returned to ML Owner dashboard");

        } catch (Exception e) {
            System.out.println("ML Request test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4, dependsOnMethods = "testLogin")
    public void testRequestedLicensesButton() {
        try {
            System.out.println("=== Testing Requested Licenses Button ===");

            // Find and click the Requested Licenses button
            WebElement requestedLicensesButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(), 'Requested Licenses')]/ancestor::div[contains(@class, 'custom-card')]//button[contains(@class, 'ml-card-button')]")));
            requestedLicensesButton.click();
            System.out.println("Clicked Requested Licenses button");

            // Verify modal appears
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.className("ant-modal-content")));
            System.out.println("Requested Licenses modal is visible");

            // Verify modal title
            WebElement modalTitle = modal.findElement(By.className("ant-modal-title"));
            System.out.println("Modal title: " + modalTitle.getText());

            // Close modal
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(., 'Close')]")));
            closeButton.click();
            System.out.println("Closed the modal");

            // Verify modal is closed
            wait.until(ExpectedConditions.invisibilityOf(modal));
            System.out.println("Modal is no longer visible");

        } catch (Exception e) {
            System.out.println("Requested Licenses test failed: " + e.getMessage());
            throw e;
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed successfully");
        }
    }
}
