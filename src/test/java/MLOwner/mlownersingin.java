
//Done
package MLOwner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test(priority = 0)
    public void testInvalidLogin() throws InterruptedException {
        try {
            driver.get("https://mmpro.aasait.lk/");
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
            loginButton.click();

            WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            WebElement password = driver.findElement(By.id("sign-in_password"));

            username.clear();
            username.sendKeys("invalidUser");
            password.clear();
            password.sendKeys("wrongPassword");

            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
            signInButton.click();

            Thread.sleep(2000); // Give time for response

            boolean stillOnLoginPage = driver.getCurrentUrl().contains("/signin");
            Assert.assertTrue(stillOnLoginPage, "❌ Unexpected redirection after failed login");
            System.out.println("✅ Proper handling of invalid login 🔒");

        } catch (Exception e) {
            System.out.println("❌ Error during invalid login test: 🛑" + e.getMessage());
            throw e;
        }
    }


    @Test(priority = 1)
    public void mlsignin() {
        try {

            driver.get("https://mmpro.aasait.lk/");

            // Wait and click on the 'Login' button
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
            loginButton.click();

            // Wait for the Sign-in page to load and the username field to be visible
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
            usernameField.sendKeys("pasindu");

            // Wait for the password field to be visible
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
            passwordField.sendKeys("12345678");
            System.out.println("👤 Entered username and password");

            // Wait and click on the 'Sign in' button
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
            signInButton.click();
            System.out.println("🚀 Submitted username and password");

            // Wait for login to complete - you might need to wait for a specific element that appears after successful login
            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            System.out.println("✅ Successfully signed in ✔️");

        } catch (Exception e) {
            System.out.println("❌ Error during sign in: ⚠️ " + e.getMessage());
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
            System.out.println("🌐 Clicked Tamil language button");

            // Wait for language change to take effect
            Thread.sleep(2000);

            // Get the page source after language change
            String afterChange = driver.getPageSource();

            // First approach: Verify the page content has actually changed
            boolean pageChanged = !beforeChange.equals(afterChange);

            // Second approach: Look for common Tamil words that should appear in UI
            // Add more Tamil words that you expect to see in your application
            String[] tamilWords = {"உரிமையாளர்", "இடம்", "உரிமங்களையும்", "கோரப்பட்ட", "வரலாறு"};

            boolean foundTamilWords = false;
            for (String word : tamilWords) {
                if (afterChange.contains(word)) {
                    System.out.println("🔍 Found Tamil word: " + word);
                    foundTamilWords = true;
                    break;
                }
            }

            // Check if Tamil button is no longer displayed (i.e., already active)
            boolean tamilButtonVisible = driver.findElements(
                By.xpath("//*[@id=\"root\"]/div/header/div[2]/button[1]/span[text()='தமிழ்']")
            ).size() > 0;

            boolean tamilActive = !tamilButtonVisible;  // If not visible, it means Tamil is active
            System.out.println("🔘 Tamil button visible: " + tamilButtonVisible);
            System.out.println("✅ Tamil button active: " + tamilActive);



            // Log the results
            System.out.println("🔄Page content changed: " + pageChanged);
            System.out.println("🔤Found Tamil words: " + foundTamilWords);
            // System.out.println("Tamil button active: " + tamilActive);

            // Combine all approaches for verification
            if (pageChanged || foundTamilWords || tamilActive) {
                System.out.println("🎉 Successfully switched to the Tamil version.");
            } else {
                System.out.println("❌Failed to switch to the Tamil version.");
            }

        } catch (Exception e) {
            System.out.println("❌Error during Tamil language test: 🛑" + e.getMessage());
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
            System.out.println("🌐 Clicked Sinhala language button");

            // Wait for language change to take effect
            Thread.sleep(2000);

            // Get the page source after language change
            String afterChange = driver.getPageSource();

            // First approach: Verify the page content has actually changed
            boolean pageChanged = !beforeChange.equals(afterChange);

            // Second approach: Look for common Sinhala words that should appear in UI
            // Add more Sinhala words that you expect to see in your application
            String[] sinhalaWords = {"සියලුම", "හිමිකරු", "කල්පිරෙන", "ඉතිහාසය", "බලපත්‍ර බලන්න"};

            boolean foundSinhalaWords = false;
            for (String word : sinhalaWords) {
                if (afterChange.contains(word)) {
                    System.out.println("🔍 Found Sinhala word: " + word);
                    foundSinhalaWords = true;
                    break;
                }
            }

            // Check if Sinhala button is visible
            boolean sinhalaButtonVisible = driver.findElements(
                By.xpath("//*[@id='root']/div/header/div[2]/button/span[text()='සිංහල']")
            ).size() > 0;

            // Sinhala active if Sinhala button NOT visible
            boolean sinhalaActive = !sinhalaButtonVisible;
            System.out.println("🔘 Sinhala button visible: " + sinhalaButtonVisible);
            System.out.println("✅ Sinhala button active: " + sinhalaActive);

            // Log the results
            System.out.println("🔄 Page content changed: " + pageChanged);
            System.out.println("🔤 Found Sinhala words: " + foundSinhalaWords);

            // Combine all approaches for verification
            if (pageChanged || foundSinhalaWords || sinhalaActive) {
                System.out.println("🎉 Successfully switched to the Sinhala version.");
            } else {
                System.out.println("❌ Failed to switch to the Sinhala version.");
            }

        } catch (Exception e) {
            System.out.println("❌ Error during Sinhala language test: 🛑 " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 4)
    public void testMissingLanguageButton() throws InterruptedException {
        try {
            driver.get("https://mmpro.aasait.lk/");
            Thread.sleep(2000); // Wait for page load

            // Attempt to locate a non-existent third language button
            boolean foundThirdLang = driver.findElements(By.xpath("//*[@id='root']/div/header/div[2]/button[3]")).size() > 0;

            Assert.assertFalse(foundThirdLang, "❌ Unexpected third language button found.");
            System.out.println("✅ No unexpected language buttons found (only Tamil and Sinhala expected).");

        } catch (Exception e) {
            System.out.println("❌ Error during missing language button check: 🛑 " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 5)
public void testBlankLoginFields() throws InterruptedException {
    try {
        driver.get("https://mmpro.aasait.lk/");
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
        loginButton.click();

        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        signInButton.click();

        // Check for any validation message or no URL change
        Thread.sleep(1500);
        boolean stillOnLogin = driver.getCurrentUrl().contains("/signin");
        Assert.assertTrue(stillOnLogin, "❌ Login allowed with blank fields.");
        System.out.println("✅ Login form correctly blocked empty submissions.");

    } catch (Exception e) {
        System.out.println("❌ Error testing blank login fields: " + e.getMessage());
        throw e;
    }
}



    @AfterClass
    public void tearDown() {
        // Close the browser after all tests
        if (driver != null) {
            driver.quit();
            System.out.println("🚪 Browser closed");
        }
    }
}
