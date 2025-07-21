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

public class RejectedLicensesTableTest extends AppointmentsTestBase {

    RejectedLicensesTablePage page;

    @BeforeClass
    public void setUpRejectedTab() {
        page = new RejectedLicensesTablePage(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By RejectedTab = By.xpath("//div[contains(@class,'ant-tabs-tab') and (contains(., 'Rejected Licenses') or contains(., 'அங்கீகரிக்கப்பட்ட உரிமங்கள்'))]");
        WebElement tabElement = wait.until(ExpectedConditions.elementToBeClickable(RejectedTab));
        tabElement.click();

        // Optional: wait for the active tab content
        By rejectedContent = By.xpath("//div[contains(@class,'ant-tabs-tabpane-active')]//div[contains(@class,'ant-table-wrapper')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(rejectedContent));
    }

    @Test(priority = 1)
    public void verifyRejectedAppointmentsExist() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector(".ant-tabs-tabpane-active .ant-table-tbody > tr")
        ));
        assertTrue(rows.size() > 0, "Rejected Licenses should be visible.");
    }

    @Test(priority = 2)
    public void verifyRejectedLicensesExist() {
        List<WebElement> rows = driver.findElements(By.cssSelector(".ant-tabs-tabpane-active .ant-table-tbody > tr"));
        assertTrue(rows.size() > 0, "Rejected licenses table should have at least one row.");
    }

    @Test(priority = 3)
    public void verifyGoogleMapsLink() {
        WebElement mapLink = driver.findElement(By.cssSelector(".ant-tabs-tabpane-active .ant-table-tbody > tr td a[href*='https://www.google.com/maps/search']"));
        String href = mapLink.getAttribute("href");
        assertTrue(href.contains("https://www.google.com/maps/search"), "Location link should point to Google Maps.");
        assertTrue(mapLink.isDisplayed(), "Map link should be visible.");
    }

    @Test(priority = 4)
    public void testViewDetailsButtonWorks() {
        // Navigate to the Rejected Licenses tab if needed
        try {
            WebElement RejectedTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("rc-tabs-1-tab-rejected-licenses")
            ));
            RejectedTab.click();
        } catch (Exception ignored) {}

        // Wait for the table to load and locate the button
        By buttonXPath = By.xpath(
                "//*[@id=\"rc-tabs-0-panel-rejected-licenses\"]/div/div/div/div/div/div/div/div/table/tbody/tr[2]/td[5]/div/div/button"
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