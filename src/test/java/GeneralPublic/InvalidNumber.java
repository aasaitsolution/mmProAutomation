package GeneralPublic;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class InvalidNumber extends BaseTest {

    private static final String BASE_URL = "https://mmpro.aasait.lk";
    private static final String INVALID_LICENSE = "LA4550";
    private static final String PHONE_NUMBER = "0769025444";
    private static final String OTP_CODE = "123456";

    /**
     * Helper method to navigate to public page
     */
    private void navigateToPublicPage() throws InterruptedException {
        driver.get(BASE_URL);
        waitForPageLoadComplete();

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/h1/button")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", loginButton);

        wait.until(ExpectedConditions.urlContains("/public"));
        waitForPageLoadComplete();
        System.out.println("Navigated to public dashboard");
    }

    private void waitForPageLoadComplete() {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    @Test(priority = 1)
    public void navigateToPublicPageTest() throws InterruptedException {
        navigateToPublicPage();
    }

    @Test(priority = 2)
    public void enterInvalidLicenseAndCheck() throws InterruptedException {
        navigateToPublicPage();

        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/main/div/input")));
        input.clear();
        input.sendKeys(INVALID_LICENSE);
        Assert.assertEquals(input.getAttribute("value"), INVALID_LICENSE);

        WebElement checkBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.check-button")));

        // Scroll to button and use JavaScript click to avoid interception
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", checkBtn);
        Thread.sleep(500); // Wait for scroll to complete
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkBtn);

        waitForPageLoadComplete();
        System.out.println("Submitted invalid license: " + INVALID_LICENSE);
    }

    @Test(priority = 3)
    public void handleInvalidAlertOrMessage() throws InterruptedException {
        navigateToPublicPage();

        // First enter invalid license
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/main/div/input")));
        input.clear();
        input.sendKeys(INVALID_LICENSE);

        WebElement checkBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.check-button")));

        // Use JavaScript click to avoid interception
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", checkBtn);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkBtn);

        waitForPageLoadComplete();

        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.alertIsPresent());
            String alertText = driver.switchTo().alert().getText();
            driver.switchTo().alert().accept();
            System.out.println("Alert handled: " + alertText);
        } catch (TimeoutException te) {
            try {
                // Find the input with class 'invalid-message' inside the modal
                WebElement errorInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.gp-modal-body input.invalid-message")
                ));
                String message = errorInput.getAttribute("value");
                System.out.println("UI Message: " + message);
            } catch (Exception e) {
                System.out.println("No alert or visible error message");
            }
        }
    }

}