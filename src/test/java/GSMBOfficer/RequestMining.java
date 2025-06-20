package GSMBOfficer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class RequestMining {

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

            WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div[2]/div/div/div/div/div[2]/table/tbody/tr[2]/td[8]/div/button[1]")));
            viewButton.click();

            Thread.sleep(2000);

            // Click close button
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/div[2]/div/div[2]/div/div[1]/div/button")));
            closeButton.click();

            Thread.sleep(1000);

            schedule();

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

    private void schedule() throws InterruptedException {

        // Find and click the "schedule" button
        WebElement scheduleButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div[2]/div/div/div/div/div[2]/table/tbody/tr[2]/td[8]/div/button[2]")));
        scheduleButton.click();

        // Click on the date picker input field to open calendar
        WebElement datePickerInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"date\"]")
        ));
        datePickerInput.click();

// Wait for the calendar panel to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("ant-picker-panel-container")
        ));

        Thread.sleep(300);

        WebElement dateToSelect = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//td[@title='2025-06-28']/div[@class='ant-picker-cell-inner']")
        ));
        dateToSelect.click();

// Verify the selected date appears in the input field
        WebElement selectedDate = driver.findElement(By.id("date"));
        String selectedValue = selectedDate.getAttribute("value");
        Assert.assertNotNull(selectedValue);
        Assert.assertFalse(selectedValue.isEmpty());
        System.out.println("Selected date: " + selectedValue);

        WebElement locationField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"location\"]")
        ));
        locationField.clear();
        locationField.sendKeys("GSMB Head Office" );

        WebElement purposeField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id=\"notes\"]")
        ));
        purposeField.clear();
        purposeField.sendKeys("Bring documents");

        // Click schedule button
//        WebElement schedule = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("/html/body/div[4]/div/div[2]/div/div[1]/div/div[3]/button[2]")));
//        schedule.click();
//
//        Thread.sleep(1000);

    }

    private void filters(){

    }


    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
