package MiningEngineer;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class ApprovedLicensesTableTest extends AppointmentsTestBase {

    ApprovedLicensesTablePage page;

    @BeforeClass
    public void setUpApprovedTab() {
        page = new ApprovedLicensesTablePage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By approvedTab = By.xpath("//div[contains(@class,'ant-tabs-tab') and (contains(., 'Approved Licenses') or contains(., 'அங்கீகரிக்கப்பட்ட உரிமங்கள்'))]");
        WebElement tabElement = wait.until(ExpectedConditions.elementToBeClickable(approvedTab));
        tabElement.click();

        // Optional: wait for the active tab content
        By approvedContent = By.xpath("//div[contains(@class,'ant-tabs-tabpane-active')]//div[contains(@class,'ant-table-wrapper')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(approvedContent));
    }

    @Test(priority = 1)
    public void verifyApprovedAppointmentsExist() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector(".ant-tabs-tabpane-active .ant-table-tbody > tr")
        ));
        assertTrue(rows.size() > 0, "Approved Licenses should be visible.");
    }

    @Test(priority = 2)
    public void verifyApprovedLicensesExist() {
        List<WebElement> rows = driver.findElements(By.cssSelector(".ant-tabs-tabpane-active .ant-table-tbody > tr"));
        assertTrue(rows.size() > 0, "Approved licenses table should have at least one row.");
    }

    @Test(priority = 3)
    public void verifyGoogleMapsLink() {
        WebElement mapLink = driver.findElement(By.cssSelector(".ant-tabs-tabpane-active .ant-table-tbody > tr td a[href*='https://www.google.com/maps/search']"));
        String href = mapLink.getAttribute("href");
        assertTrue(href.contains("https://www.google.com/maps/search"), "Location link should point to Google Maps.");
        assertTrue(mapLink.isDisplayed(), "Map link should be visible.");
    }

    /**
     * @Test(priority = 4)
     * public void verifyApprovedDateFormat() {
     * WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
     * <p>
     * WebElement dateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
     * By.cssSelector(".ant-tabs-tabpane-active .ant-table-tbody > tr td:nth-child(4)"))
     * );
     * <p>
     * String dateText = dateElement.getText().trim();
     * System.out.println("Date in UI: '" + dateText + "'");
     * <p>
     * boolean isValid = dateText.matches("\\d{1,2}/\\d{1,2}/\\d{4}") || // e.g., 7/2/2025
     * dateText.matches("\\d{4}-\\d{2}-\\d{2}") ||     // e.g., 2025-07-02
     * dateText.equalsIgnoreCase("N/A");
     * <p>
     * Assert.assertTrue(isValid, "Date should be in a valid format or N/A. Found: '" + dateText + "'");
     * <p>
     * }
     */

    @Test(priority = 5)
    public void testViewDetailsButtonWorks() {
        // Navigate to the Approved Licenses tab if needed
        try {
            WebElement approvedTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("rc-tabs-1-tab-approved-licenses")
            ));
            approvedTab.click();
        } catch (Exception ignored) {}

        // Wait for the table to load and locate the button
        By buttonXPath = By.xpath(
                "//*[@id='rc-tabs-1-panel-approved-licenses']/div/div/div/div/div/div/div/div/div/table/tbody/tr[3]/td[6]/div/div/button"
        );

        WebElement viewDetailsBtn = wait.until(ExpectedConditions.elementToBeClickable(buttonXPath));

        // Scroll into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", viewDetailsBtn);

        // Click the button
        viewDetailsBtn.click();

        // Add verification logic - for example, check for modal, page change, or details section visibility
        WebElement detailsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("ant-modal-title") // Example: adjust this locator to suit your actual details view
        ));

        Assert.assertTrue(detailsSection.isDisplayed(), "Details section should be visible after clicking 'View Details'.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}