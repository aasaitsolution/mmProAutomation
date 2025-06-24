package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class DispatchHistoryTest {

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
    public void loginToMLDashboard() {
        driver.get("https://mmpro.aasait.lk/");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginBtn.click();
        waitForPageLoadComplete();

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        WebElement password = driver.findElement(By.id("sign-in_password"));

        username.sendKeys("pasindu");
        password.sendKeys("12345678");

        WebElement signIn = driver.findElement(By.cssSelector("button[type='submit']"));
        signIn.click();
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Login successful.");

        // Navigate to dispatch history after login
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
        waitForPageLoadComplete();
    }

    @Test(priority = 2)
    public void testPageTitle() {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".history-title")));
        Assert.assertTrue(title.getText().toLowerCase().contains("dispatch"));
    }

    @Test(priority = 3)
    public void testDatePickerInteraction() throws InterruptedException {
    WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(By.className("history-datepicker")));

    // Click input to open date picker popup
    dateInput.click();

    // Wait for calendar popup to appear - adjust locator based on your actual calendar popup DOM
    // Example: if calendar days have a class like "ant-picker-cell"
    WebElement calendarPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-picker-dropdown")));

    // Now, find the date cell for 19th June 2025 (adjust your selector based on your calendar's markup)
    // Here is an example for Ant Design date picker cells, you might need to adjust it.
    WebElement dateCell = calendarPopup.findElement(By.xpath("//td[@title='2025-06-19']"));
    waitUntilClickable(dateCell);
    dateCell.click();

    // Wait a bit to see the effect
    Thread.sleep(1000);

    System.out.println("✅ Date selected visually by clicking calendar.");
    }

    @Test(priority = 4)
    public void testHistoryCardPrintAndBackToHome() throws InterruptedException {
        // --- Part 1: Find card and click Print ---
        List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("history-card")));
        Assert.assertFalse(cards.isEmpty(), "No dispatch history cards found.");

        WebElement firstCard = cards.get(0);
        Assert.assertTrue(firstCard.getText().contains("License Number"));

        WebElement printButton = firstCard.findElement(By.tagName("button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", printButton);
        waitUntilClickable(printButton);
        Thread.sleep(500); // Small pause for scroll
        printButton.click();
        waitForPageLoadComplete();
        
        // --- Part 2: Verify navigation to receipt and click Back to Home ---
        wait.until(ExpectedConditions.urlContains("/mlowner/home/dispatchload/TPLreceipt"));
        Assert.assertTrue(driver.getCurrentUrl().contains("TPLreceipt"), "Did not navigate to the TPL receipt page.");
        System.out.println("✅ Navigated to TPL receipt page.");

        // Now, on the receipt page, find and click the "Back to Home" button
        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Back to Home') or contains(.,'ආපසු') or contains(.,'முகப்புக்குத் திரும்பு')]")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", backButton);
        waitUntilClickable(backButton);
        Thread.sleep(500);
        backButton.click();
        waitForPageLoadComplete();

        // --- Part 3: Verify navigation back to the main dashboard ---
        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Current URL after clicking Back to Home: " + driver.getCurrentUrl());
        System.out.println("Page title after clicking Back to Home: " + driver.getTitle());

        WebElement cardTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[contains(@class, 'card-title-text') and " +
                    "(contains(text(), 'View All Licenses') or " +
                    "contains(text(), 'Request a Mining License') or " +
                    "contains(text(), 'View Requested Licenses'))]")
        ));

        Assert.assertTrue(cardTitle.isDisplayed(), "Expected dashboard card title not visible after clicking Back to Home.");
        System.out.println("✅ Back to Home button works correctly and dashboard cards are visible.");
    }

     // Utility method to set input value via JS and dispatch events
    private void setInputValueViaJS(WebElement inputElement, String value) {
        ((JavascriptExecutor) driver).executeScript(
            "const el = arguments[0];" +
            "const value = arguments[1];" +
            "el.value = value;" +  // <-- set value directly here
            "el.dispatchEvent(new Event('input', { bubbles: true }));" +
            "el.dispatchEvent(new Event('change', { bubbles: true }));",
            inputElement, value
        );

    }

    // Utility wait for document ready state = complete
    private void waitForPageLoadComplete() {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(
            webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    // Utility wait until element is clickable
    private void waitUntilClickable(WebElement element) {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.elementToBeClickable(element));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}