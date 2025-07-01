//Done
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
        System.out.println("✅ Login successful.");
    }

    @Test(priority = 2, dependsOnMethods = "loginToMLDashboard")
    public void openDispatchLoadPage() throws InterruptedException {
        driver.get("https://mmpro.aasait.lk/mlowner/home/dispatchload/LLL/100/431/Eravur");
        Thread.sleep(3000);
        System.out.println("🌐 Navigated to Dispatch Load Page.");
    }

    @Test(priority = 3, dependsOnMethods = "openDispatchLoadPage")
    public void validateLicenseAndRoute1() {
        WebElement licenseInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'LICENSE NUMBER')]/following::input[1]")));
        String licenseVal = licenseInput.getAttribute("value");
        System.out.println("🔍 License Number input value: " + licenseVal);
        Assert.assertEquals(licenseVal, "LLL/100/431");

        
    }

    @Test(priority = 4, dependsOnMethods = "validateLicenseAndRoute1")
    public void fillDispatchFormFields() {
        // 1. Destination
        setReactInput(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'DESTINATION')]/following::input[1]"))), "Jaffna");

        // 2. Lorry Number
        setReactInput(driver.findElement(
                By.xpath("//span[contains(text(),'LORRY NUMBER')]/following::input[1]")), "CBA4321");

        // 3. Driver Contact
        setReactInput(driver.findElement(
                By.xpath("//span[contains(text(),'DRIVER CONTACT')]/following::input[1]")), "0771234567");

        // 4. Route 1
        WebElement route1Input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'ROUTE 1')]/following::input[1]")));
        setReactInput(route1Input, "Eravur");
        String route1Val = route1Input.getAttribute("value");
        System.out.println("🛣️ Route 1 input value: " + route1Val);
        Assert.assertEquals(route1Val, "Eravur");
        // 5. Route 2
        setReactInput(driver.findElement(
                By.xpath("//span[contains(text(),'ROUTE 2')]/following::input[1]")), "Batticaloa");

        // 6. Route 3
        setReactInput(driver.findElement(
                By.xpath("//span[contains(text(),'ROUTE 3')]/following::input[1]")), "Colombo");

        System.out.println("📝 Form fields filled in order: Destination → Lorry → Driver → Route 1 → 2 → 3 ✅");
    }

    @Test(priority = 5, dependsOnMethods = "fillDispatchFormFields")
    public void submitDispatchForm() throws InterruptedException {
        WebElement submitBtn = driver.findElement(By.xpath("//button[span[contains(text(),'Submit') or contains(text(),'සටහන් කරන්න') or contains(text(),'சமர்ப்பிக்கவும்')]]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", submitBtn);

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("📤 Form submitted. ✅ Redirected to Home page.");

        Thread.sleep(3000);
    }

    private void setReactInput(WebElement element, String value) {
        try {
            element.click();
            Thread.sleep(300);
            element.clear();
            Thread.sleep(300);

            for (char c : value.toCharArray()) {
                element.sendKeys(String.valueOf(c));
                Thread.sleep(100);
            }

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "arguments[0].value = arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    element, value
            );

            wait.until(ExpectedConditions.attributeToBe(element, "value", value));
            Thread.sleep(500);
        } catch (Exception e) {
            System.err.println("❌ Error setting input value: " + element);
            e.printStackTrace();
            Assert.fail("Failed to set input value properly.");
        }
    }

    @AfterClass
    public void tearDown() throws InterruptedException {
        Thread.sleep(5000);
        if (driver != null) {
            driver.quit(); 
            System.out.println("🧹 Browser closed. Test cleanup complete.");
        }
    }
}