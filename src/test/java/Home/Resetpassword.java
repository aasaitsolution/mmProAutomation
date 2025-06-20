package Home;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;


public class Resetpassword {
    @Test
public void resetPassword() {
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized");
    WebDriver driver = new ChromeDriver(options);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    try {
        // STEP 1: Open the home page
        driver.get("http://localhost:5173/");
        System.out.println("Page title: " + driver.getTitle());
        Thread.sleep(15000);  // optional manual wait to observe page

        // STEP 2: Click Sign In
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")
        ));
        loginButton.click();

        
        // STEP 3: Click on "Forgot Password?"
        WebElement forgotPasswordBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector(".links")
        ));
        forgotPasswordBtn.click();

        // STEP 4: Wait for Forgot Password modal and input email
        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".fp-modal input[type='text']"))
        );
        emailInput.sendKeys("frtestemailuse@gmail.com");

        // STEP 5: Click Submit button
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".fp-modal .submit-button")));
        submitButton.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".fp-modal")));
        System.out.println("Forgot Password modal closed");

        // STEP 6: Wait for confirmation modal
        // wait for any Ant modal to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-modal")));

        // check if the modal with "confirmation-modal" class exists
        List<WebElement> allModals = driver.findElements(By.cssSelector(".ant-modal"));
        System.out.println("Number of modals found: " + allModals.size());

        for (WebElement modal : allModals) {
            System.out.println("Modal classes: " + modal.getAttribute("class"));
            String text = modal.getText();
            System.out.println("Modal text snippet: " + (text.length() > 100 ? text.substring(0, 100) + "..." : text));
        }

        // Then your existing check for the specific confirmation modal class (optional)
        boolean found = false;
        for (WebElement modal : allModals) {
            if (modal.getAttribute("class").contains("confirmation-modal")) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new RuntimeException("Confirmation modal with class 'confirmation-modal' not found!");
        }
        System.out.println("✅ Confirmation modal displayed");

        // STEP 7: Simulate clicking password reset link from email
        // This must match your frontend token logic and exist in backend
        driver.get("https://mmpro.aasait.lk/reset-password?token={reset_token}");

        // STEP 8: Fill in new password and confirm password
        WebElement newPasswordInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("newPassword"))
        );
        WebElement confirmPasswordInput = driver.findElement(By.name("confirmPassword"));

        // STEP 10: Fill new password
        newPasswordInput.sendKeys("NewPassword123");
        confirmPasswordInput.sendKeys("NewPassword123");

        // STEP 11: Submit new password
        WebElement resetSubmitButton = driver.findElement(By.cssSelector(".reset-password-modal .submit-button"));
        resetSubmitButton.click();

        // STEP 12: Wait for redirection or success confirmation
        wait.until(ExpectedConditions.urlContains("/signin"));
        System.out.println("✅ Password reset completed and redirected to sign-in");

    } catch (Exception e) {
        System.err.println("❌ Test failed: " + e.getMessage());
    } finally {
        driver.quit(); // quit only once at the end
    }
}

    
}
