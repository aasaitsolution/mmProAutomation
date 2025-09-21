package MLOwner;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class DispatchHistoryTest extends BaseTest {

    private void performLogin() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
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
        System.out.println("✅ Login successful.");
    }

    @Test(priority = 1)
    public void loginToDashboard() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
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
        System.out.println("✅ Login successful.");
    }

    @Test(priority = 2)
    public void navigateToDispatchHistory() throws InterruptedException {
        performLogin();
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
        waitForPageLoadComplete();
        System.out.println("✅ Navigated to Dispatch History page.");
    }

    @Test(priority = 3)
    public void verifyDispatchHistoryTitleVisible() throws InterruptedException {
        performLogin();
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
        waitForPageLoadComplete();

        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.history-title")));
        Assert.assertTrue(title.getText().toLowerCase().contains("dispatch"));
        System.out.println("✅ Page title contains 'dispatch'.");
    }

    @Test(priority = 4)
    public void verifyHistoryCardPresent() throws InterruptedException {
        performLogin();
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
        waitForPageLoadComplete();

        List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("history-card")));
        Assert.assertFalse(cards.isEmpty(), "❌ No dispatch history cards found.");
        System.out.println("✅ Dispatch history cards found.");
    }

    /**
     * FIX APPLIED: Replaced standard click with a JavaScript click and improved scrolling
     * to prevent ElementClickInterceptedException from a sticky header or other overlays.
     */
    @Test(priority = 5)
    public void testPrintReceiptNavigation() throws InterruptedException {
        performLogin();
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
        waitForPageLoadComplete();

        WebElement firstCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("history-card")));
        Assert.assertTrue(firstCard.getText().contains("License Number"));

        WebElement printButton = firstCard.findElement(By.tagName("button"));
        // Scroll element to the center of the view to avoid sticky headers/footers
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", printButton);
        wait.until(ExpectedConditions.elementToBeClickable(printButton));
        Thread.sleep(500); // Small pause for stability before clicking

        // Use JavaScript to click, which is more robust against interception
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", printButton);
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home/dispatchload/TPLreceipt"));
        Assert.assertTrue(driver.getCurrentUrl().contains("TPLreceipt"), "❌ Not navigated to TPL receipt page.");
        System.out.println("✅ Navigated to TPL receipt page.");
    }

    @Test(priority = 6)
    public void testBackToHomeNavigation() throws InterruptedException {
        performLogin();
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
        waitForPageLoadComplete();

        // First navigate to receipt page
        WebElement firstCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("history-card")));
        WebElement printButton = firstCard.findElement(By.tagName("button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", printButton);
        wait.until(ExpectedConditions.elementToBeClickable(printButton));
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", printButton);
        waitForPageLoadComplete();

        // Now test back to home
        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Back to Home') or contains(.,'ආපසු') or contains(.,'முகப்புக்குத் திரும்பு')]")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", backButton);
        wait.until(ExpectedConditions.elementToBeClickable(backButton));
        backButton.click();
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("✅ Returned to dashboard after clicking Back to Home.");
    }

    @Test(priority = 7)
    public void testHistoryCardsAbsentCase() throws InterruptedException {
        performLogin();
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=ZZZ/999/999");
        waitForPageLoadComplete();

        List<WebElement> cards = driver.findElements(By.className("history-card"));

        if (cards.isEmpty()) {
            System.out.println("✅ No dispatch history cards present as expected for invalid license.");
        } else {
            // It's better to fail the test if the condition is not met
            Assert.fail("⚠️ Unexpected history cards found for an invalid license number.");
        }
    }

    @Test(priority = 8)
    public void verifyDashboardCardVisible() throws InterruptedException {
        performLogin();
        // Navigate back to the dashboard to verify the test flow can continue
        driver.get("https://mmpro.aasait.lk/mlowner/home");
        waitForPageLoadComplete();
        WebElement cardTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(@class, 'card-title-text') and (contains(text(), 'View All Licenses') or " +
                        "contains(text(), 'Request a Mining License') or contains(text(), 'View Requested Licenses'))]")));

        Assert.assertTrue(cardTitle.isDisplayed(), "❌ Dashboard card title not visible after back navigation.");
        System.out.println("✅ Dashboard loaded successfully.");
    }

    @Test(priority = 9)
    public void testInvalidHistoryNavigation() throws InterruptedException {
        performLogin();
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=INVALID/404");
        waitForPageLoadComplete();

        // Look for a message indicating no records were found
        By noRecordsMessageLocator = By.xpath("//*[contains(text(),'No records') or contains(text(),'No TPL history found for this license') or contains(@class, 'ant-empty-description')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(noRecordsMessageLocator));

        WebElement noRecordsMessage = driver.findElement(noRecordsMessageLocator);
        Assert.assertTrue(noRecordsMessage.isDisplayed(), "❌ Error/empty message was not displayed for an invalid license.");
        System.out.println("✅ 'No records' message displayed for invalid license number as expected.");
    }

    @Test(priority = 10)
    public void testCompleteDispatchHistoryFlow() throws InterruptedException {
        // Complete end-to-end test combining multiple steps
        performLogin();

        // Navigate to dispatch history
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
        waitForPageLoadComplete();
        System.out.println("✅ Navigated to Dispatch History page.");

        // Verify dispatch history title
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.history-title")));
        Assert.assertTrue(title.getText().toLowerCase().contains("dispatch"));
        System.out.println("✅ Page title contains 'dispatch'.");

        // Verify history cards are present
        List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("history-card")));
        Assert.assertFalse(cards.isEmpty(), "❌ No dispatch history cards found.");
        System.out.println("✅ Dispatch history cards found.");

        // Test print receipt navigation
        WebElement firstCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("history-card")));
        Assert.assertTrue(firstCard.getText().contains("License Number"));

        WebElement printButton = firstCard.findElement(By.tagName("button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", printButton);
        wait.until(ExpectedConditions.elementToBeClickable(printButton));
        Thread.sleep(500);

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", printButton);
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home/dispatchload/TPLreceipt"));
        Assert.assertTrue(driver.getCurrentUrl().contains("TPLreceipt"), "❌ Not navigated to TPL receipt page.");
        System.out.println("✅ Navigated to TPL receipt page.");

        // Test back to home navigation
        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Back to Home') or contains(.,'ආපසු') or contains(.,'முகப்புக்குத் திරும்பு')]")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", backButton);
        wait.until(ExpectedConditions.elementToBeClickable(backButton));
        backButton.click();
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("✅ Returned to dashboard after clicking Back to Home.");

        // Verify dashboard is loaded
        WebElement cardTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(@class, 'card-title-text') and (contains(text(), 'View All Licenses') or " +
                        "contains(text(), 'Request a Mining License') or contains(text(), 'View Requested Licenses'))]")));

        Assert.assertTrue(cardTitle.isDisplayed(), "❌ Dashboard card title not visible after back navigation.");
        System.out.println("✅ Dashboard loaded successfully - Complete flow test passed.");
    }

    private void waitForPageLoadComplete() {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }
}