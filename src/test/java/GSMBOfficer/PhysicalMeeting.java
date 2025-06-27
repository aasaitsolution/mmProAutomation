package GSMBOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

public class PhysicalMeeting {

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

            // Find and click the "Request Mining" tab
            WebElement mlTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[5]")));
            mlTab.click();

            // Wait for form to load
            Thread.sleep(500);

            physicalMeetingApprove();
            physicalMeetingReject();


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

    private void physicalMeetingApprove() throws InterruptedException {

        // Find and click the "Physical Meeting Status" button
        WebElement physicalButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'ant-btn') and .//span[text()='Physical Meeting Status']]")
        ));
        physicalButton.click();

        // Click Approve button
        WebElement approve = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[2]/div/div[2]/div/div[1]/div/div[2]/div/div/div[2]/button")));
        approve.click();

        Thread.sleep(1000);

        WebElement commentsField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"comments\"]")
        ));
        commentsField.clear();
        commentsField.sendKeys("License is approved;" );

        String projectPath = System.getProperty("user.dir");
        String reciptPath = projectPath + "/src/test/resources/test_images/recipt.jpg";

        try {
            WebElement nicFront = driver.findElement(By.id("nicFront"));
            nicFront.sendKeys(reciptPath);
        } catch (NoSuchElementException e) {
            System.out.println("NIC Front upload field not found");
        }
    }

    private void physicalMeetingReject() throws InterruptedException {

        // Find and click the "Physical Meeting Status" button
        WebElement physicalButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'ant-btn') and .//span[text()='Physical Meeting Status']]")
        ));
        physicalButton.click();

        // Click Reject button
        WebElement reject = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[2]/div/div[2]/div/div[1]/div/div[2]/div/div/div[1]/button")));
        reject.click();

        Thread.sleep(1000);
    }


        private void fillFieldIfPresent(WebDriver driver, WebDriverWait wait, String id, String value) {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
            element.clear();
            element.sendKeys(value);
            // Trigger change event if needed
            ((JavascriptExecutor)driver).executeScript("arguments[0].dispatchEvent(new Event('change'))", element);
        }

        private void createDummyFileIfNotExists(String path) throws IOException {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        }


    @AfterMethod
    public void tearDown() {
        if (driver != null) {
//            driver.quit();
        }
    }
}
