package Home;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;



public class homepage {
    private static final Log log = LogFactory.getLog(homepage.class);
    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:5173/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    private void clickElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollIntoView(element);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        slowDown(800);
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
    }

     private void slowDown(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {}
    }

    @Test(priority = 1)
    public void testTamilVersion() {
        By tamilBtn = By.xpath("//button[.//span[contains(text(),'தமிழ்')]]");
        clickElement(tamilBtn);

        WebElement bodyText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(), 'வரவேற்பு') or contains(text(), 'mmPro')]")
        ));
        Assert.assertTrue(bodyText.isDisplayed(), "Tamil version not loaded.");
        System.out.println("✅ Successfully switched to the Tamil version.");
    }

    @Test(priority = 2)
    public void testSinhalaVersion() {
        By sinhalaBtn = By.xpath("//span[text()='සිංහල']/..");
        clickElement(sinhalaBtn);

        WebElement bodyText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(text(), 'සාදරයෙන් පිළිගනිමු') or contains(text(), 'mmPro')]")
        ));
        Assert.assertTrue(bodyText.isDisplayed(), "Sinhala version not loaded.");
        System.out.println("✅ Successfully switched to the Sinhala version.");
    }

    @Test(priority = 3)
    public void testServiceSection() {
        WebElement serviceHeading = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//h4[contains(text(), 'SERVICE') or contains(text(), 'සේවාව') or contains(text(), 'சேவை')]")
        ));
        serviceHeading.click();

        WebElement serviceSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[@id='service']")
        ));

        Assert.assertTrue(serviceSection.isDisplayed(), "Service section not displayed.");
        System.out.println("✅ Successfully scrolled to the Service section.");
    }

    @Test(priority = 4)
    public void testAboutSection() {
        By aboutLocator = By.xpath("//a[contains(@href, '#about')]");
        clickElement(aboutLocator);

        WebElement aboutSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("about")));
        Assert.assertTrue(aboutSection.isDisplayed(), "About section not displayed.");
        System.out.println("✅ Successfully scrolled to the About section.");
    }

    @Test(priority = 5)
    public void testContactSection() {
        By contactLocator = By.xpath("//a[contains(@href, '/contact')]");
        clickElement(contactLocator);

        // Debug
        System.out.println("Current URL after clicking Contact: " + driver.getCurrentUrl());

        By contactInfoLocator = By.cssSelector(".left-section.contact-info");
        WebElement contactInfo = wait.until(ExpectedConditions.presenceOfElementLocated(contactInfoLocator));
        Assert.assertTrue(contactInfo.isDisplayed(), "Contact info section is not displayed.");

    }

    @Test(priority = 6)
    public void testLoginButton() {
        // Correct XPath using multiple OR conditions properly
        By loginBtn = By.xpath("//a/button[.//span[contains(text(),'Login') or contains(text(),'உள்நுழைய') or contains(text(),'පිවිසුම')]]");

        clickElement(loginBtn);

        wait.until(ExpectedConditions.urlContains("/signin"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/signin"), "Login navigation failed.");
        System.out.println("✅ Navigated to Login page successfully.");

        driver.navigate().back(); // Return to homepage
    }

    


    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}











