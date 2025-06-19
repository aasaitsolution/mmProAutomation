package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;

import java.time.Duration;

public class DispatchLoadPageTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Adjusted to a reasonable timeout
    }

    @Test(priority = 1)
    public void loginToMLDashboard() {
        driver.get("https://mmpro.aasait.lk/");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginBtn.click();

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        WebElement password = driver.findElement(By.id("sign-in_password"));

        username.sendKeys("pasindu");
        password.sendKeys("12345678");

        WebElement signIn = driver.findElement(By.cssSelector("button[type='submit']"));
        signIn.click();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Login successful.");
    }

    @Test(priority = 2, dependsOnMethods = "loginToMLDashboard")
    public void testDispatchLoadForm() {
        // Navigate to Dispatch Load page
        driver.get("https://mmpro.aasait.lk/mlowner/home/dispatchload/LLL/100/402");

        // Wait for form to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(text(),'Dispatch Your Load Here') or contains(text(),'යැවිය යුතු ප්‍රමාණ පිළිබඳ මෙහි සටහන් කරන්න') or contains(text(),'உங்கள் சுமையை இங்கே அனுப்பவும்')]")));

        // ===== Destination =====
        WebElement destinationInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'DESTINATION:') or contains(text(),'ගමනාන්තය:') or contains(text(),'சேருமிடம்:')]/following::input[1]")));
        destinationInput.click();
        destinationInput.clear();
        destinationInput.sendKeys("Matara");

        WebElement mataraOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@class='ant-select-item-option-content' and contains(text(),'Matara')]")));
        mataraOption.click();

        // ===== Lorry Number =====
        WebElement lorryInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'LORRY NUMBER:') or contains(text(),'ලොරිය අංකය:') or contains(text(),'லொறி எண்:')]/following::input[1]")));

// Instead of sendKeys, use JavaScript to ensure React registers the input
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                lorryInput, "ABC1234"
        );


        // ===== Driver Contact =====
        WebElement driverContactInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'DRIVER CONTACT:') or contains(text(),'රියදුරු සම්බන්ධතා:') or contains(text(),'சாரதி தொடர்பு:')]/following::input[1]")));
        driverContactInput.clear();
        driverContactInput.sendKeys("0711234567");

        // ===== Routes =====
        WebElement route1 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'ROUTE 1:') or contains(text(),'මාර්ගය 1:') or contains(text(),'வழி 1:')]/following::input[1]")));
        route1.clear();
        route1.sendKeys("Matara");

        WebElement route2 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'ROUTE 2:') or contains(text(),'මාර්ගය 2:') or contains(text(),'வழி 2:')]/following::input[1]")));
        route2.clear();
        route2.sendKeys("Galle");

        WebElement route3 = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'ROUTE 3:') or contains(text(),'මාර්ගය 3:') or contains(text(),'வழி 3:')]/following::input[1]")));
        route3.clear();
        route3.sendKeys("Colombo");

        // ===== Cubes =====
        WebElement cubeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'CUBES:') or contains(text(),'කියුබ් ගණන:') or contains(text(),'கனசதுரங்கள்:')]/following::input[1]")));
        if (!cubeInput.getAttribute("value").equals("1")) {
            cubeInput.clear();
            cubeInput.sendKeys("1");
        }

        // ===== Submit Button =====
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[contains(text(),'Submit') or contains(text(),'සටහන් කරන්න') or contains(text(),'சமர்ப்பிக்கவும்')]]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

        // ===== Verify Success Modal =====
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("ant-modal-content")));
        System.out.println("Success modal is visible.");

        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal-body')]//div[contains(text(),'success') or contains(text(),'සාර්ථක') or contains(text(),'வெற்றிகரமான')]")));
        System.out.println("Success message: " + successMessage.getText());

        WebElement backBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[contains(text(),'Back') or contains(text(),'ආපසු') or contains(text(),'பின்செல்')]]")));
        backBtn.click();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Returned to home page.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            // driver.quit(); // Uncomment this for real execution
            System.out.println("Browser closed.");
        }
    }
}

