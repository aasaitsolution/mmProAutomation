package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import java.time.Duration;


public class RoyaltyPayementTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito"); // clean session
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test(priority = 1)
    public void login() {
        try {
        driver.get("https://mmpro.aasait.lk/");
            // Wait and click on the 'Login' button
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
            loginButton.click();

            // Wait for the Sign-in page to load and the username field to be visible
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            usernameField.sendKeys("pasindu");

            // Wait for the password field to be visible
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
            passwordField.sendKeys("12345678");
            System.out.println("👤 Entered username and password");

            // Wait and click on the 'Sign in' button
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
            signInButton.click();
            System.out.println("🚀 Submitted username and password");

            // Wait for login to complete - you might need to wait for a specific element that appears after successful login
            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            System.out.println("🔐 Login successful.");

        } catch (Exception e) {
            System.out.println("❌ Error during sign in: ⚠️ " + e.getMessage());
            e.printStackTrace();
        }
    }
        


    @Test(priority = 2, dependsOnMethods = "login")
    public void openRoyaltyModal() {
        WebElement royaltyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")));
        royaltyBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal-title') and contains(text(),'Royalty Payment')]")));
        System.out.println("💰 Royalty modal opened.");
    }

    @Test(priority = 3, dependsOnMethods = "openRoyaltyModal")
    public void submitRoyaltyForm() {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder*='Enter Payment Amount']")));
        input.sendKeys("1000");

        WebElement payNowBtn = driver.findElement(By.cssSelector(".pay-now-button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payNowBtn);
        payNowBtn.click();

        System.out.println("🧾 Payment submitted. Waiting for PayHere...");
    }

    @Test(priority = 4, dependsOnMethods = "submitRoyaltyForm")
    public void selectMasterCardAndPay() {
        try {
            // Wait for iframe and switch
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.tagName("iframe")));

            // Select MasterCard
            WebElement masterCardOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#payment_container_MASTER")));
            masterCardOption.click();
            System.out.println("💳 MasterCard selected.");

            // Switch to nested iframe that contains the card form
            WebElement innerIframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
            driver.switchTo().frame(innerIframe);
            System.out.println("🔄 Switched to nested iframe.");

            // Wait for input fields to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cardHolderName")));

            // Fill the form with dummy test data
            driver.findElement(By.id("cardHolderName")).sendKeys("Sankavi");
            driver.findElement(By.id("cardNo")).sendKeys("4916217501611292"); 
            driver.findElement(By.id("cardSecureId")).sendKeys("123");
            driver.findElement(By.id("cardExpiry")).sendKeys("09/26");

            // Click Pay
            WebElement payButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.btn.btn-primary")
            ));
            payButton.click();

            System.out.println("✅ Dummy card details submitted. Payment in progress...");

            // wait for success model
            Thread.sleep(4000);

            System.out.println("🎉 Payment Approved modal is visible!");

        } catch (Exception e) {
            System.out.println("⚠️ Failed to process payment in iframe.");
            e.printStackTrace();
        } finally {
            driver.switchTo().defaultContent(); // always switch back
        }
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
            System.out.println("🚪 Browser closed.");
        }
    }
}