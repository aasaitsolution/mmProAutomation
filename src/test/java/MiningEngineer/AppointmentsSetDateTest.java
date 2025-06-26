package MiningEngineer;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
            } catch (Exception ignored) {}
        } catch (Exception e) {
            captureScreenshot("navigation_failure");
            throw new RuntimeException("Failed to navigate to appointments: " + e.getMessage(), e);
        }
    }

    @Test(priority = 1)
    public void testSetDateButtonVisibility() {
        try {
            WebElement setDateButton = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//button[contains(@class, 'ant-btn') and .//text()[normalize-space()='Set Date' or normalize-space()='දිනය සකසන්න' or normalize-space()='திகதி அமைக்கவும்']]")
            ));
            assert setDateButton.isDisplayed() : "Set Date button is not visible";
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
            input.click();

            WebElement datePanel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-picker-panel")
            ));

            assert datePanel.isDisplayed() : "Date picker panel did not appear";
        } catch (Exception e) {
            captureScreenshot("popover_failure");
            throw e;
        }
    }

    @Test(priority = 3)
    public void testDateSelection() {
        // Get and format tomorrow's date
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String formattedDate = tomorrow.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        // Click the set date button
        WebElement setDateButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'ant-btn') and " +
                        "(contains(., 'Set Date') or contains(., 'දිනය සකසන්න') or contains(., 'திகதி அமைக்கவும்'))]")));
        setDateButton.click();

        // Wait for and click date picker input
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".ant-picker-input input")));
        input.click();

        // Wait for calendar to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ant-picker-dropdown, .ant-picker-panel")));

        // Select the date
        WebElement dateCell = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(String.format("//td[@title='%s']", formattedDate))));
        dateCell.click();

        // Verify selection
        String selectedDate = input.getAttribute("value");
        assertEquals(selectedDate, formattedDate, "Selected date not shown in input field");
    }

    @Test(priority = 4, dependsOnMethods = "testDateSelection")
    public void testSetAppointmentDate() {
        // Switch to pending tab
        driver.findElement(By.xpath("//div[text()='Pending']")).click();

        // Click on "Set Date" button
        WebElement setDateButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Set Date')]")));
        setDateButton.click();

        // Pick today's date
        WebElement today = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".ant-picker-cell-inner"))); // Selects the first visible date
        today.click();

        // Click Confirm button in the popover
        WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Confirm')]")));
        confirmButton.click();

        // Expect success message
        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("ant-message-success")));
        Assert.assertTrue(message.getText().contains("scheduled"));
    }


    @Test(priority = 5)
    public void testPastDateDisabled() {
        try {
            WebElement setDateButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class, 'ant-btn') and .//text()[contains(., 'Confirm') or contains(., 'තහවුරු කරන්න') or contains(., 'உறுதிப்படுத்தவும்')]]")
            ));
            setDateButton.click();

            WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-popover .ant-picker-input input")
            ));
            input.click();

            LocalDate yesterday = LocalDate.now().minusDays(1);
            String formattedYesterday = yesterday.format(DateTimeFormatter.ofPattern(DATE_FORMAT));

            WebElement pastDateCell = driver.findElement(
                    By.xpath(String.format("//td[@title='%s']", formattedYesterday))
            );

            assert pastDateCell.getAttribute("class").contains("ant-picker-cell-disabled") :
                    "Past date should be disabled but is clickable";
        } catch (Exception e) {
            captureScreenshot("past_date_test_failure");
            throw e;
        }
    }

    @AfterMethod
    public void closePopover() {
        try {
            WebElement body = driver.findElement(By.tagName("body"));
            body.click(); // Click somewhere outside to dismiss popover
        } catch (Exception ignored) {}
    }
}
