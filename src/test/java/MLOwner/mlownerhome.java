package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
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

    @Test
    public void testMLOwnerHomePageButtons() {
        try {
            // 1. Login
            driver.get("https://mmpro.aasait.lk/");
            System.out.println("Current URL: " + driver.getCurrentUrl());

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/signin'] button")));
            loginButton.click();
            System.out.println("Clicked login button");

            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            usernameField.sendKeys("pasindu");

            WebElement passwordField = driver.findElement(By.id("sign-in_password"));
            passwordField.sendKeys("12345678");

            WebElement signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
            signInButton.click();
            System.out.println("Submitted login form");

            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            System.out.println("Reached ML Owner Home page");

            // 2. Test all three cards
            testCardNavigation("View All Licenses", "/mlowner/home/viewlicenses");
            testCardNavigation("Request a Mining License", "/mlowner/home/mlrequest");
            testModalCardNavigation("View Requested Licenses");

            System.out.println("✅ All tests passed!");

        } catch (Exception e) {
            System.out.println("❌ Test Failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private void testCardNavigation(String buttonText, String expectedUrlPart) {
        System.out.println("=== Testing Card: " + buttonText + " ===");

        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(text(), '" + buttonText + "')]/ancestor::div[contains(@class, 'custom-card')]")));

        WebElement clickMeButton = card.findElement(
                By.xpath(".//button[contains(@class, 'ml-card-button')]"));
        clickMeButton.click();
        System.out.println("Clicked button: " + buttonText);

        wait.until(ExpectedConditions.urlContains(expectedUrlPart));
        System.out.println("Navigated to: " + expectedUrlPart);

        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Returned to home page");
    }

    private void testModalCardNavigation(String buttonText) {
        System.out.println("=== Testing Modal Card: " + buttonText + " ===");

        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(text(), '" + buttonText + "')]/ancestor::div[contains(@class, 'custom-card')]")));

        WebElement clickMeButton = card.findElement(
                By.xpath(".//button[contains(@class, 'ml-card-button')]"));
        clickMeButton.click();
        System.out.println("Clicked button: " + buttonText);

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("ant-modal-content")));
        System.out.println("Modal appeared for: " + buttonText);

        WebElement closeButton = modal.findElement(
                By.xpath(".//button[contains(text(), 'Close') or contains(text(), 'වසන්න') or contains(text(), 'மூடு')]"));
        closeButton.click();
        System.out.println("Closed the modal for: " + buttonText);
    }
}
