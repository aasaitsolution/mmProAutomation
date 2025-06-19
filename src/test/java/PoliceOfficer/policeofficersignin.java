package PoliceOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class policeofficersignin {
 private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void policeSignIn() {
        driver.get("http://localhost:5173/");
        try {
            // Continue with the rest of your login flow
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/signin'] button")
            ));
            loginButton.click();

            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("sign-in_username")
            ));
            usernameField.sendKeys("saman");

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("sign-in_password")
            ));
            passwordField.sendKeys("12345678");
            System.out.println("Signed in successfully");

            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit']")
            ));
            signInButton.click();
            System.out.println("Submitted username and password");

            try {
                Alert alert = driver.switchTo().alert();
                alert.dismiss(); // or .accept()
            } catch (NoAlertPresentException e) {
                System.out.println("No alert to handle.");
            }

            

            // Step 2: Wait for dashboard to load
            wait.until(ExpectedConditions.urlContains("/police-officer"));

            // Step 3: Input a vehicle number
            WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[type='text']")));
            inputField.sendKeys("ABX1234");  // Replace with test case value

            // WebDriverWait waitJava = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement checkButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.po-check-button")));
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
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            driver.quit();
        }
    }
}