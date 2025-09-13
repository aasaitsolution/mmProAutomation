package GeneralPublic;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.annotations.*;
import java.time.Duration;

public class InvalidNumber extends BaseTest {

    private static final String BASE_URL = "https://mmpro.aasait.lk";
    private static final String INVALID_LICENSE = "LA4550";
    private static final String PHONE_NUMBER = "0769025444";
    private static final String OTP_CODE = "123456";

    @BeforeClass
    public void setupClass() {
        // Initialize the base test setup once for the entire class
        super.setup();
    }

    @BeforeMethod
    public void navigateToApplication() {
        // Navigate to base URL before each test method
        driver.get(BASE_URL);
    }

    @Test(priority = 1)
    public void navigateToPublicPage() {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/h1/button")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginButton);
        wait.until(ExpectedConditions.urlContains("/public"));
        System.out.println("✅ Navigated to public dashboard");
    }

    @Test(priority = 2, dependsOnMethods = "navigateToPublicPage")
    public void enterInvalidLicenseAndCheck() throws InterruptedException {
        // First navigate to public page since this depends on the previous test
        navigateToPublicPage();

        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/main/div/input")));
        input.clear();
        input.sendKeys(INVALID_LICENSE);
        Assert.assertEquals(input.getAttribute("value"), INVALID_LICENSE);

        WebElement checkBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/main/div[2]/button")));
        Thread.sleep(1000);
        checkBtn.click();
        System.out.println("🚗 Submitted invalid license: " + INVALID_LICENSE);
    }

    @Test(priority = 3, dependsOnMethods = "enterInvalidLicenseAndCheck")
    public void handleInvalidAlertOrMessage() {
        // First navigate to public page and enter invalid license
        navigateToPublicPage();

        try {
            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/main/div/input")));
            input.clear();
            input.sendKeys(INVALID_LICENSE);

            WebElement checkBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.check-button")));

            // Try multiple click methods for better reliability
            try {
                checkBtn.click();
            } catch (Exception clickException) {
                // If regular click fails, try JavaScript click
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkBtn);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not enter license and click check button");
        }

        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.alertIsPresent());
            String alertText = driver.switchTo().alert().getText();
            driver.switchTo().alert().accept();
            System.out.println("⚠️ Alert handled: " + alertText);
        } catch (TimeoutException te) {
            try {
                // Fix: Find the input with class 'invalid-message' inside the modal
                WebElement errorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.gp-modal-body input.invalid-message")
                ));
                String message = errorInput.getAttribute("value");
                System.out.println("❌ UI Message: " + message);
            } catch (Exception e) {
                System.out.println("🟡 No alert or visible error message");
            }
        }
    }

    // @Test(priority = 4, dependsOnMethods = "handleInvalidAlertOrMessage")
    // public void clickReportAndVerifyPhone() {
    //     // First navigate to public page and enter invalid license
    //     navigateToPublicPage();
    //
    //     try {
    //         WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
    //                 By.xpath("//*[@id=\"root\"]/div/main/div/main/div/input")));
    //         input.clear();
    //         input.sendKeys(INVALID_LICENSE);

    //         WebElement checkBtn = wait.until(ExpectedConditions.elementToBeClickable(
    //                 By.cssSelector("button.check-button")));
    //         checkBtn.click();
    //     } catch (Exception e) {
    //         System.out.println("⚠️ Could not enter license and click check button");
    //     }
    //
    //     try {
    //         WebElement reportBtn = wait.until(ExpectedConditions.elementToBeClickable(
    //                 By.xpath("//button[contains(text(), 'Report') or contains(@class, 'report')]")));
    //         reportBtn.click();
    //         System.out.println("📝 Report button clicked");

    //         WebElement phoneInput = wait.until(ExpectedConditions.elementToBeClickable(
    //                 By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div/div[2]/div/div/input")));
    //         phoneInput.sendKeys(PHONE_NUMBER);

    //         WebElement verifyBtn = wait.until(ExpectedConditions.elementToBeClickable(
    //                 By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div/div[2]/div/div/button")));
    //         verifyBtn.click();
    //         System.out.println("📱 Phone number submitted: " + PHONE_NUMBER);

    //         WebElement otpInput = wait.until(ExpectedConditions.elementToBeClickable(
    //                 By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div/div[2]/div/div/div[1]/input")));
    //         otpInput.sendKeys(OTP_CODE);

    //         WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
    //                 By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div/div[2]/div/div/div[1]/button")));
    //         submitBtn.click();
    //         System.out.println("🔐 OTP submitted: " + OTP_CODE);

    //     } catch (Exception e) {
    //         System.out.println("❌ Phone verification flow failed");
    //         Assert.fail("Phone verification step failed");
    //     }
    // }

    @AfterClass
    public void cleanupClass() {
        // Use BaseTest's teardown method instead of duplicating logic
        super.tearDown();
        System.out.println("🧹 Test class cleanup completed");
    }
}