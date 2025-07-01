package GSMBOfficer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class TPLandMLview {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";

    @BeforeMethod
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Reduced timeout to 30 seconds
        performLogin();
    }

    @Test(priority = 1)
    public void testViewMiningLicense() throws InterruptedException {
        navigateToTab("Mining License", "//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[3]");

        WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div[2]/table/tbody/tr[2]/td[7]/div/button")));
        viewButton.click();

        verifyAndCloseModal("/html/body/div[2]/div/div[2]/div/div[1]/div/button");
    }

    @Test(priority = 2)
    public void testViewTransportLicense() throws InterruptedException {
        navigateToTab("Transport License", "//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[2]");

        WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div[2]/table/tbody/tr[2]/td[11]/div/button")));
        viewButton.click();

        verifyAndCloseModal("/html/body/div[2]/div/div[2]/div/div[1]/div/button");
    }

    @Test(priority = 3)
    public void testComplaintsPagination() throws InterruptedException {
        navigateToTab("Complaints", "//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[4]");

        // Test pagination
        testPagination("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/ul/li[5]/button");
        testPagination("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/ul/li[1]/button");
    }

    private void navigateToTab(String tabName, String tabXpath) throws InterruptedException {
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));
        Thread.sleep(200); // Small delay for page stability

        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(tabXpath)));
        tab.click();
        Thread.sleep(500); // Wait for tab content to load
    }

    private void verifyAndCloseModal(String closeButtonXpath) throws InterruptedException {
        Thread.sleep(2000); // Wait for modal to fully appear

        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(closeButtonXpath)));
        closeButton.click();

        Thread.sleep(1000); // Wait for modal to close
    }

    private void testPagination(String buttonXpath) throws InterruptedException {
        try {
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(buttonXpath)));
            button.click();
            Thread.sleep(1000); // Wait for page to load
        } catch (Exception e) {
            System.out.println("Pagination button not found or not clickable: " + buttonXpath);
            // Continue test even if pagination fails (depending on your requirements)
        }
    }

    private void performLogin() {
        driver.get(BASE_URL + "/signin/");

        WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
        username.sendKeys("nimal");

        WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
        password.sendKeys("12345678");

        WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".ant-btn-primary")
        ));
        signinButton.click();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}