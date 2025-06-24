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
        driver.get("http://localhost:5173/");
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
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("gp-modal-body")));
             WebElement input = modal.findElement(By.cssSelector("input.valid-message"));
    String modalValue = input.getAttribute("value");  // Correct way to read <input value="...">
    System.out.println("📩 Modal appeared with value: " + modalValue);


            if (modalValue.toLowerCase().contains("valid")) {
                System.out.println("✅ Vehicle number marked as Valid");
            } else {
                System.out.println("⚠️ Unexpected modal message");
            }
        } catch (TimeoutException e) {
            System.out.println("❌ Modal did not appear");
            Assert.fail("Modal not found after checking license number");
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
