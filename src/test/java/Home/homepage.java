package Home;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class homepage extends BaseTest {

    private static final String BASE_URL = "https://mmpro.aasait.lk/";

    @BeforeMethod
    public void navigateToHomepage() {
        driver.get(BASE_URL);
        waitForPageLoadComplete();

        // Verify homepage elements are present
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//button[.//span[contains(text(),'தமிழ்')]]")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//span[text()='සිංහල']/..")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'logo')] | //img[contains(@alt, 'logo')]"))
        ));
        System.out.println("✅ Homepage loaded successfully.");
    }

    private void clickElementSafely(By locator, String elementName) throws InterruptedException {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        wait.until(ExpectedConditions.elementToBeClickable(element));

        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }

        Thread.sleep(1000);
        System.out.println("✅ Clicked: " + elementName);
    }

    @Test(priority = 1)
    public void testTamilVersion() throws InterruptedException {
        clickElementSafely(By.xpath("//button[.//span[contains(text(),'தமிழ்')]]"), "Tamil language button");

        WebElement bodyText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[normalize-space(text())='வரவேற்பு']")
        ));

        Assert.assertTrue(bodyText.isDisplayed(), "❌ Tamil version content not loaded.");
        System.out.println("✅ Tamil version loaded successfully.");
    }

    @Test(priority = 2)
    public void testSinhalaVersion() throws InterruptedException {
        clickElementSafely(By.xpath("//span[text()='සිංහල']/.."), "Sinhala language button");

        WebElement bodyText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[normalize-space(text())='සාදරයෙන් පිළිගනිමු']")
        ));
        Assert.assertTrue(bodyText.isDisplayed(), "❌ Sinhala version content not loaded.");
        System.out.println("✅ Sinhala version loaded successfully.");
    }

    @Test(priority = 3)
    public void testServiceSection() throws InterruptedException {
        By serviceLocator = By.xpath("//a[contains(@href, '#service')] | //h4[contains(text(),'SERVICE')]");
        clickElementSafely(serviceLocator, "Service section link");

        WebElement serviceSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='service'] | //*[contains(@class, 'service')]")
        ));
        Assert.assertTrue(serviceSection.isDisplayed(), "❌ Service section not displayed.");
        System.out.println("✅ Service section displayed successfully.");
    }

    @Test(priority = 4)
    public void testAboutSection() throws InterruptedException {
        By aboutLocator = By.xpath("//a[contains(@href, '#about')] | //*[contains(text(),'About')]");
        clickElementSafely(aboutLocator, "About section link");

        WebElement aboutSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@id='about'] | //*[contains(@class, 'about')]")
        ));
        Assert.assertTrue(aboutSection.isDisplayed(), "❌ About section not displayed.");
        System.out.println("✅ About section displayed successfully.");
    }

    @Test(priority = 5)
    public void testContactSection() throws InterruptedException {
        By contactLocator = By.xpath("//a[contains(@href, '/contact')] | //*[contains(text(),'Contact')]");
        clickElementSafely(contactLocator, "Contact section link");

        waitForPageLoadComplete();
        WebElement contactInfo = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(@class,'contact')] | //*[contains(text(),'Contact')]")
        ));
        Assert.assertTrue(contactInfo.isDisplayed(), "❌ Contact section not displayed.");
        System.out.println("✅ Contact section displayed successfully.");
    }

    @Test(priority = 6)
    public void testLoginButton() throws InterruptedException {
        By loginBtn = By.xpath("//a/button[.//span[contains(text(),'Login') or contains(text(),'உள்நுழைய') or contains(text(),'පිවිසුම')]] | //a[contains(@href,'/signin')]");
        clickElementSafely(loginBtn, "Login button");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/signin"),
                ExpectedConditions.presenceOfElementLocated(By.id("sign-in_username"))
        ));
        Assert.assertTrue(driver.getCurrentUrl().contains("signin"), "❌ Not navigated to login page.");
        System.out.println("✅ Navigated to login page successfully.");

        driver.navigate().back();
        waitForPageLoadComplete();
        System.out.println("✅ Returned to homepage successfully.");
    }

    private void waitForPageLoadComplete() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }
}
