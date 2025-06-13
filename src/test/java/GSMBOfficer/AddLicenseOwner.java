package GSMBOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

public class AddLicenseOwner {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";

    @BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Increased timeout
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    public void testAddLicenseOwner() throws InterruptedException {
        try {
            // Logina
            performLogin();

            // Wait for dashboard to load completely
            wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));

            // Click "Register New Owner" button
            WebElement addNewOwnerButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[3]/div[2]/div/a[1]/button")
            ));
            addNewOwnerButton.click();

            // Wait for owner name field and enter value
            WebElement firstNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"firstname\"]")
            ));
            firstNameField.sendKeys("Kamal");

            WebElement lastNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"lastname\"]")
            ));
            lastNameField.sendKeys("Perera");

            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"mail\"]")
            ));
            emailField.sendKeys("Kamal@gmail.com");

            WebElement mobileField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"phoneNumber\"]")
            ));
            mobileField.sendKeys("0789076745");

            WebElement addressField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"address\"]")
            ));
            addressField.sendKeys("No, 43/A, Colombo");

            WebElement nicField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"NIC\"]")
            ));
            nicField.sendKeys("200056320956");



            // Click to create the license
            WebElement createLicenseOwnerButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"root\"]/div/main/div/form/div/div[7]/div/div/div/div/div/div/button[1]")
            ));
            Thread.sleep(100);

        } catch (Exception e) {
            System.out.println("Test failed! Final URL: " + driver.getCurrentUrl());
            System.out.println("Exception: " + e.getMessage());

            // Print page source to see what's on the page
            System.out.println("Page source: " + driver.getPageSource());

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
