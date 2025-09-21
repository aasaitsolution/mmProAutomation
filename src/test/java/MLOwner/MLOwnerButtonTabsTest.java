package MLOwner;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;

import java.time.Duration;

public class MLOwnerButtonTabsTest extends BaseTest {

    private void performLogin() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/");
        waitForPageLoadComplete();

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginButton.click();
        waitForPageLoadComplete();

        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("sign-in_username")));
        usernameField.sendKeys("pasindu");

        WebElement passwordField = driver.findElement(By.id("sign-in_password"));
        passwordField.sendKeys("12345678");

        WebElement signInButton = driver.findElement(By.cssSelector("button[type='submit']"));
        signInButton.click();
        waitForPageLoadComplete();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Successfully logged in to ML Owner dashboard");
    }

    private void waitForPageLoadComplete() {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    @Test(priority = 1)
    public void testLogin() throws InterruptedException {
        performLogin();
    }

    @Test(priority = 2)
    public void navigateToLicensesPage() throws InterruptedException {
        performLogin();

        try {
            WebElement viewLicensesLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='/mlowner/home/viewlicenses']")));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    viewLicensesLink);
            Thread.sleep(500); // Allow scroll animation

            try {
                viewLicensesLink.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewLicensesLink);
            }

            wait.until(ExpectedConditions.urlContains("/mlowner/home/viewlicenses"));
            waitForPageLoadComplete();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("container")));
            System.out.println("Navigated to Licenses page");

            Thread.sleep(2000);

            // Navigate back to dashboard
            driver.navigate().back();
            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            waitForPageLoadComplete();
            System.out.println("Returned to ML Owner dashboard");

        } catch (Exception e) {
            System.err.println("Navigation to Licenses page failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test(priority = 3)
    public void testRequestedLicensesButton() throws InterruptedException {
        performLogin();

        try {
            System.out.println("=== Testing Requested Licenses Button ===");
            // Wait for the dashboard to be ready before interacting
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/mlowner/home/viewlicenses']")));

            WebElement requestedLicensesButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(), 'Requested Licenses')]/ancestor::div[contains(@class, 'custom-card')]//button[contains(@class, 'ml-card-button')]")));

            // Use JavaScript click to avoid interception
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", requestedLicensesButton);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", requestedLicensesButton);

            System.out.println("Clicked Requested Licenses button");

            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.className("ant-modal-content")));
            System.out.println("Requested Licenses modal is visible");

            WebElement modalTitle = modal.findElement(By.className("ant-modal-title"));
            System.out.println("Modal title: " + modalTitle.getText());

            // Use a more specific XPath for the close button inside the modal footer
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@class='ant-modal-footer']//button[contains(.,'Close')]")));

            // Use JavaScript click for close button as well
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
            System.out.println("Closed the modal");

            wait.until(ExpectedConditions.invisibilityOf(modal));
            System.out.println("Modal is no longer visible");

        } catch (Exception e) {
            System.err.println("Requested Licenses test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4)
    public void testMLRequestButton() throws InterruptedException {
        performLogin();

        try {
            // Wait for the ML Request button to be clickable
            WebElement mlRequestButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='/mlowner/home/mlrequest']")));

            // Scroll into view and use JavaScript click
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", mlRequestButton);
            Thread.sleep(500);

            try {
                mlRequestButton.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", mlRequestButton);
            }

            // Wait for URL to change
            wait.until(ExpectedConditions.urlContains("/mlowner/home/mlrequest"));
            waitForPageLoadComplete();

            System.out.println("Successfully navigated to ML Request page");

            Thread.sleep(1000); // Brief pause

        } catch (Exception e) {
            System.err.println("Navigation to ML Request page failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}