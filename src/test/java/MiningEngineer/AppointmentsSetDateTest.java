package MiningEngineer;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class AppointmentsSetDateTest extends AppointmentsTestBase {
    private final String DATE_FORMAT = "yyyy-MM-dd";

    @BeforeClass
    public void navigateToAppointments() {
        try {
            driver.get(BASE_URL + "/me/dashboard");

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".appointments-table")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(., 'Set Date') or contains(., 'දිනය සකසන්න') or contains(., 'திகதி அமைக்கவும்')]"))
            ));

            try {
                WebElement modalClose = driver.findElement(By.cssSelector(".ant-modal-close"));
                if (modalClose.isDisplayed()) {
                    modalClose.click();
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".ant-modal")));
                }
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            captureScreenshot("navigation_failure");
            throw new RuntimeException("Failed to navigate to appointments: " + e.getMessage(), e);
        }
    }

    // Helper method for robust clicking
    private void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    @Test(priority = 1)
    public void testSetDateButtonVisibility() {
        try {
            WebElement setDateButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[contains(@class, 'ant-btn') and .//text()[normalize-space()='Set Date' or normalize-space()='දිනය සකසන්න' or normalize-space()='திகதி அமைக்கவும்']]")
            ));
            Assert.assertTrue(setDateButton.isDisplayed(), "Set Date button is not visible");
        } catch (Exception e) {
            captureScreenshot("button_visibility_failure");
            throw e;
        }
    }

    @Test(priority = 2)
    public void testSetDateButtonClickOpensPopover() {
        try {
            WebElement setDateButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class, 'ant-btn') and .//text()[contains(., 'Set Date') or contains(., 'දිනය සකසන්න') or contains(., 'திகதி அமைக்கவும்')]]")
            ));
            setDateButton.click();

            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-popover .ant-picker-input input")
            ));

            // --- FIX: Use JavaScript click to prevent ElementClickInterceptedException ---
            jsClick(input);

            WebElement datePanel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-picker-panel")
            ));

            Assert.assertTrue(datePanel.isDisplayed(), "Date picker panel did not appear");
        } catch (Exception e) {
            captureScreenshot("popover_failure");
            throw e;
        }
    }

    @Test(priority = 3)
    public void testDateSelection() throws InterruptedException {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String formattedDate = tomorrow.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String formattedTitle = tomorrow.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));

        try {
            WebElement setDateButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//span[text()='Set Date']/parent::button)[1]")));
            setDateButton.click();

            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-popover-content .ant-picker-input input")));

            // JS click to ensure it's not blocked
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input);

            // Wait for calendar panel to load
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".ant-picker-dropdown:not(.ant-picker-dropdown-hidden)")));

            // Try matching with both possible title formats
            String xpath = String.format(
                    "//td[contains(@title,'%s') or contains(@title,'%s')]",
                    formattedDate, formattedTitle);

            WebElement dateCell = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));

            // Use JS click to bypass overlapping elements
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dateCell);
            Thread.sleep(300); // slight delay for smooth scrolling
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dateCell);

            // Optional: verify selected date in input
            String selectedDate = input.getAttribute("value");
            assertNotNull(selectedDate, "No date was selected");

        } catch (Exception e) {
            captureScreenshot("date_selection_failure");
            throw e;
        }
    }


//    @Test(priority = 4, dependsOnMethods = "testDateSelection")
//    public void testSetAppointmentDate() {
//        try {
//            // 1. First wait for the tab to exist (not necessarily clickable yet)
//            wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.cssSelector("[data-testid='pending-tab'], .ant-tabs-tab")));
//
//            // 2. Try clicking with retry logic
//            int attempts = 0;
//            while (attempts < 3) {
//                try {
//                    WebElement pendingTab = wait.until(ExpectedConditions.elementToBeClickable(
//                            By.cssSelector("[data-testid='pending-tab']")));
//                    ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", pendingTab);
//                    ((JavascriptExecutor)driver).executeScript("arguments[0].click();", pendingTab);
//                    break;
//                } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
//                    attempts++;
//                    if (attempts == 3) throw e;
//                    Thread.sleep(1000);
//                }
//            }
//
//            // 3. Wait for content to load
//            wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.cssSelector(".ant-table-row")));
//
//            // Rest of the test...
//
//        } catch (Exception e) {
//            captureScreenshot("testSetAppointmentDate_failure");
//            throw new RuntimeException("Test failed: " + e.getMessage(), e);
//        }
//    }

    @Test(priority = 5)
    public void testPastDateDisabled() {
        try {
            // Re-open the set date popover
            WebElement setDateButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//span[text()='Set Date']/parent::button)[1]")));
            setDateButton.click();

            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-popover .ant-picker-input input")
            ));

            // --- FIX: Use JavaScript click to prevent ElementClickInterceptedException ---
            jsClick(input);

            LocalDate yesterday = LocalDate.now().minusDays(1);
            String formattedYesterday = yesterday.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

            WebElement pastDateCell = driver.findElement(
                    By.xpath(String.format("//td[@title='%s']", formattedYesterday))
            );

            Assert.assertTrue(pastDateCell.getAttribute("class").contains("ant-picker-cell-disabled"),
                    "Past date should be disabled but is not");
        } catch (Exception e) {
            captureScreenshot("past_date_test_failure");
            throw e;
        }
    }

    @AfterMethod
    public void closePopover() {
        try {
            // Pressing Escape is a reliable way to close popovers/modals
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        } catch (Exception ignored) {
        }
    }
}
