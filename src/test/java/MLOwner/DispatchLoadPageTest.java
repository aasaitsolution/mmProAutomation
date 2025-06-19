
package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class DispatchLoadPageTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--start-maximized");
        // This argument can help prevent random crashes in some environments
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test(priority = 1)
    public void loginToMLDashboard() {
        driver.get("https://mmpro.aasait.lk/");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        WebElement username = driver.findElement(By.id("sign-in_username"));
        WebElement password = driver.findElement(By.id("sign-in_password"));

        username.sendKeys("pasindu");
        password.sendKeys("12345678");

        WebElement signIn = driver.findElement(By.cssSelector("button[type='submit']"));
        signIn.click();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Login successful.");
    }

    /**
     * Helper method to reliably set the value of a React input field.
     * Clicks the element to focus, then uses JavaScript to set the value and
     * dispatches 'input' and 'change' events to trigger React's state updates.
     */
    private void setReactInput(WebElement element, String value) {
        try {
            // Click the element first to ensure it has focus
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();

            JavascriptExecutor js = (JavascriptExecutor) driver;
            // Set the value and dispatch events
            js.executeScript(
                    "arguments[0].value = arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    element, value
            );

            // Wait for the value to be correctly set in the DOM
            wait.until(ExpectedConditions.attributeToBe(element, "value", value));
        } catch (Exception e) {
            System.err.println("Failed to set input for element: " + element);
            e.printStackTrace();
            // Fail the test immediately if we can't set an input
            Assert.fail("Failed to set input value for element. See console for details.", e);
        }
    }

    @Test(priority = 2, dependsOnMethods = "loginToMLDashboard")
    public void testDispatchLoadFormSubmission() {
        driver.get("https://mmpro.aasait.lk/mlowner/home/dispatchload/LLL/100/402");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(text(),'Dispatch Your Load Here')]")));
        System.out.println("Dispatch Load page loaded.");

        // ===== Destination =====
        // CORRECTED: Treat AutoComplete as a simple text input since suggestion fetching is disabled.
        WebElement destinationInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'DESTINATION')]/following::input[1]")));
        setReactInput(destinationInput, "Matara");
        System.out.println("Set Destination to 'Matara'.");

        // ===== Lorry Number =====
        WebElement lorryInput = driver.findElement(By.xpath("//span[contains(text(),'LORRY NUMBER')]/following::input[1]"));
        setReactInput(lorryInput, "CBA-4321");
        System.out.println("Set Lorry Number to 'CBA-4321'.");

        // ===== Driver Contact =====
        WebElement driverContactInput = driver.findElement(By.xpath("//span[contains(text(),'DRIVER CONTACT')]/following::input[1]"));
        setReactInput(driverContactInput, "0771234567");
        System.out.println("Set Driver Contact to '0771234567'.");

        // ===== Route 1 (Pre-filled, verify or set if needed) =====
        // Note: The React code suggests Route 1 is pre-filled with 'divisionalSecretary'.
        // We will assume it's correct and proceed to set Routes 2 and 3.
        WebElement route1 = driver.findElement(By.xpath("//span[contains(text(),'ROUTE 1')]/following::input[1]"));
        System.out.println("Verified Route 1 is present with value: " + route1.getAttribute("value"));

        // ===== Route 2 =====
        WebElement route2 = driver.findElement(By.xpath("//span[contains(text(),'ROUTE 2')]/following::input[1]"));
        setReactInput(route2, "Galle");
        System.out.println("Set Route 2 to 'Galle'.");

        // ===== Route 3 =====
        WebElement route3 = driver.findElement(By.xpath("//span[contains(text(),'ROUTE 3')]/following::input[1]"));
        setReactInput(route3, "Colombo");
        System.out.println("Set Route 3 to 'Colombo'.");

        // ===== Cubes =====
        WebElement cubeInput = driver.findElement(By.xpath("//span[contains(text(),'CUBES')]/following::input[1]"));
        setReactInput(cubeInput, "2");
        System.out.println("Set Cubes to '2'.");

        // ===== Submit Button =====
        WebElement submitBtn = driver.findElement(By.xpath("//button[span[contains(text(),'Submit') or contains(text(),'සටහන් කරන්න') or contains(text(),'சமர்ப்பிக்கவும்')]]"));
        // Use Javascript click to avoid potential interception issues
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", submitBtn);
        System.out.println("Clicked Submit button.");

        // ===== Verify Success Modal =====
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-modal-content")));
        System.out.println("Success modal is visible.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal-body')]//div[contains(text(),'success') or contains(text(),'සාර්ථක') or contains(text(),'வெற்றிகரமாக')]")));
        System.out.println("Success message verified.");

        // ===== Go Back to Home =====
        WebElement backBtn = driver.findElement(By.xpath("//button[span[contains(text(),'Back') or contains(text(),'ආපසු') or contains(text(),'பின்செல்')]]"));
        backBtn.click();
        System.out.println("Clicked 'Back to Home' button.");

        // Final verification
        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Test successful. Returned to home page.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }
}