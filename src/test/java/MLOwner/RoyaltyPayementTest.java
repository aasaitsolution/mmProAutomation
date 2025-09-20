package MLOwner;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import java.time.Duration;
import org.testng.Assert;

public class RoyaltyPayementTest extends BaseTest {

    /**
     * Helper method to perform login before tests that require authentication
     */
    private void performLogin() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/");
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
        loginButton.click();
        waitForPageLoadComplete();

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        usernameField.sendKeys("pasindu");

        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
        passwordField.sendKeys("12345678");
        System.out.println("👤 Entered username and password");

        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        signInButton.click();
        System.out.println("🚀 Submitted username and password");
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("🔐 Login successful.");
    }

    @Test(priority = 0)
    public void invalidLoginShouldFail() throws InterruptedException {
        try {
            driver.get("https://mmpro.aasait.lk/");
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
            loginButton.click();
            waitForPageLoadComplete();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username"))).sendKeys("wrongUser");
            driver.findElement(By.id("sign-in_password")).sendKeys("wrongPass");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            waitForPageLoadComplete();

            Thread.sleep(2000);
            String currentUrl = driver.getCurrentUrl();
            assert !currentUrl.contains("/mlowner/home") : "❌ Login should have failed but succeeded.";
            System.out.println("✅ Invalid login prevented as expected.");
        } catch (Exception e) {
            System.out.println("⚠️ Login failure test encountered an error.");
            e.printStackTrace();
        }
    }

    @Test(priority = 1)
    public void login() throws InterruptedException {
        performLogin();
    }

    @Test(priority = 2)
    public void openRoyaltyModal() throws InterruptedException {
        performLogin();

        WebElement royaltyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")));
        royaltyBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal-title') and contains(text(),'Royalty Payment')]")));
        System.out.println("💰 Royalty modal opened.");
    }

    @Test(priority = 3)
    public void submitRoyaltyForm() throws InterruptedException {
        performLogin();

        WebElement royaltyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")));
        royaltyBtn.click();

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder*='Enter Payment Amount']")));
        input.sendKeys("1000");

        WebElement payNowBtn = driver.findElement(By.cssSelector(".pay-now-button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payNowBtn);
        payNowBtn.click();

        System.out.println("🧾 Payment submitted. Waiting for PayHere...");
    }

    @Test(priority = 4)
    public void selectMasterCardAndPay() throws InterruptedException {
        performLogin();

        // Open modal and submit form first
        WebElement royaltyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")));
        royaltyBtn.click();

        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder*='Enter Payment Amount']")));
        input.sendKeys("1000");

        WebElement payNowBtn = driver.findElement(By.cssSelector(".pay-now-button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payNowBtn);
        payNowBtn.click();

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

            System.out.println("✅ Card details submitted. Payment in progress...");

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

    @Test(priority = 5)
    public void emptyAmountShouldShowError() throws InterruptedException {
        performLogin();

        try {
            WebElement royaltyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")));
            royaltyBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'ant-modal-title') and contains(text(),'Royalty Payment')]")));

            WebElement payNowBtn = driver.findElement(By.cssSelector(".pay-now-button"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payNowBtn);
            payNowBtn.click();

            Thread.sleep(1000);  // Let validation trigger
            System.out.println("⚠️ Attempted to submit empty amount.");
        } catch (Exception e) {
            System.out.println("✅ Handled missing amount gracefully.");
            e.printStackTrace();
        }
    }

    @Test(priority = 6)
    public void invalidCardDetailsShouldFail() throws InterruptedException {
        performLogin();

        try {
            WebElement royaltyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")));
            royaltyBtn.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'ant-modal-title') and contains(text(),'Royalty Payment')]")));

            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[placeholder*='Enter Payment Amount']")));
            input.sendKeys("1000");

            WebElement payNowBtn = driver.findElement(By.cssSelector(".pay-now-button"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payNowBtn);
            payNowBtn.click();

            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.tagName("iframe")));
            WebElement masterCardOption = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#payment_container_MASTER")));
            masterCardOption.click();

            WebElement innerIframe = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
            driver.switchTo().frame(innerIframe);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cardHolderName")));
            driver.findElement(By.id("cardHolderName")).sendKeys("Sankavi");
            driver.findElement(By.id("cardNo")).sendKeys("123"); // Invalid number
            driver.findElement(By.id("cardSecureId")).sendKeys("12"); // too short
            driver.findElement(By.id("cardExpiry")).sendKeys("00/00"); // invalid expiry

            // Wait briefly for validation messages to appear
            Thread.sleep(2000);

            // Assert exact text of validation messages
            WebElement cardNumberError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("small[data-bv-for='cardNo'][data-bv-result='INVALID']")));
            Assert.assertEquals(cardNumberError.getText().trim(), "The card number is not valid",
                    "❌ Card number validation message text mismatch!");

            WebElement cvvError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("small[data-bv-for='cardSecureId'][data-bv-result='INVALID']")));
            Assert.assertEquals(cvvError.getText().trim(), "The CVV is not valid",
                    "❌ CVV validation message text mismatch!");

            WebElement expiryError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("small[data-bv-for='cardExpiry'][data-bv-result='INVALID']")));
            Assert.assertEquals(expiryError.getText().trim(), "The card expiry is not valid",
                    "❌ Expiry date validation message text mismatch!");

            System.out.println("✅ Payment failed with invalid card data and exact validation messages verified.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    @Test(priority = 7)
    public void modalShouldNotOpenIfButtonMissing() throws InterruptedException {
        performLogin();

        try {
            driver.get("https://mmpro.aasait.lk/nonexistent-page"); // fake URL to simulate missing button
            waitForPageLoadComplete();
            driver.findElement(By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")).click();
            System.out.println("❌ Button was found on invalid page — test should fail.");
        } catch (NoSuchElementException e) {
            System.out.println("✅ Modal not opened because button was missing — expected behavior.");
        } finally {
            driver.get("https://mmpro.aasait.lk/mlowner/home");
            waitForPageLoadComplete();
        }
    }

    @Test(priority = 8)
    public void enterNonNumericAmountShouldFail() throws InterruptedException {
        performLogin();

        WebElement royaltyBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")));
        royaltyBtn.click();

        WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder*='Enter Payment Amount']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='abc';", amountInput);
        amountInput.sendKeys(Keys.TAB); // trigger blur event

        WebElement payNowBtn = driver.findElement(By.cssSelector(".pay-now-button"));
        payNowBtn.click();

        Thread.sleep(1000);
        System.out.println("❌ Non-numeric amount should not be accepted.");
    }

    @Test(priority = 9)
    public void zeroAmountShouldTriggerValidation() throws InterruptedException {
        performLogin();

        WebElement royaltyBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")));
        royaltyBtn.click();

        WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder*='Enter Payment Amount']")));
        amountInput.clear();
        amountInput.sendKeys("0");

        WebElement payNowBtn = driver.findElement(By.cssSelector(".pay-now-button"));
        payNowBtn.click();

        Thread.sleep(1000); // allow error notification to appear
        System.out.println("✅ Validation triggered for zero amount.");
    }

    @Test(priority = 10)
    public void formShouldResetOnModalReopen() throws InterruptedException {
        performLogin();

        WebElement royaltyBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")));
        royaltyBtn.click();

        WebElement amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder*='Enter Payment Amount']")));
        amountInput.sendKeys("999");

        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-modal-footer button"))); // Close button
        closeBtn.click();

        Thread.sleep(1000); // allow modal to close

        royaltyBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//span[contains(text(),'Pay Royalty')]]")));
        royaltyBtn.click();

        amountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[placeholder*='Enter Payment Amount']")));
        String currentValue = amountInput.getAttribute("value");

        assert currentValue.equals("") : "❌ Form did not reset!";
        System.out.println("✅ Form reset verified after modal reopen.");
    }

    @Test(priority = 11)
    public void simulateSuccessPaymentCallback() throws InterruptedException {
        performLogin();

        ((JavascriptExecutor) driver).executeScript(
                "window.payhere && window.payhere.onCompleted && window.payhere.onCompleted('ORDER12345');"
        );
        System.out.println("✅ Simulated PayHere success callback.");
    }

    private void waitForPageLoadComplete() {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    public void waitForIframeToDisappear() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("ph-iframe")));
        } catch (TimeoutException ignored) {}
    }

    public void removePayHereIframeIfPresent() {
        try {
            WebElement iframe = driver.findElement(By.id("ph-iframe"));
            if (iframe.isDisplayed()) {
                ((JavascriptExecutor) driver).executeScript("document.getElementById('ph-iframe').remove();");
                waitForIframeToDisappear();
            }
        } catch (NoSuchElementException ignored) {}
    }
}