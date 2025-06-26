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
    public void loginToDashboard() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
        loginBtn.click();
        Thread.sleep(500); // wait after login click
        waitForPageLoadComplete();
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        WebElement password = driver.findElement(By.id("sign-in_password"));

        username.sendKeys("pasindu");
        password.sendKeys("12345678");

        WebElement signIn = driver.findElement(By.cssSelector("button[type='submit']"));
        signIn.click();
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("✅ Login successful.");
    }

    @Test(priority = 2)
    public void navigateToDispatchHistory() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
        waitForPageLoadComplete();
        System.out.println("✅ Navigated to Dispatch History page.");
    }

    @Test(priority = 3)
    public void verifyDispatchHistoryTitleVisible() throws InterruptedException {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".history-title")));
        Assert.assertTrue(title.getText().toLowerCase().contains("dispatch"));
        System.out.println("✅ Page title contains 'dispatch'.");
    }

    @Test(priority = 4)
    public void openDatePickerAndSelectDate() throws InterruptedException {
        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(By.className("history-datepicker")));
        dateInput.click();

        WebElement calendarPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-picker-dropdown")));
        WebElement dateCell = calendarPopup.findElement(By.xpath("//td[@title='2025-06-19']"));
        waitUntilClickable(dateCell);
        dateCell.click();

        Thread.sleep(2000);
        System.out.println("✅ Date selected from calendar.");
    }

    @Test(priority = 5)
    public void verifyHistoryCardPresent() throws InterruptedException {
        List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("history-card")));
        Assert.assertFalse(cards.isEmpty(), "❌ No dispatch history cards found.");
        System.out.println("✅ Dispatch history cards found.");
    }
    @Test(priority = 6)
    public void testPrintReceiptNavigation() throws InterruptedException {
        WebElement firstCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("history-card")));
        Assert.assertTrue(firstCard.getText().contains("License Number"));

        WebElement printButton = firstCard.findElement(By.tagName("button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", printButton);
        waitUntilClickable(printButton);
        Thread.sleep(1000);
        printButton.click();
        Thread.sleep(1000);
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home/dispatchload/TPLreceipt"));
        Assert.assertTrue(driver.getCurrentUrl().contains("TPLreceipt"), "❌ Not navigated to TPL receipt page.");
        System.out.println("✅ Navigated to TPL receipt page.");
    }

    @Test(priority = 7)
    public void testBackToHomeNavigation() throws InterruptedException {
        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(.,'Back to Home') or contains(.,'ආපසු') or contains(.,'முகப்புக்குத் திரும்பு')]")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", backButton);
        waitUntilClickable(backButton);
        Thread.sleep(500);
        backButton.click();
        Thread.sleep(1000);
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("✅ Returned to dashboard after clicking Back to Home.");
    }



    @Test(priority = 8)
    public void testHistoryCardsAbsentCase() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=ZZZ/999/999");
        Thread.sleep(3000);
        waitForPageLoadComplete();

        List<WebElement> cards = driver.findElements(By.className("history-card"));

        if (cards.isEmpty()) {
            System.out.println("🛑 No dispatch history cards present as expected for invalid license.");

            // Try clicking "Back to Home" button if visible
            try {
                WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.history-back-button span")));

                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", backButton);
                backButton.click();
                waitForPageLoadComplete();

                wait.until(ExpectedConditions.urlContains("/mlowner/home"));
                System.out.println("✅ Navigated back to home successfully from empty history.");
            } catch (TimeoutException e) {
                System.out.println("❌ 'Back to Home' button not found or not clickable.");
            }
        } else {
            System.out.println("⚠️ Unexpected cards found for invalid license.");
        }
    }
    
    
    @Test(priority = 9)
    public void verifyDashboardCardVisible() {
        WebElement cardTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//span[contains(@class, 'card-title-text') and (contains(text(), 'View All Licenses') or " +
                    "contains(text(), 'Request a Mining License') or contains(text(), 'View Requested Licenses'))]")));

        Assert.assertTrue(cardTitle.isDisplayed(), "❌ Dashboard card title not visible after back navigation.");
        System.out.println("✅ Dashboard loaded successfully.");
    }

    @Test(priority = 10)
    public void testInvalidHistoryNavigation() {
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=INVALID/404");
        waitForPageLoadComplete();

        List<WebElement> errorElements = driver.findElements(By.xpath("//*[contains(text(),'No records') or contains(text(),'No TPL history found for this license')]"));
        if (!errorElements.isEmpty()) {
            System.out.println("❌ Error message displayed for invalid license number as expected.");
        } else {
            System.out.println("⚠️ No error message visible for invalid license.");
        }
    }

    private void waitForPageLoadComplete() {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(
            webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

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