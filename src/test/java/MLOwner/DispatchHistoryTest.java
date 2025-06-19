package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class DispatchHistoryTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
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

        // Navigate to dispatch history after login
        driver.get("https://mmpro.aasait.lk/mlowner/history?licenseNumber=LLL/100/402");
    }

    @Test(priority = 2)
    public void testPageTitle() {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".history-title")));
        Assert.assertTrue(title.getText().toLowerCase().contains("dispatch"));
    }

    @Test(priority = 3)
    public void testDatePickerInteraction() {
        WebElement datePicker = wait.until(ExpectedConditions.elementToBeClickable(By.className("history-datepicker")));
        datePicker.click();

        WebElement today = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-picker-cell-today")));
        today.click();
    }

    @Test(priority = 4)
    public void testHistoryCardAndPrint() throws InterruptedException {
        List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("history-card")));
        Assert.assertTrue(cards.size() > 0, "No dispatch history cards found.");

        WebElement firstCard = cards.get(0);
        Assert.assertTrue(firstCard.getText().contains("License Number"));

        WebElement printButton = firstCard.findElement(By.tagName("button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", printButton);
        Thread.sleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", printButton);

        wait.until(ExpectedConditions.urlContains("/mlowner/home/dispatchload/TPLreceipt"));
        Assert.assertTrue(driver.getCurrentUrl().contains("TPLreceipt"));

        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("/mlowner/home/dispatchhistory"));
    }

    @Test(priority = 5)
    public void testNoRecordsDisplay() {
        driver.get("https://mmpro.aasait.lk/mlowner/home/dispatchhistory");

        WebElement empty = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-empty-description")));
        Assert.assertTrue(empty.getText().toLowerCase().contains("no dispatch"));
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
