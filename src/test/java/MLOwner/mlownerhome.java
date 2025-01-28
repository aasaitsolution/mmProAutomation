package MLOwner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import java.time.Duration;

public class mlownerhome {

    @Test
    public void openWebsite() {
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

            // Wait for the page to load after sign-in (adjust this wait condition as needed for your page)
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("some-element-that-appears-after-login")));

            WebElement tamilButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[span[text()='தமிழ்']]")
            ));
            tamilButton.click();
            // Wait for 10 seconds after clicking the button
            Thread.sleep(2000);

            // After 10 seconds, click the same button again
            tamilButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[span[text()='English']]")));
            tamilButton.click();System.out.println("tamil button clicked");


            WebElement sinhalaButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[span[text()='සිංහල']]")
            ));
            sinhalaButton.click();
            // Wait for 10 seconds after clicking the button
            Thread.sleep(2000);

            // After 10 seconds, click the same button again
            sinhalaButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[span[text()='English']]")));
            sinhalaButton.click();System.out.println("sinhala button clicked");


            //check view licences button and back to the home


            WebElement viewButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[span[text()='View Licenses']]")
            ));
            //click dispatch and back to the home
            viewButton.click();
            Thread.sleep(2000);
            driver.navigate().back();System.out.println("view license button clicked");

            WebElement dispatchButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[span[text()='Dispatch Load']]")
            ));
            dispatchButton.click();
            Thread.sleep(2000);
            driver.navigate().back();System.out.println("dispatch button clicked");
            //click history and back to the home


            WebElement historyButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[span[text()='History']]")
            ));
            historyButton.click();
            Thread.sleep(2000);
            driver.navigate().back();System.out.println("history button clicked");


            System.out.println("Test Passed!");

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