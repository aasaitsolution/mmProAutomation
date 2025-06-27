package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class mlownerhome {

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
    public void testLogin() throws InterruptedException {
        System.out.println("🚀 Starting ML Owner Login Test...");

        driver.get("https://mmpro.aasait.lk/");
        System.out.println("🌐 Opened URL: " + driver.getCurrentUrl());
        Thread.sleep(1000);

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginButton.click();
        System.out.println("🔐 Clicked Login button");
        Thread.sleep(1000);

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        usernameField.sendKeys("pasindu");

        WebElement passwordField = driver.findElement(By.id("sign-in_password"));
        passwordField.sendKeys("12345678");

        WebElement signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton.click();
        System.out.println("✅ Submitted login form");
        Thread.sleep(1500);

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("🏠 Reached ML Owner Home Page");
        Thread.sleep(1000);
    }

    @Test(priority = 2)
    public void testViewAllLicensesCard() throws InterruptedException {
        testCardNavigation("View All Licenses", "/mlowner/home/viewlicenses", "📄");
    }

    @Test(priority = 3)
    public void testRequestMiningLicenseCard() throws InterruptedException {
        testCardNavigation("Request a Mining License", "/mlowner/home/mlrequest", "📝");
    }

    @Test(priority = 4)
    public void testViewRequestedLicensesModal() throws InterruptedException {
        System.out.println("🔍 Testing Modal Card: View Requested Licenses");

        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(text(), 'View Requested Licenses')]/ancestor::div[contains(@class, 'custom-card')]")));

        WebElement clickMeButton = card.findElement(By.xpath(".//button[contains(@class, 'ml-card-button')]"));
        clickMeButton.click();
        System.out.println("🖱️ Clicked button: View Requested Licenses");
        Thread.sleep(1000);

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("ant-modal-content")));
        System.out.println("💬 Modal appeared");

        WebElement closeButton = modal.findElement(By.cssSelector("button[aria-label='Close']"));
        closeButton.click();
        System.out.println("❎ Closed the modal");
        Thread.sleep(1000);
    }

    private void testCardNavigation(String buttonText, String expectedUrlPart, String emoji) throws InterruptedException {
        System.out.println("=== " + emoji + " Testing Card: " + buttonText + " ===");

        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(text(), '" + buttonText + "')]/ancestor::div[contains(@class, 'custom-card')]")));

        WebElement clickMeButton = card.findElement(By.xpath(".//button[contains(@class, 'ml-card-button')]"));
        clickMeButton.click();
        System.out.println("🖱️ Clicked: " + buttonText);
        Thread.sleep(1000);

        wait.until(ExpectedConditions.urlContains(expectedUrlPart));
        System.out.println("✅ Navigated to: " + expectedUrlPart);
        Thread.sleep(1000);

        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("↩️ Returned to Home Page");
        Thread.sleep(1000);
    }

    @AfterClass
    public void tearDown() {
        System.out.println("🧹 Closing the browser...");
        driver.quit();
    }
}