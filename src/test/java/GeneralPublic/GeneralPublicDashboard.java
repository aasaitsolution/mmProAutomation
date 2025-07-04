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
            // 1. **FIXED**: Wait for the MODAL element to become VISIBLE, not just present.
            //    We target the main modal container.
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.modal"))); // Wait for the main modal div to be visible

            // 2. **FIXED**: Find the correct element (the input field) and get its 'value' attribute.
            //    We wait for the input to be visible inside the modal.
            WebElement messageInput = wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(
                    modal, By.cssSelector("input.invalid-message"))).get(0);

            String modalValue = messageInput.getAttribute("value").trim();
            System.out.println("📩 Modal appeared with value: " + modalValue);

            // 3. **IMPROVED**: Use a more specific assertion to check the actual text.
            //    This makes your test more reliable.
            Assert.assertEquals(modalValue, "Invalid Load", "The modal did not show the expected 'Invalid Load' message.");

            // If the test passes the assertion, the vehicle number is correctly identified as invalid.
            System.out.println("✅ Correctly identified as 'Invalid Load'.");

            // Find and click the close button to dismiss the modal
            WebElement closeButton = driver.findElement(By.cssSelector(".modal-close-button"));
            closeButton.click();

            // Optional: Wait for the modal to disappear to ensure the close action worked
            wait.until(ExpectedConditions.invisibilityOf(modal));

            System.out.println("🧹 Modal closed successfully.");

        } catch (TimeoutException e) {
            System.out.println("❌ Modal did not appear in the expected time.");
            System.out.println("Page Snapshot: " + driver.getPageSource());
            // Add the exception to the fail message for better debugging
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
