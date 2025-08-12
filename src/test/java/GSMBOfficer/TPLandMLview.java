package GSMBOfficer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

public class TPLandMLview {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "https://mmpro.aasait.lk";
    private static final String USERNAME = "nimal";
    private static final String PASSWORD = "12345678";

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.setAcceptInsecureCerts(true);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        performLogin();
    }

    @Test(priority = 1)
    public void testViewMiningLicense() {
        navigateToTab("Mining License", "//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[3]");

        WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='View']]")));
        viewButton.click();

        verifyAndCloseModal("/html/body/div[2]/div/div[2]/div/div[1]/div/button");
    }

    @Test(priority = 2)
    public void testViewTransportLicense() {
        navigateToTab("Transport License", "//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[2]");

        WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='View']]")));
        viewButton.click();

        verifyAndCloseModal("/html/body/div[2]/div/div[2]/div/div[1]/div/button");
    }

    @Test(priority = 3)
    public void testComplaintsPagination() {
        navigateToTab("Complaints", "//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[4]");

        // Test pagination
        testPagination("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/ul/li[5]/button");
        testPagination("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/ul/li[1]/button");
    }

    private void navigateToTab(String tabName, String tabXpath) {
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));
        waitABit(200);

        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(tabXpath)));
        tab.click();
        waitABit(500);
    }

    private void verifyAndCloseModal(String closeButtonXpath) {
        waitABit(2000);

        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(closeButtonXpath)));
        closeButton.click();

        waitABit(1000);
    }

    private void testPagination(String buttonXpath) {
        try {
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(buttonXpath)));
            button.click();
            waitABit(1000);
        } catch (Exception e) {
            System.out.println("Pagination button not found or not clickable: " + buttonXpath);
        }
    }

    private void performLogin() {
        driver.get(BASE_URL + "/signin/");

        WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
        username.sendKeys(USERNAME);

        WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
        password.sendKeys(PASSWORD);

        WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".ant-btn-primary")));
        signinButton.click();

        // Verify login success
        wait.until(ExpectedConditions.urlContains("/gsmb/dashboard"));
    }

    private void waitABit(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}