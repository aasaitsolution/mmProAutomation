package PoliceOfficer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import java.time.Duration;

public class policeofficersignin {

    @Test
    public void policeSignIn() {
        WebDriver driver = new ChromeDriver();
        driver.get("http://localhost:5173/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Continue with the rest of your login flow
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/signin'] button")
            ));
            loginButton.click();

            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("sign-in_username")
            ));
            usernameField.sendKeys("police");

            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("sign-in_password")
            ));
            passwordField.sendKeys("1234abcd");
            System.out.println("Signed in successfully");

            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit']")
            ));
            signInButton.click();
            System.out.println("Submitted username and password");



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