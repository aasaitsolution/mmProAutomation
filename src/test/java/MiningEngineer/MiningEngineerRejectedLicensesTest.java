package MiningEngineer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class MiningEngineerRejectedLicensesTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test(priority = 1)
    public void signInAsMiningEngineer() {
        driver.get("https://mmpro.aasait.lk/signin");

        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        WebElement password = driver.findElement(By.id("sign-in_password"));
        WebElement loginBtn = driver.findElement(By.cssSelector(".ant-btn-primary"));

        username.sendKeys("amal");
        password.sendKeys("12345678");
        loginBtn.click();

        WebElement rejectedTab = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'me-tab-label') and contains(., 'Rejected')]")));
        Assert.assertTrue(rejectedTab.isDisplayed(), "Login failed or dashboard did not load properly");
    }

    @Test(priority = 2, dependsOnMethods = "signInAsMiningEngineer")
    public void navigateToRejectedLicensesTabAndValidate() throws InterruptedException {
        WebElement rejectedTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'me-tab-label') and contains(., 'Rejected')]")));
        rejectedTab.click();

        // Optional short pause for visual transition
        Thread.sleep(2000);

        WebElement table = new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'ant-tabs-tabpane-active')]//table")));

        // Wait for at least one rejected row to appear
        List<WebElement> rejectedRows = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.numberOfElementsToBeMoreThan(
                        By.cssSelector(".ant-tag-error"), 0));
        Assert.assertTrue(rejectedRows.size() > 0, "No rejected licenses found");

        // Validate map links
        List<WebElement> mapLinks = driver.findElements(By.cssSelector("a[href*='google.com/maps']"));
        Assert.assertTrue(mapLinks.size() > 0, "No map links found for locations");

        // Assert View Details buttons
        List<WebElement> viewButtons = driver.findElements(By.xpath("//button[contains(text(), 'View Details')]"));
        Assert.assertTrue(viewButtons.size() > 0, "No 'View Details' buttons found");

        // Click View Details
        viewButtons.get(0).click();

        WebElement modal = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-modal")));
        Assert.assertTrue(modal.isDisplayed(), "View License modal did not appear");

        WebElement closeBtn = modal.findElement(By.cssSelector(".ant-modal-close"));
        closeBtn.click();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOf(modal));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
