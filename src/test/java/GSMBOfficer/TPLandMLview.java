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
        wait = new WebDriverWait(driver, Duration.ofSeconds(100)); // Increased timeout
    }

    @Test
    public void testAddMLOwnerLicense() throws InterruptedException {
        try {
            // Login
            performLogin();

            // Wait for dashboard to load completely
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));

            // Add a small delay to ensure page is fully loaded
            Thread.sleep(200);

            // Find and click the "Mining License" tab
            WebElement mlTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[3]")));
            mlTab.click();

            // Wait for form to load
            Thread.sleep(500);

            WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div[2]/table/tbody/tr[2]/td[7]/div/button")));
            viewButton.click();

            Thread.sleep(2000);

            // Click close button
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div/div[2]/div/div[1]/div/button")));
            closeButton.click();

            Thread.sleep(1000);

            // Find and click the "Transport License" tab
            WebElement tplTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[2]")));
            tplTab.click();

            // Wait for form to load
            Thread.sleep(500);

            viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div[2]/table/tbody/tr[2]/td[11]/div/button")));
            viewButton.click();

            Thread.sleep(2000);

            // Click close button
            closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div/div[2]/div/div[1]/div/button")));
            closeButton.click();

            Thread.sleep(1000);

            // Find and click the "complaints" tab
            WebElement complaintsTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[4]")));
            complaintsTab.click();

            // Wait for form to load
            Thread.sleep(1000);

            WebElement paginationButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/ul/li[5]/button")));
            paginationButton.click();

            // Wait for form to load
            Thread.sleep(1000);

            WebElement Button3 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/ul/li[1]/button")));
            Button3.click();

            // Wait for form to load
            Thread.sleep(1000);

            WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/ul/li[1]/button")));
            backButton.click();

            // Wait for form to load
            Thread.sleep(1000);


        } catch (Exception e) {
            System.out.println("Test failed! Final URL: " + driver.getCurrentUrl());
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
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
