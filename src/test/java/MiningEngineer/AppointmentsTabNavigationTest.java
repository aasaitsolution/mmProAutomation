package MiningEngineer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

public class AppointmentsTabNavigationTest extends AppointmentsTestBase {

    @Test
    public void testSwitchToPendingTab() {
        switchAndVerifyTab(
                "//div[@data-tab-key='pending-scheduling']",
                "//h3[contains(., 'Pending Scheduling Appointments')]",
                "pending_tab"
        );
    }


    @Test
    public void testSwitchToScheduledTab() {
        switchAndVerifyTab(
                "//div[contains(@class, 'ant-tabs-tab') and contains(., 'Scheduled')]",
                "//h3[contains(., 'Scheduled Appointments')]",
                "scheduled_tab"
        );
    }

    @Test
    public void testSwitchToHoldTab() {
        switchAndVerifyTab(
                "//span[@class='tab-title' and normalize-space(text())='Hold Licenses']/parent::div",
                "//h3[contains(., 'On Hold Appointments')]",
                "hold_tab"
        );
    }


    private void switchAndVerifyTab(String tabLocator, String contentLocator, String screenshotPrefix) {
        try {
            // Debug: Print all visible tabs
            List<WebElement> allTabs = driver.findElements(By.cssSelector(".ant-tabs-tab"));
            System.out.println("Available tabs:");
            allTabs.forEach(tab -> System.out.println("- " + tab.getText()));

            // Find and click the tab
            WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(tabLocator)));
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", tab);
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", tab);

            // Wait for content to load
            WebElement content = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(contentLocator)));

            // Additional verification
            WebElement activeTab = driver.findElement(By.cssSelector(".ant-tabs-tab-active"));
            Assert.assertTrue(activeTab.getText().contains(tab.getText()),
                    "Tab '" + tab.getText() + "' is not active");

            Assert.assertTrue(content.isDisplayed(), "Tab content not displayed");

        } catch (Exception e) {
            captureScreenshot(screenshotPrefix + "_failure");

            // Print current page state for debugging
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page source snippet: " + driver.getPageSource().substring(0, 1000));

            throw new RuntimeException("Tab test failed: " + e.getMessage(), e);
        }
    }
}