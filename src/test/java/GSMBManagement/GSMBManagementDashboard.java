package GSMBManagement;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.Test;

import java.time.Duration;

public class GSMBManagementDashboard {

    @Test
    public void gsmSignInAndActivationFlow() {

        // Setup ChromeDriver with options
       ChromeOptions options = new ChromeOptions();
    options.addArguments("--incognito");

    WebDriver driver = new ChromeDriver(options); // Declare driver
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));

        try {
            // Step 1: Navigate to app
            driver.get("http://localhost:5173/");

            // Step 2: Click login
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/signin'] button")));
            loginButton.click();

            // Step 3: Enter credentials
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username"))).sendKeys("sunil");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password"))).sendKeys("12345678");

            WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit']")));
            signInBtn.click();

            System.out.println("Signed in and submitted credentials.");

            // Step 4: Handle browser alert if it appears
            try {
                Alert alert = driver.switchTo().alert();
                alert.dismiss(); // or .accept()
            } catch (NoAlertPresentException e) {
                System.out.println("No alert to handle.");
            }

            

            // Step 5: Click Activation button

   WebElement activationBtn = wait.until(ExpectedConditions.elementToBeClickable(
    By.xpath("//button[.//span[text()='Activation']]")
));
activationBtn.click();


            Thread.sleep(1000);// optional: allow for React transition

            System.out.println("URL after clicking Activation: " + driver.getCurrentUrl());

            // Step 6: Confirm page by checking element
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(),'Activate Officers')]")
            ));
            System.out.println("Successfully navigated to Activate Officers page.");
            
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                Thread.sleep(5000); // Just for observation if needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            driver.quit();
        }
    }
}
