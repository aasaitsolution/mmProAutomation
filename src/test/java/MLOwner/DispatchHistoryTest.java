package MLOwner;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

public class DispatchHistoryTest extends BaseTest {

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
    public void navigateToDispatchHistory() {
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
        waitForPageLoadComplete();
        System.out.println("✅ Navigated to Dispatch History page.");
    }

    @Test(priority = 3)
    public void verifyDispatchHistoryTitleVisible() {
        WebElement historyTitle = driver.findElement(By.xpath("//h1[@class='history-title']"));
        Assert.assertTrue(historyTitle.getText().toLowerCase().contains("dispatch"));
        System.out.println("✅ Page title contains 'dispatch'.");
    }

    @Test(priority = 5)
    public void verifyHistoryCardPresent() {
        List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("history-card")));
        Assert.assertFalse(cards.isEmpty(), "❌ No dispatch history cards found.");
        System.out.println("✅ Dispatch history cards found.");
    }

    @Test(priority = 6)
    public void testPrintReceiptNavigation() throws InterruptedException {
        WebElement firstCard = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("history-card")));
        Assert.assertTrue(firstCard.getText().contains("License Number"));

        WebElement printButton = firstCard.findElement(By.tagName("button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", printButton);
        wait.until(ExpectedConditions.elementToBeClickable(printButton));
        Thread.sleep(500);

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", printButton);
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home/dispatchload/TPLreceipt"));
        Assert.assertTrue(driver.getCurrentUrl().contains("TPLreceipt"), "❌ Not navigated to TPL receipt page.");
        System.out.println("✅ Navigated to TPL receipt page.");
    }

    @Test(priority = 7)
    public void testBackToHomeNavigation() {
        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Back to Home') or contains(.,'ආපසු') or contains(.,'முகப்புக்குத் திரும்பு')]")));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", backButton);
        wait.until(ExpectedConditions.elementToBeClickable(backButton));
        backButton.click();
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("✅ Returned to dashboard after clicking Back to Home.");
    }

    @Test(priority = 8)
    public void testHistoryCardsAbsentCase() {
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=ZZZ/999/999");
        waitForPageLoadComplete();

        List<WebElement> cards = driver.findElements(By.className("history-card"));

        if (cards.isEmpty()) {
            System.out.println("✅ No dispatch history cards present as expected for invalid license.");
        } else {
            Assert.fail("⚠️ Unexpected history cards found for an invalid license number.");
        }
    }

    @Test(priority = 9)
    public void verifyDashboardCardVisible() {
        driver.get("https://mmpro.aasait.lk/mlowner/home");
        waitForPageLoadComplete();
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

        By noRecordsMessageLocator = By.xpath("//*[contains(text(),'No records') or contains(text(),'No TPL history found for this license') or contains(@class, 'ant-empty-description')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(noRecordsMessageLocator));

        WebElement noRecordsMessage = driver.findElement(noRecordsMessageLocator);
        Assert.assertTrue(noRecordsMessage.isDisplayed(), "❌ Error/empty message was not displayed for an invalid license.");
        System.out.println("✅ 'No records' message displayed for invalid license number as expected.");
    }

    private void waitForPageLoadComplete() {
        new WebDriverWait(driver, java.time.Duration.ofSeconds(30)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }
}
