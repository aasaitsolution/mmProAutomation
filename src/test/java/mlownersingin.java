import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import java.time.Duration;

public class mlownersingin {

    @Test
    public void mlsignin() {
        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver();

        // Open the URL
        driver.get("http://localhost:5173/");

        // Optionally, add a wait to let the page load before interacting
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Wait and click on the 'Login' button
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
            loginButton.click();

            // Wait for the Sign-in page to load and the username field to be visible
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            usernameField.sendKeys("pasindu");

            // Wait for the password field to be visible
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
            passwordField.sendKeys("12345678");
            System.out.println("signed in ok");

            // Wait and click on the 'Sign in' button
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
            signInButton.click();System.out.println("submitted username and password");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Optionally, close the browser after a few seconds to observe the language change
            try {
                Thread.sleep(5000);  // Wait for 5 seconds to let the language change process complete
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Close the browser after the test
            driver.quit();

        }
    }
}