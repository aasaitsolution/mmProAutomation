package GSMBOfficer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import java.time.Duration;

public class GSMBmlTab {
    @Test
    public void gsmbSignin() {
        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver();

        // Open the URL
        driver.get("http://localhost:5173/signin");

        // Create wait instance with longer timeout
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

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

            // Wait for the dashboard to load fully - increase wait time
            Thread.sleep(3000);

            // Try multiple approaches to click the ML Owner tab
            try {
                // Approach 1: Try with proper XPath escaping
                WebElement mlOwnerTab = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='rc-tabs-1-tab-MLOWNER']")));
                mlOwnerTab.click();
                System.out.println("Clicked ML Owner tab using approach 1");
            } catch (Exception e1) {
                try {
                    // Approach 2: Try with text content
                    WebElement mlOwnerTab = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//div[contains(text(),'ML Owner')]")));
                    mlOwnerTab.click();
                    System.out.println("Clicked ML Owner tab using approach 2");
                } catch (Exception e2) {
                    try {
                        // Approach 3: Try with JavaScript click
                        WebElement mlOwnerTab = driver.findElement(By.xpath("//*[@id='rc-tabs-1-tab-MLOWNER']"));
                        JavascriptExecutor executor = (JavascriptExecutor) driver;
                        executor.executeScript("arguments[0].click();", mlOwnerTab);
                        System.out.println("Clicked ML Owner tab using approach 3 (JavaScript)");
                    } catch (Exception e3) {
                        try {
                            // Approach 4: Try with partial ID match
                            WebElement mlOwnerTab = wait.until(ExpectedConditions.elementToBeClickable(
                                    By.xpath("//*[contains(@id, 'MLOWNER')]")));
                            mlOwnerTab.click();
                            System.out.println("Clicked ML Owner tab using approach 4");
                        } catch (Exception e4) {
                            System.out.println("All attempts to click ML Owner tab failed.");
                            throw new RuntimeException("Unable to click ML Owner tab after multiple attempts");
                        }
                    }
                }
            }

            // Wait after clicking tab
            Thread.sleep(200);

            // Click the "plus" button before clicking the add button
            try {
                WebElement plusButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div/div/table/tbody/tr[1]/td[1]/button")));
                plusButton.click();
                System.out.println("Clicked the plus button");
            } catch (Exception e) {
                // Try alternative approach with JavaScript if the first attempt fails
                try {
                    WebElement pusButton = driver.findElement(By.xpath("//button[contains(@id,'pus')]"));
                    JavascriptExecutor executor = (JavascriptExecutor) driver;
                    executor.executeScript("arguments[0].click();", pusButton);
                    System.out.println("Clicked the plus button using JavaScript");
                } catch (Exception ex) {
                    System.out.println("Failed to click the plus button: " + ex.getMessage());
                    // Continue execution even if plus button is not found
                }
            }

            // Add a small wait after clicking pus button
            Thread.sleep(500);

            // Find and click the "+" button in the ML Owner table
            WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div/div/table/tbody/tr[1]/td[8]/a/button")));
            addButton.click();
            System.out.println("Clicked the + button in ML Owner table");

        } catch (Exception e) {
            System.out.println("Test failed with error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Wait to observe the results
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Close the browser
            driver.quit();
        }
    }
}