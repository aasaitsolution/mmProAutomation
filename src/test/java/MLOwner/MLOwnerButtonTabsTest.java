package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;

import java.time.Duration;

public class MLOwnerButtonTabsTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test(priority = 1)
    public void testLogin() {
        try {
            driver.get("https://mmpro.aasait.lk/");
            System.out.println("Navigated to application homepage");

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/signin'] button")));
            loginButton.click();

            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("sign-in_username")));
            usernameField.sendKeys("pasindu");

            WebElement passwordField = driver.findElement(By.id("sign-in_password"));
            passwordField.sendKeys("12345678");

            WebElement signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
            signInButton.click();

            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            System.out.println("✅ Successfully logged in to ML Owner dashboard");

        } catch (Exception e) {
            System.out.println("❌ Login test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 2, dependsOnMethods = "testLogin")
    public void navigateToLicensesPage() {
        try {
            WebElement viewLicensesLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='/mlowner/home/viewlicenses']")));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    viewLicensesLink);
            Thread.sleep(500); // Allow scroll animation

            try {
                viewLicensesLink.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewLicensesLink);
            }

            wait.until(ExpectedConditions.urlContains("/mlowner/home/viewlicenses"));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("container")));
            System.out.println("✅ Navigated to Licenses page");

            Thread.sleep(5000);

            // Navigate back to dashboard
            driver.navigate().back();
            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            System.out.println("🔙 Returned to ML Owner dashboard");

        } catch (Exception e) {
            System.out.println("❌ Navigation to Licenses page failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }



    @Test(priority = 3, dependsOnMethods = "testLogin")
    public void testRequestedLicensesButton() {
        try {
            System.out.println("=== Testing Requested Licenses Button ===");

            WebElement requestedLicensesButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(), 'Requested Licenses')]/ancestor::div[contains(@class, 'custom-card')]//button[contains(@class, 'ml-card-button')]")));
            requestedLicensesButton.click();
            System.out.println("Clicked Requested Licenses button");

            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.className("ant-modal-content")));
            System.out.println("✅ Requested Licenses modal is visible");

            WebElement modalTitle = modal.findElement(By.className("ant-modal-title"));
            System.out.println("Modal title: " + modalTitle.getText());

            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(., 'Close')]")));
            closeButton.click();
            System.out.println("Closed the modal");

            wait.until(ExpectedConditions.invisibilityOf(modal));
            System.out.println("✅ Modal is no longer visible");

        } catch (Exception e) {
            System.out.println("❌ Requested Licenses test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4, dependsOnMethods = "testLogin")
    public void testMLRequestButton() {
        try {
            WebElement mlRequestButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='/mlowner/home/mlrequest']")));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", mlRequestButton);
            Thread.sleep(500);

            try {
                mlRequestButton.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", mlRequestButton);
            }

            wait.until(ExpectedConditions.urlContains("/mlowner/home/mlrequest"));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("container")));
            System.out.println("✅ Successfully navigated to ML Request page");

            Thread.sleep(5000);

            // Navigate back to dashboard
            driver.navigate().back();
            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            System.out.println("🔙 Returned to ML Owner dashboard");

        } catch (Exception e) {
            System.out.println("❌ Navigation to Licenses page failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("🧹 Browser closed successfully");
        }
    }
}
