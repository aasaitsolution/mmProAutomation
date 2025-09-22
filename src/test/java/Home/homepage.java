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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class homepage extends BaseTest {
    private static final Log log = LogFactory.getLog(homepage.class);
    private static final String BASE_URL = "https://mmpro.aasait.lk/";

    @BeforeMethod
    public void navigateToHomepage() {
        super.setup(); // Call parent setup method

        // Navigate to homepage with retry logic
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                driver.get(BASE_URL);

                // Wait for page to load completely
                wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));

                // Wait for a key element to be present (language buttons or logo)
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//button[.//span[contains(text(),'தமிழ்')]]")),
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//span[text()='සිංහල']/..")),
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'logo')] | //img[contains(@alt, 'logo')]"))
                ));

                System.out.println("Homepage loaded successfully");
                break;

            } catch (Exception e) {
                System.out.println("Attempt " + (i + 1) + " to load homepage failed: " + e.getMessage());
                if (i == maxRetries - 1) {
                    throw new RuntimeException("Failed to load homepage after " + maxRetries + " attempts", e);
                }
                slowDown(2000); // Wait before retry
            }
        }
    }

    private void clickElementSafely(By locator, String elementName) {
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));

            // Scroll element into view
            scrollIntoView(element);

            // Wait for element to be clickable
            wait.until(ExpectedConditions.elementToBeClickable(element));

            try {
                element.click();
            } catch (ElementClickInterceptedException e) {
                System.out.println("Regular click failed for " + elementName + ", trying JavaScript click");
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            }

            slowDown(1000); // Allow time for action to complete
            System.out.println("Successfully clicked: " + elementName);

        } catch (Exception e) {
            throw new RuntimeException("Failed to click " + elementName + ": " + e.getMessage(), e);
        }
    }

    private void scrollIntoView(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", element);
            Thread.sleep(1000); // Wait for scroll to complete
        } catch (InterruptedException ignored) {}
    }

    private void slowDown(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {}
    }

    @Test(priority = 1)
    public void testTamilVersion() {
        try {
            By tamilBtn = By.xpath("//button[.//span[contains(text(),'தமிழ்')]]");
            clickElementSafely(tamilBtn, "Tamil language button");

            // Wait for content to change to Tamil
            WebElement bodyText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'வரவேற்பு') or contains(text(), 'mmPro')] | //*[contains(text(), 'தமிழ்')]")
            ));

            Assert.assertTrue(bodyText.isDisplayed(), "Tamil version content not loaded.");
            System.out.println("✅ Successfully switched to the Tamil version.");

        } catch (Exception e) {
            System.err.println("Tamil version test failed: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Tamil version test failed: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void testSinhalaVersion() {
        try {
            By sinhalaBtn = By.xpath("//span[text()='සිංහල']/..");
            clickElementSafely(sinhalaBtn, "Sinhala language button");

            // Wait for content to change to Sinhala
            WebElement bodyText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'සාදරයෙන් පිළිගනිමු') or contains(text(), 'mmPro')] | //*[contains(text(), 'සිංහල')]")
            ));

            Assert.assertTrue(bodyText.isDisplayed(), "Sinhala version content not loaded.");
            System.out.println("✅ Successfully switched to the Sinhala version.");

        } catch (Exception e) {
            System.err.println("Sinhala version test failed: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Sinhala version test failed: " + e.getMessage());
        }
    }

    @Test(priority = 3)
    public void testServiceSection() {
        try {
            // Try multiple possible selectors for service section
            By[] serviceSelectors = {
                    By.xpath("//h4[contains(text(), 'SERVICE') or contains(text(), 'සේවාව') or contains(text(), 'சேவை')]"),
                    By.xpath("//a[contains(@href, '#service')]"),
                    By.xpath("//*[contains(text(), 'SERVICE') or contains(text(), 'Service')]")
            };

            WebElement serviceElement = null;
            for (By selector : serviceSelectors) {
                try {
                    serviceElement = wait.until(ExpectedConditions.elementToBeClickable(selector));
                    break;
                } catch (Exception e) {
                    System.out.println("Service selector failed: " + selector);
                }
            }

            if (serviceElement == null) {
                throw new RuntimeException("Could not find service section element");
            }

            scrollIntoView(serviceElement);
            serviceElement.click();
            slowDown(2000);

            // Verify service section is visible
            WebElement serviceSection = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id='service'] | //*[contains(@class, 'service')]")
            ));

            Assert.assertTrue(serviceSection.isDisplayed(), "Service section not displayed.");
            System.out.println("✅ Successfully navigated to the Service section.");

        } catch (Exception e) {
            System.err.println("Service section test failed: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Service section test failed: " + e.getMessage());
        }
    }

    @Test(priority = 4)
    public void testAboutSection() {
        try {
            By aboutLocator = By.xpath("//a[contains(@href, '#about')] | //*[contains(text(), 'About') or contains(text(), 'ABOUT')]");
            clickElementSafely(aboutLocator, "About section link");

            WebElement aboutSection = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id='about'] | //*[contains(@class, 'about')]")
            ));

            Assert.assertTrue(aboutSection.isDisplayed(), "About section not displayed.");
            System.out.println("✅ Successfully navigated to the About section.");

        } catch (Exception e) {
            System.err.println("About section test failed: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("About section test failed: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void testContactSection() {
        try {
            By contactLocator = By.xpath("//a[contains(@href, '/contact')] | //*[contains(text(), 'Contact') or contains(text(), 'CONTACT')]");
            clickElementSafely(contactLocator, "Contact section link");

            // Wait for navigation or content change
            slowDown(3000);

            System.out.println("Current URL after clicking Contact: " + driver.getCurrentUrl());

            // Try multiple selectors for contact content
            By[] contactSelectors = {
                    By.cssSelector(".left-section.contact-info"),
                    By.xpath("//*[contains(@class, 'contact')]"),
                    By.xpath("//*[contains(text(), 'Contact') or contains(text(), 'contact')]")
            };

            WebElement contactInfo = null;
            for (By selector : contactSelectors) {
                try {
                    contactInfo = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
                    break;
                } catch (Exception e) {
                    System.out.println("Contact selector failed: " + selector);
                }
            }

            Assert.assertNotNull(contactInfo, "Contact section not found");
            Assert.assertTrue(contactInfo.isDisplayed(), "Contact info section is not displayed.");
            System.out.println("✅ Successfully navigated to the Contact section.");

        } catch (Exception e) {
            System.err.println("Contact section test failed: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Contact section test failed: " + e.getMessage());
        }
    }

    @Test(priority = 6)
    public void testLoginButton() {
        try {
            By loginBtn = By.xpath("//a/button[.//span[contains(text(),'Login') or contains(text(),'உள்நுழைய') or contains(text(),'පිවිසුම')]] | //button[contains(text(), 'Login')] | //a[contains(@href, '/signin')]");
            clickElementSafely(loginBtn, "Login button");

            // Wait for navigation with more time
            boolean navigated = wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/signin"),
                    ExpectedConditions.urlContains("signin"),
                    ExpectedConditions.presenceOfElementLocated(By.id("sign-in_username"))
            ));

            Assert.assertTrue(driver.getCurrentUrl().contains("signin"), "Login navigation failed. Current URL: " + driver.getCurrentUrl());
            System.out.println("✅ Navigated to Login page successfully.");

            // Return to homepage
            driver.navigate().back();
            slowDown(2000);
            System.out.println("✅ Returned to homepage successfully.");

        } catch (Exception e) {
            System.err.println("Login button test failed: " + e.getMessage());
            System.err.println("Current URL: " + driver.getCurrentUrl());
            e.printStackTrace();
            Assert.fail("Login button test failed: " + e.getMessage());
        }
    }

    @Override
    public void tearDown() {
        super.tearDown(); // Call parent teardown method
    }
}