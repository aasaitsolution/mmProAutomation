//Done
package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import org.testng.Assert;

import java.time.Duration;
import java.util.List;

public class LicensesPageTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test(priority = 1)
    public void loginToDashboard() {
        try {
            driver.get("https://mmpro.aasait.lk/");

            // Wait for page to load completely
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));

            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href='/signin'] button")));
            loginBtn.click();

            WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("sign-in_username")));
            WebElement password = driver.findElement(By.id("sign-in_password"));

            username.clear();
            username.sendKeys("pasindu");
            password.clear();
            password.sendKeys("12345678");

            WebElement signInBtn = driver.findElement(By.cssSelector("button[type='submit']"));
            signInBtn.click();

            // Wait for redirect to home page
            wait.until(ExpectedConditions.urlContains("/mlowner/home"));
            System.out.println("✅ Login successful");

        } catch (Exception e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 2, dependsOnMethods = "loginToDashboard")
    public void navigateToLicensesPage() throws InterruptedException {
        try {
            // Wait for page to load and find the View Licenses link
            WebElement viewLicensesLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='/mlowner/home/viewlicenses']")));

            // Scroll to element and click
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", viewLicensesLink);
            Thread.sleep(500); // Wait for scroll animation

            // Use JavaScript click for reliability
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewLicensesLink);

            // Wait for navigation to licenses page
            wait.until(ExpectedConditions.urlContains("/mlowner/home/viewlicenses"));

            // Wait for page content to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("container")));

            System.out.println("✅ Navigated to Licenses page");

        } catch (Exception e) {
            System.err.println("❌ Navigation to Licenses page failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 3, dependsOnMethods = "navigateToLicensesPage")
    public void testSearchFunctionality() throws InterruptedException {
        try {
            // Wait for search input to be available
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input.search-input")));

            // Clear and enter search text
            searchInput.clear();
            searchInput.sendKeys("ML");

            // Wait for search results to update
            Thread.sleep(2000);

            // Check if any license cards are present
            List<WebElement> cards = driver.findElements(By.cssSelector(".license-card"));

            if (cards.isEmpty()) {
                // Check if "No Data Available" message is shown
                WebElement emptyState = driver.findElement(By.cssSelector(".ant-empty"));
                Assert.assertNotNull(emptyState, "Neither license cards nor empty state found");
                System.out.println("⚠️ No licenses found for search term 'ML'");
            } else {
                System.out.println("✅ Search functionality working - found " + cards.size() + " license(s)");
            }

        } catch (Exception e) {
            System.err.println("❌ Search functionality test failed: " + e.getMessage());
            throw e;
        }
    }

//    @Test(priority = 4, dependsOnMethods = "navigateToLicensesPage")
//    public void testDateFilters() throws InterruptedException {
//        try {
//            // Refresh the page to reset any previous filters
//            driver.navigate().refresh();
//            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
//            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input.search-input")));
//            Thread.sleep(1000); // Pause for UI to settle after refresh
//
//            // Find the Ant Design Range Picker component
//            WebElement dateRangePicker = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-picker-range")));
//
//            // Click the range picker to open the calendar panel
//            dateRangePicker.click();
//
//            // Wait for the calendar dropdown panel to become visible
//            WebElement datePickerPanel = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.cssSelector(".ant-picker-dropdown:not(.ant-picker-dropdown-hidden)")));
//
//            // Find the "Today" button in the footer of the date picker panel
//            WebElement todayButton = wait.until(ExpectedConditions.elementToBeClickable(
//                    datePickerPanel.findElement(By.className("ant-picker-today-btn"))));
//
//            // Clicking "Today" in a RangePicker selects today's date for both start and end
//            todayButton.click();
//
//            // Wait for the panel to close as confirmation of selection
//            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".ant-picker-dropdown")));
//
//            // Wait for search results to update based on the filter
//            Thread.sleep(2000);
//
//            // Check results after date filtering
//            List<WebElement> cards = driver.findElements(By.cssSelector(".license-card"));
//
//            if (cards.isEmpty()) {
//                WebElement emptyState = driver.findElement(By.cssSelector(".ant-empty"));
//                Assert.assertNotNull(emptyState, "Neither license cards nor empty state found after date filter");
//                System.out.println("⚠️ No licenses found for today's date range");
//            } else {
//                System.out.println("✅ Date filter working - found " + cards.size() + " license(s)");
//            }
//
//        } catch (Exception e) {
//            System.err.println("❌ Date filter test failed: " + e.getMessage());
//            throw e;
//        }
//    }

    @Test(priority = 5, dependsOnMethods = "navigateToLicensesPage")
    public void testCardContentsAndHistoryButton() throws InterruptedException {
        try {
            // Refresh the page to reset filters
            driver.navigate().refresh();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("container")));

            // Wait for license cards to load
            List<WebElement> cards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".license-card")));

            Assert.assertFalse(cards.isEmpty(), "No license cards found on the page");

            WebElement firstCard = cards.get(0);
            String cardText = firstCard.getText();

            // Verify card contents
            Assert.assertTrue(cardText.contains("License Number"), "❌ License Number missing from card");
            Assert.assertTrue(cardText.contains("Owner"), "❌ Owner missing from card");
            Assert.assertTrue(cardText.contains("Location"), "❌ Location missing from card");

            System.out.println("✅ Card contents verified");

            // Find and click history button
            WebElement historyBtn = firstCard.findElement(
                    By.xpath(".//button[contains(., 'History')]"));

            // Scroll to button and click using JavaScript for reliability
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", historyBtn);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", historyBtn);

            // Wait for navigation to history page
            wait.until(ExpectedConditions.urlContains("/mlowner/history"));
            Assert.assertTrue(driver.getCurrentUrl().contains("licenseNumber"),
                    "URL should contain licenseNumber parameter");

            System.out.println("✅ History button navigation successful");

        } catch (Exception e) {
            System.err.println("❌ Card contents and history button test failed: " + e.getMessage());
            throw e;
        }
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            try {
                Thread.sleep(2000); // Brief pause before closing
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            driver.quit();
            System.out.println("✅ Browser closed successfully");
        }
    }
}