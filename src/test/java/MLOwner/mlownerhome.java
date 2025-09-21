package MLOwner;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class mlownerhome extends BaseTest {

    /**
     * Login helper to reuse across tests
     */
    private void performLogin() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/");
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginButton.click();
        waitForPageLoadComplete();

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        WebElement passwordField = driver.findElement(By.id("sign-in_password"));

        usernameField.sendKeys("pasindu");
        passwordField.sendKeys("12345678");

        WebElement signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton.click();
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("✅ Login successful.");
    }

    @Test(priority = 1)
    public void testLogin() throws InterruptedException {
        System.out.println("🚀 Starting ML Owner Login Test...");
        performLogin();
        Assert.assertTrue(driver.getCurrentUrl().contains("/mlowner/home"), "❌ Login failed!");
        System.out.println("🏠 Reached ML Owner Home Page");
    }

    @Test(priority = 2)
    public void testViewAllLicensesCard() throws InterruptedException {
        performLogin();
        testCardNavigation("View All Licenses", "/mlowner/home/viewlicenses", "📄");
    }

    @Test(priority = 3)
    public void testRequestMiningLicenseCard() throws InterruptedException {
        performLogin();
        testCardNavigation("Request a Mining License", "/mlowner/home/mlrequest", "📝");
    }

    @Test(priority = 4)
    public void testViewRequestedLicensesModal() throws InterruptedException {
        performLogin();
        System.out.println("🔍 Testing Modal Card: View Requested Licenses");

        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(text(), 'View Requested Licenses')]/ancestor::div[contains(@class, 'custom-card')]")));

        WebElement clickMeButton = card.findElement(By.xpath(".//button[contains(@class, 'ml-card-button')]"));
        clickMeButton.click();
        System.out.println("🖱️ Clicked button: View Requested Licenses");

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("ant-modal-content")));
        System.out.println("💬 Modal appeared");

        WebElement closeButton = modal.findElement(By.cssSelector("button[aria-label='Close']"));
        closeButton.click();
        System.out.println("❎ Closed the modal");
    }

    /**
     * Reusable helper for card navigation
     */
    private void testCardNavigation(String buttonText, String expectedUrlPart, String emoji) throws InterruptedException {
        System.out.println("=== " + emoji + " Testing Card: " + buttonText + " ===");

        WebElement card = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(text(), '" + buttonText + "')]/ancestor::div[contains(@class, 'custom-card')]")));

        WebElement clickMeButton = card.findElement(By.xpath(".//button[contains(@class, 'ml-card-button')]"));
        clickMeButton.click();
        System.out.println("🖱️ Clicked: " + buttonText);

        wait.until(ExpectedConditions.urlContains(expectedUrlPart));
        System.out.println("✅ Navigated to: " + expectedUrlPart);

        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("↩️ Returned to Home Page");
    }

    /**
     * Page load helper
     */
    private void waitForPageLoadComplete() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(30))
                .until(webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }
}
