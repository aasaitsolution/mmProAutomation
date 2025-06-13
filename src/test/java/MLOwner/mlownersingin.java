package MLOwner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.time.Duration;

public class mlownersingin {

    // Declare WebDriver as a class variable so all test methods can access it
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        // Create a new instance of the Chrome driver
        driver = new ChromeDriver();

        // Initialize wait
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Open the URL
        driver.get("http://localhost:5173/");

        // Maximize window for better visibility
        driver.manage().window().maximize();
    }

    @Test(priority = 1)
    public void mlsignin() {
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
            System.out.println("Entered username and password");

            // Wait and click on the 'Sign in' button
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
            signInButton.click();
            System.out.println("Submitted username and password");

            // Wait for login to complete - you might need to wait for a specific element that appears after successful login
            wait.until(ExpectedConditions.urlContains("/dashboard"));
            System.out.println("Successfully signed in");

        } catch (Exception e) {
            System.out.println("Error during sign in: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 2)
    public void testTamilVersion() {
        try {
            // Wait for page to be fully loaded after login
            Thread.sleep(2000);

            // Get the page source before language change for comparison
            String beforeChange = driver.getPageSource();

            // Click the Tamil button
            WebElement tamilButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/header/div[2]/button[1]")));
            tamilButton.click();
            System.out.println("Clicked Tamil language button");

            // Wait for language change to take effect
            Thread.sleep(2000);

            // Get the page source after language change
            String afterChange = driver.getPageSource();

            // First approach: Verify the page content has actually changed
            boolean pageChanged = !beforeChange.equals(afterChange);

            // Second approach: Look for common Tamil words that should appear in UI
            // Add more Tamil words that you expect to see in your application
            String[] tamilWords = {"பதிவு", "உள்நுழைய", "முகப்பு", "உதவி", "வரவேற்கிறோம்"};

            boolean foundTamilWords = false;
            for (String word : tamilWords) {
                if (afterChange.contains(word)) {
                    System.out.println("Found Tamil word: " + word);
                    foundTamilWords = true;
                    break;
                }
            }

            // Third approach: Check if the active language indicator shows Tamil
            boolean tamilActive = false;
            try {
                // Look for the active language button or indicator
                WebElement activeLangElement = driver.findElement(
                        By.xpath("//*[@id=\"root\"]/div/header/div[2]/button[1][contains(@class, 'active')]"));
                tamilActive = activeLangElement != null;
            } catch (Exception e) {
                // Element not found, tamilActive remains false
            }

            // Log the results
            System.out.println("Page content changed: " + pageChanged);
            System.out.println("Found Tamil words: " + foundTamilWords);
            System.out.println("Tamil button active: " + tamilActive);

            // Combine all approaches for verification
            if (pageChanged || foundTamilWords || tamilActive) {
                System.out.println("Successfully switched to the Tamil version.");
            } else {
                System.out.println("Failed to switch to the Tamil version.");
            }

        } catch (Exception e) {
            System.out.println("Error during Tamil language test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 3)
    public void testSinhalaVersion() {
        try {
            // Wait for page to be fully loaded after previous test
            Thread.sleep(2000);

            // Get the page source before language change for comparison
            String beforeChange = driver.getPageSource();

            // Click the Sinhala button
            WebElement sinhalaButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"root\"]/div/header/div[2]/button[2]")));
            sinhalaButton.click();
            System.out.println("Clicked Sinhala language button");

            // Wait for language change to take effect
            Thread.sleep(2000);

            // Get the page source after language change
            String afterChange = driver.getPageSource();

            // First approach: Verify the page content has actually changed
            boolean pageChanged = !beforeChange.equals(afterChange);

            // Second approach: Look for common Sinhala words that should appear in UI
            // Add more Sinhala words that you expect to see in your application
            String[] sinhalaWords = {"ලියාපදිංචි", "පිවිසුම", "මුල් පිටුව", "උදව්", "සාදරයෙන් පිළිගනිමු"};

            boolean foundSinhalaWords = false;
            for (String word : sinhalaWords) {
                if (afterChange.contains(word)) {
                    System.out.println("Found Sinhala word: " + word);
                    foundSinhalaWords = true;
                    break;
                }
            }

            // Third approach: Check if the active language indicator shows Sinhala
            boolean sinhalaActive = false;
            try {
                // Look for the active language button or indicator
                WebElement activeLangElement = driver.findElement(
                        By.xpath("//*[@id=\"root\"]/div/header/div[2]/button[2][contains(@class, 'active')]"));
                sinhalaActive = activeLangElement != null;
            } catch (Exception e) {
                // Element not found, sinhalaActive remains false
            }

            // Log the results
            System.out.println("Page content changed: " + pageChanged);
            System.out.println("Found Sinhala words: " + foundSinhalaWords);
            System.out.println("Sinhala button active: " + sinhalaActive);

            // Combine all approaches for verification
            if (pageChanged || foundSinhalaWords || sinhalaActive) {
                System.out.println("Successfully switched to the Sinhala version.");
            } else {
                System.out.println("Failed to switch to the Sinhala version.");
            }

        } catch (Exception e) {
            System.out.println("Error during Sinhala language test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        // Close the browser after all tests
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed");
        }
    }
}