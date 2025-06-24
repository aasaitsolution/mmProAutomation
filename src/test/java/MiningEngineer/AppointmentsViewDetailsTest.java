package MiningEngineer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AppointmentsViewDetailsTest extends AppointmentsTestBase {

    @Test
    public void testViewDetailsInPendingTab() {
        try {
            // 1. Navigate to Pending Scheduling tab
            switchToPendingTab();

            // 2. Wait for at least one appointment to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//tr[contains(@class, 'ant-table-row')]")));

            // 3. Find and click the first "View Details" button
            clickFirstViewDetailsButton();

            // 4. Verify details modal appears
            verifyDetailsModalVisible();

            // 5. Verify modal content
          //  verifyModalContent();

            // 6. Close the modal
            closeDetailsModal();

        } catch (Exception e) {
            captureScreenshot("view_details_failure");
            System.out.println("Current URL: " + driver.getCurrentUrl());
            System.out.println("Page source snippet: " + driver.getPageSource().substring(0, 500));
            throw new RuntimeException("View Details test failed", e);
        }
    }

    private void switchToPendingTab() {
        // More reliable tab locator using data attributes if available
        By pendingTabLocator = By.xpath(
                "//div[contains(@class, 'ant-tabs-tab') and contains(., 'Pending')] | " +
                        "//div[@data-tab-key='pending']");

        WebElement pendingTab = wait.until(ExpectedConditions.elementToBeClickable(pendingTabLocator));

        // Scroll into view and click using JavaScript
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", pendingTab);
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", pendingTab);

        // Wait for tab to be active - multiple verification strategies
        wait.until(ExpectedConditions.or(
                // Check aria-selected attribute
                ExpectedConditions.attributeContains(pendingTab, "aria-selected", "true"),
                // Check for active tab class
                ExpectedConditions.attributeContains(pendingTab, "class", "ant-tabs-tab-active"),
                // Check if tab content is loaded
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class, 'ant-tabs-tabpane-active')]//table"))
        ));
    }

    private void clickFirstViewDetailsButton() {
        // More flexible button locator
        By viewDetailsButton = By.xpath(
                "(//button[contains(., 'View') and contains(., 'Details')])[1] | " +
                        "(//button[@data-testid='view-details-button'])[1]");

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(viewDetailsButton));
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", button);
    }

    private void verifyDetailsModalVisible() {
        // Check for modal using multiple possible identifiers
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class, 'ant-modal') and contains(@class, 'open')]")),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class, 'appointment-details-modal')]")),
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class, 'ant-modal-title')]"))
        ));
    }
/*
    private void verifyModalContent() {
        // Verify basic fields exist with more flexible locators
        Assert.assertTrue(waitForElement(By.xpath(
                        "//div[contains(text(), 'Mining License') or contains(text(), 'License Number')]")),
                "License number should be displayed");
        Assert.assertTrue(waitForElement(By.xpath(
                        "//div[contains(text(), 'Applicant') or contains(text(), 'Name')]")),
                "Applicant name should be displayed");
        Assert.assertTrue(waitForElement(By.xpath(
                        "//div[contains(text(), 'Location')]")),
                "Location should be displayed");

        // Verify status is pending
        Assert.assertTrue(waitForElement(By.xpath(
                        "//span[contains(text(), 'Pending') or contains(text(), 'නියමිත') or contains(text(), 'நிலுவையிலுள்ள')]")),
                "Status should be Pending");
    }
*/
private void closeDetailsModal() {
    // Try these alternative locators
    By closeButton = By.xpath(
            "//button[contains(@class, 'ant-modal-close')] | " +
                    "//button[contains(@aria-label, 'Close')] | " +
                    "//button[contains(@class, 'ant-btn') and contains(., 'Close')]");

    // Add explicit wait for button to be clickable
    WebElement button = wait.until(ExpectedConditions.elementToBeClickable(closeButton));

    // Click using JavaScript as a fallback
    try {
        button.click();
    } catch (Exception e) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", button);
    }

    // Alternative wait condition for modal disappearance
    wait.until(ExpectedConditions.invisibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'ant-modal-root') and contains(@class, 'ant-modal-wrap')]")));
}

    private boolean waitForElement(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)) != null;
        } catch (Exception e) {
            return false;
        }
    }
}