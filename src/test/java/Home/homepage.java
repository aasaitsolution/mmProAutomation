package Home;

import base.BaseTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class homepage extends BaseTest {
    private static final Log log = LogFactory.getLog(homepage.class);
    private static final String BASE_URL = "https://mmpro.aasait.lk/";

    private void navigateToHomepage() {
        driver.get(BASE_URL);
        System.out.println("Navigated to homepage: " + BASE_URL);
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
        navigateToHomepage();

        try {
            By tamilBtn = By.xpath("//button[.//span[contains(text(),'தமிழ்')]]");
            clickElement(tamilBtn);

            WebElement bodyText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'வரவேற்பு') or contains(text(), 'mmPro')]")
            ));
            Assert.assertTrue(bodyText.isDisplayed(), "Tamil version not loaded.");
            System.out.println("✅ Successfully switched to the Tamil version.");

        } catch (Exception e) {
            Assert.fail("Tamil version test failed: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void testSinhalaVersion() {
        navigateToHomepage();

        try {
            By sinhalaBtn = By.xpath("//span[text()='සිංහල']/..");
            clickElement(sinhalaBtn);

            WebElement bodyText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'සාදරයෙන් පිළිගනිමු') or contains(text(), 'mmPro')]")
            ));
            Assert.assertTrue(bodyText.isDisplayed(), "Sinhala version not loaded.");
            System.out.println("✅ Successfully switched to the Sinhala version.");

        } catch (Exception e) {
            Assert.fail("Sinhala version test failed: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void testServiceSection() {
        navigateToHomepage();

        try {
            WebElement serviceHeading = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//h4[contains(text(), 'SERVICE') or contains(text(), 'සේවාව') or contains(text(), 'சேவை')]")
            ));
            serviceHeading.click();

            WebElement serviceSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='service']")
            ));

            Assert.assertTrue(serviceSection.isDisplayed(), "Service section not displayed.");
            System.out.println("✅ Successfully scrolled to the Service section.");

        } catch (Exception e) {
            Assert.fail("Service section test failed: " + e.getMessage());
        }
    }

    @Test(priority = 4)
    public void testAboutSection() {
        navigateToHomepage();

        try {
            By aboutLocator = By.xpath("//a[contains(@href, '#about')]");
            clickElement(aboutLocator);

            WebElement aboutSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("about")));
            Assert.assertTrue(aboutSection.isDisplayed(), "About section not displayed.");
            System.out.println("✅ Successfully scrolled to the About section.");

        } catch (Exception e) {
            Assert.fail("About section test failed: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void testContactSection() {
        navigateToHomepage();

        try {
            By contactLocator = By.xpath("//a[contains(@href, '/contact')]");
            clickElement(contactLocator);

            // Debug
            System.out.println("Current URL after clicking Contact: " + driver.getCurrentUrl());

            By contactInfoLocator = By.cssSelector(".left-section.contact-info");
            WebElement contactInfo = wait.until(ExpectedConditions.presenceOfElementLocated(contactInfoLocator));
            Assert.assertTrue(contactInfo.isDisplayed(), "Contact info section is not displayed.");
            System.out.println("✅ Successfully navigated to the Contact section.");

        } catch (Exception e) {
            Assert.fail("Contact section test failed: " + e.getMessage());
        }
    }

    @Test(priority = 6)
    public void testLoginButton() {
        navigateToHomepage();

        try {
            // Correct XPath using multiple OR conditions properly
            By loginBtn = By.xpath("//a/button[.//span[contains(text(),'Login') or contains(text(),'உள்நுழைய') or contains(text(),'පිවිසුම')]]");

            clickElement(loginBtn);

            wait.until(ExpectedConditions.urlContains("/signin"));
            Assert.assertTrue(driver.getCurrentUrl().contains("/signin"), "Login navigation failed.");
            System.out.println("✅ Navigated to Login page successfully.");

            driver.navigate().back(); // Return to homepage
            System.out.println("✅ Returned to homepage successfully.");

        } catch (Exception e) {
            Assert.fail("Login button test failed: " + e.getMessage());
        }
    }

    @Test(priority = 7)
    public void testHomepageElementsVisibility() {
        navigateToHomepage();

        try {
            // Test that key homepage elements are visible
            WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//img[contains(@src, 'logo') or contains(@alt, 'logo')] | //div[contains(@class, 'logo')]")
            ));
            Assert.assertTrue(logo.isDisplayed(), "Logo is not visible on homepage");

            // Test language buttons are present
            WebElement tamilBtn = driver.findElement(By.xpath("//button[.//span[contains(text(),'தமிழ்')]]"));
            WebElement sinhalaBtn = driver.findElement(By.xpath("//span[text()='සිංහල']/.."));

            Assert.assertTrue(tamilBtn.isDisplayed(), "Tamil language button not visible");
            Assert.assertTrue(sinhalaBtn.isDisplayed(), "Sinhala language button not visible");

            System.out.println("✅ Homepage elements visibility test passed.");

        } catch (Exception e) {
            Assert.fail("Homepage elements visibility test failed: " + e.getMessage());
        }
    }
}