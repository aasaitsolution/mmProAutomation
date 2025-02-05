package GSMBOfficer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import java.time.Duration;

public class GsmbOfficerSignin {

    @Test
    public void gsmbSignin() {
        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver();

        // Open the URL
        driver.get("http://localhost:5173/signin");

        // Optionally, add a wait to let the page load before interacting
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Wait for the username field to be visible and enter the username
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            usernameField.sendKeys("nimal");

            // Wait for the password field to be visible and enter the password
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
            passwordField.sendKeys("12345678");

            // Wait and click on the 'Sign in' button
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-btn-primary")));
            signInButton.click();
            System.out.println("Signed in successfully");

            // Wait for the dashboard or any confirmation element after login
            WebElement dashboardElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".dashboard-container")));
            System.out.println("Dashboard loaded successfully");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Optionally, close the browser after a few seconds to observe the login process
            try {
                Thread.sleep(5000);  // Wait for 5 seconds to observe the login
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Close the browser after the test
            driver.quit();
        }
    }
}
