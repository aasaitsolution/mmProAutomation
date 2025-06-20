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

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        WebElement password = driver.findElement(By.id("sign-in_password"));

        username.sendKeys("pasindu");
        password.sendKeys("12345678");

        WebElement signIn = driver.findElement(By.cssSelector("button[type='submit']"));
        signIn.click();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Login successful.");

        // Navigate to dispatch history after login
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
    }

    @Test(priority = 2)
    public void testPageTitle() {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".history-title")));
        Assert.assertTrue(title.getText().toLowerCase().contains("dispatch"));
    }

    @Test(priority = 3)
    public void testDatePickerInteraction() throws InterruptedException {
        WebElement datePicker = wait.until(ExpectedConditions.elementToBeClickable(By.className("history-datepicker")));
        datePicker.click();

        // FIX: Add a small static wait to allow for the calendar panel's animation to complete.
        // This makes the element finding much more reliable.
        Thread.sleep(500);

        String dayToSelect = "19";
        WebElement dateCell = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-picker-cell-in-view') and .//div[text()='" + dayToSelect + "']]")
        ));
        dateCell.click();
        System.out.println("✅ Date " + dayToSelect + " selected successfully.");
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
        Thread.sleep(500); // Small pause for scroll
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", printButton);

        // --- Part 2: Verify navigation to receipt and click Back to Home ---
        wait.until(ExpectedConditions.urlContains("/mlowner/home/dispatchload/TPLreceipt"));
        Assert.assertTrue(driver.getCurrentUrl().contains("TPLreceipt"), "Did not navigate to the TPL receipt page.");
        System.out.println("✅ Navigated to TPL receipt page.");

        // Now, on the receipt page, find and click the "Back to Home" button
        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Back to Home') or contains(.,'ආපසු') or contains(.,'முகப்புக்குத் திரும்பு')]")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", backButton);
        Thread.sleep(500);
        backButton.click();

        // --- Part 3: Verify navigation back to the main dashboard ---
        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        WebElement dashboard = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'Dashboard') or contains(text(),'උපකරණ පුවරුව') or contains(text(),'டாஷ்போர்டு')]")
        ));
        Assert.assertTrue(dashboard.isDisplayed(), "Dashboard not visible after clicking Back to Home.");
        System.out.println("✅ Back to Home button works correctly and returned to the dashboard.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}