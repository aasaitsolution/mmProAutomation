package GSMBOfficer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import java.time.Duration;
import java.util.List;

public class GSMBmlTab {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        // Set up Chrome options for better stability
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // Create driver with options
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Create wait instance
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    public void gsmbSignin() {
        try {
            // Navigate to the application
            driver.get("http://localhost:5173/signin");
            System.out.println("Navigated to signin page");

            // Wait for page to load completely
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            // Sign in process
            performSignIn();

            // Wait for dashboard to load
            waitForDashboardLoad();

            // Click ML Owner tab
            clickMLOwnerTab();

            // Handle the plus button and add button
            handleMLOwnerActions();

            System.out.println("Test completed successfully!");

        } catch (Exception e) {
            System.out.println("Test failed with error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Test execution failed", e);
        }
    }

    private void performSignIn() {
        try {
            // Wait for and fill username
            WebElement usernameField = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
            usernameField.clear();
            usernameField.sendKeys("nimal");
            System.out.println("Username entered");

            // Wait for and fill password
            WebElement passwordField = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
            passwordField.clear();
            passwordField.sendKeys("12345678");
            System.out.println("Password entered");

            // Click sign in button
            WebElement signInButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector(".ant-btn-primary")));
            signInButton.click();
            System.out.println("Sign in button clicked");

        } catch (Exception e) {
            throw new RuntimeException("Sign in failed: " + e.getMessage(), e);
        }
    }

    private void waitForDashboardLoad() {
        try {
            // Wait for URL to change (indicating successful login)
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("signin")));

            // Wait for dashboard elements to be present
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));

            // Additional wait for dynamic content
            Thread.sleep(3000);
            System.out.println("Dashboard loaded successfully");

        } catch (Exception e) {
            throw new RuntimeException("Dashboard loading failed: " + e.getMessage(), e);
        }
    }

    private void clickMLOwnerTab() {
        System.out.println("Attempting to click ML Owner tab...");

        // List of strategies to find and click the ML Owner tab
        String[] tabSelectors = {
                "//*[@id='rc-tabs-1-tab-MLOWNER']",
                "//div[contains(@class, 'ant-tabs-tab') and contains(text(), 'ML Owner')]",
                "//div[contains(text(), 'ML Owner')]",
                "//*[contains(@id, 'MLOWNER')]",
                "//span[contains(text(), 'ML Owner')]/parent::div",
                "//div[@role='tab' and contains(text(), 'ML Owner')]"
        };

        boolean tabClicked = false;

        for (int i = 0; i < tabSelectors.length && !tabClicked; i++) {
            try {
                WebElement mlOwnerTab = wait.until(
                        ExpectedConditions.elementToBeClickable(By.xpath(tabSelectors[i])));

                // Try regular click first
                try {
                    mlOwnerTab.click();
                    tabClicked = true;
                    System.out.println("ML Owner tab clicked using selector " + (i + 1));
                } catch (Exception clickException) {
                    // If regular click fails, try JavaScript click
                    JavascriptExecutor executor = (JavascriptExecutor) driver;
                    executor.executeScript("arguments[0].click();", mlOwnerTab);
                    tabClicked = true;
                    System.out.println("ML Owner tab clicked using JavaScript with selector " + (i + 1));
                }

            } catch (Exception e) {
                System.out.println("Selector " + (i + 1) + " failed: " + e.getMessage());
                continue;
            }
        }

        if (!tabClicked) {
            // Final attempt: find all tabs and click by text
            try {
                List<WebElement> tabs = driver.findElements(By.xpath("//div[contains(@class, 'ant-tabs-tab')]"));
                for (WebElement tab : tabs) {
                    if (tab.getText().contains("ML Owner")) {
                        JavascriptExecutor executor = (JavascriptExecutor) driver;
                        executor.executeScript("arguments[0].click();", tab);
                        tabClicked = true;
                        System.out.println("ML Owner tab found and clicked from tab list");
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Final tab search failed: " + e.getMessage());
            }
        }

        if (!tabClicked) {
            throw new RuntimeException("Unable to click ML Owner tab after all attempts");
        }

        // Wait for tab content to load
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void handleMLOwnerActions() {
        try {
            // Wait for table to be present
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("table")));
            System.out.println("Table found on ML Owner tab");

            // Try to find and click the plus button (expand row)
            boolean plusButtonClicked = false;
            String[] plusButtonSelectors = {
                    "//button[contains(@class, 'ant-btn') and contains(., '+')]",
                    "//button[contains(@aria-label, 'plus')]",
                    "//button[contains(@title, 'Expand')]",
                    "//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div/div/table/tbody/tr[1]/td[1]/button",
                    "//tbody/tr[1]/td[1]/button"
            };

            for (String selector : plusButtonSelectors) {
                try {
                    WebElement plusButton = wait.until(
                            ExpectedConditions.elementToBeClickable(By.xpath(selector)));
                    plusButton.click();
                    plusButtonClicked = true;
                    System.out.println("Plus button clicked using selector: " + selector);
                    break;
                } catch (Exception e) {
                    continue;
                }
            }

            if (plusButtonClicked) {
                // Wait for row expansion
                Thread.sleep(500);
            }

            // Now try to find and click the add button
            boolean addButtonClicked = false;
            String[] addButtonSelectors = {
                    "//button[contains(@class, 'ant-btn') and contains(., '+')]",
                    "//a/button[contains(@class, 'ant-btn')]",
                    "//*[@id=\"root\"]/div/main/div/div[4]/div/div/div/div/div/div/div/table/tbody/tr[1]/td[8]/a/button",
                    "//tbody/tr[1]/td[8]//button",
                    "//tbody/tr[1]/td[last()]//button"
            };

            for (String selector : addButtonSelectors) {
                try {
                    WebElement addButton = wait.until(
                            ExpectedConditions.elementToBeClickable(By.xpath(selector)));
                    addButton.click();
                    addButtonClicked = true;
                    System.out.println("Add button clicked using selector: " + selector);
                    break;
                } catch (Exception e) {
                    continue;
                }
            }

            if (!addButtonClicked) {
                System.out.println("Warning: Add button not found or not clickable");
            }

        } catch (Exception e) {
            throw new RuntimeException("ML Owner actions failed: " + e.getMessage(), e);
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            try {
                // Wait to observe results
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            driver.quit();
            System.out.println("Browser closed");
        }
    }
}