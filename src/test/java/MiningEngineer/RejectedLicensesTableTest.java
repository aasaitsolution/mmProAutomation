package MiningEngineer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class RejectedLicensesTableTest extends AppointmentsTestBase {

    @BeforeClass
    public void setUpRejectedTab() {
        try {
            System.out.println("=== SETUP DEBUG START ===");

            // Wait for initial page load
            Thread.sleep(5000);

            // Capture initial state
            captureScreenshot("01_initial_page_state");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page Title: " + driver.getTitle());

            // Find all tabs
            System.out.println("\n=== FINDING ALL TABS ===");
            List<WebElement> allTabs = driver.findElements(By.cssSelector("*[class*='tab']"));
            System.out.println("Found " + allTabs.size() + " elements with 'tab' in class name");

            for (int i = 0; i < allTabs.size(); i++) {
                WebElement tab = allTabs.get(i);
                if (tab.isDisplayed()) {
                    String text = tab.getText().trim();
                    String className = tab.getAttribute("class");
                    String id = tab.getAttribute("id");
                    String role = tab.getAttribute("role");

                    System.out.println(String.format("Tab %d: text='%s', class='%s', id='%s', role='%s'",
                            i + 1, text, className, id, role));
                }
            }

            // Try clicking on different tabs to see which one might be "Rejected"
            System.out.println("\n=== TRYING TO FIND REJECTED TAB ===");
            WebElement rejectedTab = null;

            // Strategy 1: Look for specific text
            List<WebElement> tabsWithText = driver.findElements(
                    By.xpath("//div[contains(@class,'tab') and (contains(text(), 'Reject') or contains(text(), 'Denied') or contains(text(), 'அங்கீகரிக்கப்'))]")
            );

            if (!tabsWithText.isEmpty()) {
                rejectedTab = tabsWithText.get(0);
                System.out.println("Found tab with rejected-related text: " + rejectedTab.getText());
            } else {
                System.out.println("No tabs found with rejected-related text");

                // Strategy 2: Try clicking each tab to see content
                List<WebElement> clickableTabs = driver.findElements(By.cssSelector(".ant-tabs-tab, [role='tab']"));
                System.out.println("Found " + clickableTabs.size() + " clickable tabs");

                for (int i = 0; i < clickableTabs.size(); i++) {
                    WebElement tab = clickableTabs.get(i);
                    if (tab.isDisplayed()) {
                        try {
                            System.out.println("\nTrying tab " + (i+1) + ": " + tab.getText());
                            tab.click();
                            Thread.sleep(2000);

                            captureScreenshot("02_tab_" + (i+1) + "_clicked");

                            // Check what content appeared
                            List<WebElement> tables = driver.findElements(By.cssSelector("table, .ant-table"));
                            List<WebElement> rows = driver.findElements(By.cssSelector("tr"));

                            System.out.println("After clicking tab " + (i+1) + ":");
                            System.out.println("- Found " + tables.size() + " tables");
                            System.out.println("- Found " + rows.size() + " table rows");

                            // Look for any content that might indicate this is the rejected tab
                            String pageText = driver.findElement(By.tagName("body")).getText().toLowerCase();
                            if (pageText.contains("reject") || pageText.contains("denied") ||
                                    pageText.contains("decline") || pageText.contains("invalid")) {
                                System.out.println("*** This might be the rejected tab! ***");
                                rejectedTab = tab;
                                break;
                            }

                        } catch (Exception e) {
                            System.out.println("Failed to click tab " + (i+1) + ": " + e.getMessage());
                        }
                    }
                }
            }

            if (rejectedTab != null) {
                // Make sure the rejected tab is selected
                try {
                    rejectedTab.click();
                    Thread.sleep(2000);
                    captureScreenshot("03_rejected_tab_final");
                    System.out.println("✅ Successfully selected what appears to be the rejected tab");
                } catch (Exception e) {
                    System.out.println("Error clicking final rejected tab: " + e.getMessage());
                }
            } else {
                System.out.println("❌ Could not identify the rejected licenses tab");
                captureScreenshot("03_no_rejected_tab_found");
            }

            System.out.println("=== SETUP DEBUG END ===\n");

        } catch (Exception e) {
            System.out.println("Setup failed: " + e.getMessage());
            e.printStackTrace();
            captureScreenshot("setup_failed");
        }
    }

    @Test(priority = 1)
    public void debugCurrentPageContent() {
        try {
            System.out.println("\n=== CURRENT PAGE CONTENT DEBUG ===");
            captureScreenshot("04_current_page_for_testing");

            // Get all visible text content
            WebElement body = driver.findElement(By.tagName("body"));
            String pageText = body.getText();
            System.out.println("Page contains " + pageText.length() + " characters of text");

            // Check for tables
            List<WebElement> allTables = driver.findElements(By.cssSelector("table, .ant-table, [class*='table']"));
            System.out.println("Found " + allTables.size() + " table-like elements");

            for (int i = 0; i < allTables.size(); i++) {
                WebElement table = allTables.get(i);
                if (table.isDisplayed()) {
                    System.out.println("Table " + (i+1) + " is visible");

                    List<WebElement> rows = table.findElements(By.tagName("tr"));
                    System.out.println("- Has " + rows.size() + " rows");

                    if (rows.size() > 0) {
                        String firstRowText = rows.get(0).getText();
                        System.out.println("- First row text: " + firstRowText.substring(0, Math.min(100, firstRowText.length())));
                    }
                } else {
                    System.out.println("Table " + (i+1) + " is NOT visible");
                }
            }

            // Check for links
            List<WebElement> allLinks = driver.findElements(By.tagName("a"));
            int visibleLinks = 0;
            int mapsLinks = 0;

            for (WebElement link : allLinks) {
                if (link.isDisplayed()) {
                    visibleLinks++;
                    String href = link.getAttribute("href");
                    if (href != null && (href.contains("maps.google") || href.contains("google.com/maps"))) {
                        mapsLinks++;
                        System.out.println("Found maps link: " + href);
                    }
                }
            }

            System.out.println("Found " + visibleLinks + " visible links");
            System.out.println("Found " + mapsLinks + " Google Maps links");

            // Check for buttons
            List<WebElement> allButtons = driver.findElements(By.tagName("button"));
            int visibleButtons = 0;

            for (WebElement button : allButtons) {
                if (button.isDisplayed()) {
                    visibleButtons++;
                    if (visibleButtons <= 5) { // Show first 5 buttons
                        System.out.println("Button " + visibleButtons + ": '" + button.getText() + "'");
                    }
                }
            }

            System.out.println("Found " + visibleButtons + " visible buttons total");

            // Check for any empty state messages
            List<WebElement> emptyMessages = driver.findElements(By.cssSelector(
                    ".ant-empty, .ant-table-placeholder, [class*='empty'], [class*='no-data'], [class*='no-result']"
            ));

            for (WebElement msg : emptyMessages) {
                if (msg.isDisplayed()) {
                    System.out.println("Empty state message: " + msg.getText());
                }
            }

            System.out.println("=== END CURRENT PAGE CONTENT DEBUG ===\n");

        } catch (Exception e) {
            System.out.println("Debug failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 2)
    public void testBasicInteraction() {
        try {
            System.out.println("\n=== BASIC INTERACTION TEST ===");

            // Try to find any clickable element and interact with it
            List<WebElement> clickableElements = driver.findElements(
                    By.cssSelector("button:not([disabled]), a[href], input[type='button'], .ant-btn")
            );

            System.out.println("Found " + clickableElements.size() + " potentially clickable elements");

            for (int i = 0; i < Math.min(3, clickableElements.size()); i++) {
                WebElement element = clickableElements.get(i);
                if (element.isDisplayed()) {
                    try {
                        System.out.println("Trying to click element " + (i+1) + ": " + element.getTagName() + " - " + element.getText());
                        element.click();
                        Thread.sleep(1000);
                        captureScreenshot("05_after_click_" + (i+1));

                        // Check if anything changed
                        List<WebElement> modals = driver.findElements(By.cssSelector(".ant-modal, .ant-drawer, [class*='modal'], [class*='drawer']"));
                        for (WebElement modal : modals) {
                            if (modal.isDisplayed()) {
                                System.out.println("Modal/Drawer appeared after click!");
                                break;
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("Click failed: " + e.getMessage());
                    }
                }
            }

            System.out.println("=== END BASIC INTERACTION TEST ===\n");

        } catch (Exception e) {
            System.out.println("Interaction test failed: " + e.getMessage());
        }
    }
}