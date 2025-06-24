package GeneralPublic;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
public class GeneralPublicDashboard {
    @Test
    public void publicsignin() {
        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver();

        // Open the URL
        driver.get("http://localhost:5173/");

        // Optionally, add a wait to let the page load before interacting
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Wait and click on the 'check validity' button
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"root\"]/div/main/h1/button")));
            loginButton.click();
        
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='text']")));
            inputField.sendKeys("ABX1234");  // Replace with test case value

            // WebDriverWait waitJava = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.check-button")));
            checkButton.click();

            System.out.println("Submitted vehicle number");

            // Step 4: Handle modal
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.className("modal-content")));
            String modalText = modal.getText();
            System.out.println("Modal appeared with text: " + modalText);

            if (modalText.toLowerCase().contains("invalid")) {
                System.out.println("Vehicle number marked as invalid as expected.");
            } else {
                System.out.println("Unexpected modal message.");
            }



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
