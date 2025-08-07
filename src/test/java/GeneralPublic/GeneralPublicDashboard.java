//Done
package GeneralPublic;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.openqa.selenium.*;

import java.time.Duration;
import java.util.List;

public class GeneralPublicDashboard {
    private WebDriver driver;
    private WebDriverWait wait;

     @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);  // Only create driver once with options
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        System.out.println("✅ Browser launched");
    }

    @Test(priority = 1)
    public void openWebsite() {
        driver.get("https://mmpro.aasait.lk/");
        System.out.println("🌐 Opened public site");
        Assert.assertTrue(driver.getTitle() != null, "Page did not load properly");
    }

    @Test(priority = 2, dependsOnMethods = {"openWebsite"})
    public void clickCheckValidityButton() {
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[@id=\"root\"]/div/main/h1/button")));
        loginButton.click();
        System.out.println("🟢 Clicked 'Check Validity' button");
    }

    @Test(priority = 3, dependsOnMethods = {"clickCheckValidityButton"})
    public void enterAndSubmitVehicleNumber() {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("input[type='text']")));
        inputField.sendKeys("ABX1234");
        System.out.println("✍️ Entered vehicle number: ABX1234");

        WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.check-button")));
        checkButton.click();
        System.out.println("✅ Clicked check button");
    }

    @Test(priority = 4, dependsOnMethods = {"enterAndSubmitVehicleNumber"})
public void verifyModalResponse() {
    try {
        // 1. Wait for modal-content to appear
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("div.modal-content")));

        // 2. Wait for the input inside gp-modal-body
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(
            modal, By.cssSelector("input.valid-message"))).get(0);

        // 3. Extract the value
        String modalValue = inputField.getAttribute("value").trim();
        System.out.println("📩 Modal appeared with value: " + modalValue);

        // 4. Assert it's "Valid Load" now
        Assert.assertEquals(modalValue, "Valid Load", "The modal did not show the expected 'Valid Load' message.");

        // 5. Close modal
        WebElement closeButton = modal.findElement(By.cssSelector(".modal-close-button"));
        closeButton.click();
        wait.until(ExpectedConditions.invisibilityOf(modal));
        System.out.println("🧹 Modal closed successfully.");

    } catch (TimeoutException e) {
        System.out.println("❌ Modal did not appear in the expected time.");
        Assert.fail("Modal not found after checking license number.", e);
    }
}




    @Test(priority = 5, dependsOnMethods = {"verifyModalResponse"})
    public void enterInvalidVehicleNumberFormat() {
        try {
            WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='text']")));
            inputField.clear();
            inputField.sendKeys("XYZ!");  // Invalid format
            System.out.println("✍️ Entered invalid vehicle number: XYZ!");

            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.check-button")));
            checkButton.click();
            System.out.println("🚫 Clicked check button with invalid input");
            Thread.sleep(1000);

            // Wait for the modal to appear
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("gp-modal-body")));

            // Look for the .invalid-message input
            WebElement invalidInput = modal.findElement(By.cssSelector("input.invalid-message"));
            String message = invalidInput.getAttribute("value");

            System.out.println("📩 Modal appeared with value: " + message);

            if (message.toLowerCase().contains("invalid")) {
                System.out.println("✅ Invalid vehicle number correctly handled");
            } else {
                System.out.println("⚠️ Unexpected message in modal for invalid input");
            }

            Thread.sleep(1000);
            // Close the modal
            WebElement closeBtn = driver.findElement(By.cssSelector("button.modal-close-button"));
            closeBtn.click();
            System.out.println("🧹 Modal closed after invalid entry");

        } catch (TimeoutException e) {
            System.out.println("❌ Modal did not appear for invalid vehicle number format");
            Assert.fail("Modal not found for invalid vehicle number format");
        } catch (NoSuchElementException e) {
            System.out.println("❌ Invalid message input not found in modal");
            Assert.fail("Input with class 'invalid-message' not found");
        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
            Assert.fail("Unexpected exception during invalid input test");
        }
    }

 

    @AfterClass
    public void tearDown() {
        try {
            Thread.sleep(3000); // Let user see result
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (driver != null) {
            driver.quit();
            System.out.println("🛑 Browser closed");
        }
    }
}
