//Done
package MiningEngineer;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static org.testng.Assert.fail;

public class ScheduledTabTests extends AppointmentsTestBase {

    @BeforeClass
    public void setUpScheduledTab() {
        // Wait for and click on the "Scheduled" tab
        By scheduledTab = By.xpath("//div[contains(@class,'ant-tabs-tab') and normalize-space()='Scheduled']");

        WebElement tabElement = wait.until(ExpectedConditions.elementToBeClickable(scheduledTab));
        tabElement.click();

        // Optional: Wait for tab content to appear
        By scheduledContent = By.xpath("//div[contains(@class,'ant-tabs-tabpane') and contains(@class,'active')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(scheduledContent));
    }

    @Test(priority = 1)
    public void verifyScheduledAppointmentsExist() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector(".appointments-table .ant-table-tbody > tr")
        ));
        Assert.assertTrue(rows.size() > 0, "Scheduled appointments should be visible.");
    }

    @Test(priority = 2)
    public void verifyScheduledDateColumnExists() {
        WebElement header = driver.findElement(By.xpath("//th[contains(text(), 'Scheduled Date')]"));
        Assert.assertTrue(header.isDisplayed(), "Scheduled Date column should be displayed.");
    }

    @Test(priority = 3)
    public void verifyViewOnMapLinkWorks() {
        WebElement link = driver.findElement(By.cssSelector("a[href*='https://www.google.com/maps']"));
        String href = link.getAttribute("href");
        Assert.assertTrue(href.contains("https://www.google.com/maps/search/"), "Google Maps link should be valid.");
    }

    @Test(priority = 4)
    public void testViewDetailsButtonOpensModal() {
        try {
            // Wait for and click using the test ID
            WebElement viewDetailsButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[data-testid='view-details-button']")
            ));

            // Scroll and click with JavaScript
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", viewDetailsButton);
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", viewDetailsButton);

            // Wait for modal using the more reliable wrapper approach
            WebElement modalWrapper = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-modal-wrap:not([style*='display: none'])")
            ));

            Assert.assertTrue(modalWrapper.isDisplayed(), "Modal wrapper should be visible");

            // Find the close button within the visible modal
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-modal-wrap:not([style*='display: none']) .ant-modal-close")
            ));

            // Click using JavaScript
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", closeButton);

            // Wait for modal to disappear
            wait.until(ExpectedConditions.invisibilityOf(modalWrapper));

        } catch (Exception e) {
            takeScreenshot("view_details_modal_error");
            fail("Test failed: " + e.getMessage());
        }
    }

    @Test(priority = 5)
    public void testApproveButtonFunctionality() {
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table/tbody/tr[2]/td[last()]//button[span[text()='Approve']]")));
        Assert.assertTrue(approveButton.isDisplayed(), "Approve button should be visible.");
        approveButton.click();
    }

    @Test(priority = 6)
    public void testModalVisibleAfterClick() {
        WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//div[text()='Approve Mining License']")));
        Assert.assertTrue(modalTitle.isDisplayed(), "Modal should be visible after clicking Approve.");
    }

    @Test(priority = 7)
    public void testTotalCapacityInputPresence() {
        WebElement totalCapacity = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Enter total capacity in cubic meters']")));
        Assert.assertTrue(totalCapacity.isDisplayed(), "Total Capacity input should be present.");
    }

    @Test(priority = 8)
    public void testMonthlyMaximumCapacityInputPresence() {
        WebElement monthlyCapacity = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Enter monthly maximum capacity']")));
        Assert.assertTrue(monthlyCapacity.isDisplayed(), "Monthly Maximum Capacity input should be present.");
    }

    @Test(priority = 9)
    public void testStartDateAndExpiryDatePresence() {
        WebElement startDate = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Select date' and ancestor::div[contains(., 'Start Date')]]")));
        WebElement expiryDate = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Select date' and ancestor::div[contains(., 'Expiry Date')]]")));
        Assert.assertTrue(startDate.isDisplayed(), "Start Date input should be present.");
        Assert.assertTrue(expiryDate.isDisplayed(), "Expiry Date input should be present.");
    }

    @Test(priority = 10)
    public void testUploadReportPresence() {
        WebElement uploadBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[text()='Select Files']/ancestor::button")));
        Assert.assertTrue(uploadBtn.isDisplayed(), "Upload button should be visible.");
    }

    @Test(priority = 11)
    public void testCommentsFieldPresence() {
        WebElement commentsField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//textarea[@placeholder='Enter additional comments']")));
        Assert.assertTrue(commentsField.isDisplayed(), "Comments textarea should be visible.");
    }

    @Test(priority = 12)
    public void testSubmitButtonFunctionality() {
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='Submit']]")));
        Assert.assertTrue(submitBtn.isDisplayed(), "Submit button should be present.");
        // You may submit with valid/empty data depending on validation testing
        submitBtn.click();
    }

  @Test(priority = 13)
public void testFillAndSubmitApprovalForm() {
    try {
        // Log browser warnings (optional)
        driver.manage().logs().get("browser").forEach(
            log -> System.out.println("BROWSER LOG: " + log));

        // 1. Click "Approve" button
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//span[text()='Approve']]")));
        approveButton.click();

        // Wait for modal and form visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-modal-body form")));

        // Fill Total Capacity
        WebElement totalCapacityInput = driver.findElement(By.id("totalCapacity"));
        totalCapacityInput.clear();
        totalCapacityInput.sendKeys("1000");

        // Fill Monthly Maximum Capacity
        WebElement monthlyCapacityInput = driver.findElement(By.id("monthlyCapacity"));
        monthlyCapacityInput.clear();
        monthlyCapacityInput.sendKeys("300");

        // Select specific start date
        WebElement startDateInput = driver.findElement(By.id("startDate"));
startDateInput.click();
selectDate(driver, wait, "06/08/2025", "startDate");

        Thread.sleep(1000);


        // // Select specific due date
        // // 1. Click expiry date input to open date picker
        // WebElement expiryDateInput = wait.until(ExpectedConditions.elementToBeClickable( By.xpath("//input[@id='dueDate']")));
        // expiryDateInput.click();

        // 2. Select expiry date (e.g., 10 August 2025)
        // You need to reuse the same calendar navigation and click logic you used for the start date

        
        
        String testFilePath = System.getProperty("user.dir") + "/test-files/receipt.pdf";
        createDummyFileIfNotExists(testFilePath);

        // Upload file
        WebElement uploadInput = driver.findElement(By.id("attachments"));
        uploadInput.sendKeys(testFilePath);

        // Fill Comments (optional)
        WebElement commentsInput = driver.findElement(By.id("comments"));
        commentsInput.clear();
        commentsInput.sendKeys("Capacities set and report uploaded.");

        WebElement dueDateInput = driver.findElement(By.id("dueDate"));
        dueDateInput.click();
        selectDate(driver, wait, "10/08/2025", "dueDate");

        // Click Submit button
        WebElement submitBtn = driver.findElement(By.xpath("//button[contains(.,'Submit')]"));
        submitBtn.click();

        // Wait for modal to disappear (form submitted)
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".ant-modal-body")));

        System.out.println("Form submitted successfully.");
    } catch (Exception e) {
        takeScreenshot("approval_form_failure");
        Assert.fail("Failed to fill and submit approval form: " + e.getMessage());
    }
}



    @Test(priority = 14)
    public void testCloseApproveModalButton() {
        try {
            // 1. Wait for any visible modal wrapper (more reliable than content)
            List<WebElement> modals = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".ant-modal-wrap:not([style*='display: none'])")));

            if (modals.isEmpty()) {
                System.out.println("No visible modals found to close");
                return;
            }

            // 2. Take screenshot before closing for debugging
            takeScreenshot("before_modal_close");

            // 3. Find close button within the visible modal
            WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-modal-wrap:not([style*='display: none']) .ant-modal-close")));

            // 4. Scroll to and click with JavaScript (more reliable)
            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", closeBtn);
            Thread.sleep(300); // Small pause for scroll to complete
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", closeBtn);

            // 5. Wait for modal to close with multiple verification methods
            try {
                // Wait for modal to become invisible
                wait.until(ExpectedConditions.invisibilityOf(closeBtn));

                // Additional verification - check modal content is gone
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".ant-modal-wrap:not([style*='display: none']) .ant-modal-content")));

                // Final check - count of visible modals should be 0
                wait.until(driver -> driver.findElements(
                        By.cssSelector(".ant-modal-wrap:not([style*='display: none'])")).size() == 0);

            } catch (TimeoutException e) {
                takeScreenshot("after_failed_modal_close");
                throw new AssertionError("Modal failed to close properly");
            }

        } catch (Exception e) {
            takeScreenshot("modal_close_error");
            // Print browser console logs for debugging
            driver.manage().logs().get("browser").forEach(l -> System.out.println("CONSOLE LOG: " + l));
            throw new AssertionError("Failed to close modal: " + e.getMessage(), e);
        }
    }


    @Test(priority = 15)
    public void testHoldButtonFunctionality() {
        // Wait for any previous modals to close
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".ant-modal-content")));

        WebElement holdButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//tr[contains(@class,'ant-table-row')][1]//button[contains(.,'Hold')]")));
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", holdButton);
    }

    @Test(priority = 16)
    public void testHoldModalVisibleAfterClick() {
        WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//*[contains(.,'Hold Appointment')]")));
        Assert.assertTrue(modalTitle.isDisplayed(), "Modal should be visible after clicking Hold.");
    }


   @Test(priority = 17)
   public void testModalHoldButtonFunctionality() {
       try {
           
        WebElement holdButton = wait.until(ExpectedConditions.elementToBeClickable(
                   By.xpath("//button[.//span[text()='Hold']]")));
                holdButton.click();
        
        // Fill the textarea with reason
        WebElement reasonTextarea = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//textarea[@id='comment']")));
        reasonTextarea.clear();
        reasonTextarea.sendKeys("The appointment needs to be rescheduled due to unforeseen circumstances.");

        // Click the "Confirm Hold" button
        WebElement confirmHoldButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[.//span[text()='Confirm Hold']]")));
        confirmHoldButton.click();
                
        System.out.println("Form submitted successfully.");
       } catch (Exception e) {
           takeScreenshot("hold_button_failure");
           // Print browser console logs
           driver.manage().logs().get("browser").forEach(l -> System.out.println("BROWSER LOG: " + l));
           throw new AssertionError("Hold button test failed: " + e.getMessage(), e);
       }
   }

    @Test(priority = 18)
    public void testCloseHoldModalButton() {
        try {
            // Find all close buttons in visible modals
            List<WebElement> closeButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".ant-modal-wrap:not([style*='display: none']) .ant-modal-close")));

            if (!closeButtons.isEmpty()) {
                // Get the last opened modal's close button (most likely the hold modal)
                WebElement closeBtn = closeButtons.get(closeButtons.size() - 1);

                // Click using JavaScript
                ((JavascriptExecutor)driver).executeScript("arguments[0].click();", closeBtn);

                // Wait for modal to disappear
                wait.until(ExpectedConditions.invisibilityOf(closeBtn));
            } else {
                fail("No modal close buttons found");
            }
        } catch (Exception e) {
            takeScreenshot("close_hold_modal_error");
            fail("Failed to close hold modal: " + e.getMessage());
        }
    }

    @Test(priority = 20)
    public void testRejectButtonFunctionality() {
        try {
            // 1. First ensure all modals are closed
            closeAllModals();

            // 2. Wait for page to settle (optional but recommended)
            Thread.sleep(1000); // Small pause after closing modals

            // 3. Locate the reject button with a more robust selector
            WebElement rejectButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//tr[contains(@class,'ant-table-row')]//button[contains(.,'Reject')]")));

            // 4. Make sure button is truly visible and clickable
            wait.until(ExpectedConditions.visibilityOf(rejectButton));
            wait.until(ExpectedConditions.elementToBeClickable(rejectButton));

            // 5. Scroll into view with proper alignment
            ((JavascriptExecutor)driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center', inline: 'center'});",
                    rejectButton);

            // 6. Use JavaScript click as last resort
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", rejectButton);

            // 7. Verify the reject confirmation modal appears
            WebElement rejectModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'ant-modal')][contains(.,'Reject')]")));
            Assert.assertTrue(rejectModal.isDisplayed(), "Reject confirmation modal should appear");

        } catch (Exception e) {
            takeScreenshot("reject_button_failure");
            fail("Failed to click reject button: " + e.getMessage());
        }
    }

    private void closeAllModals() {
        try {
            // Find all modal close buttons (Ant Design typically uses this class)
            List<WebElement> closeButtons = driver.findElements(
                    By.cssSelector(".ant-modal-close"));

            // Click each close button found
            for (WebElement closeBtn : closeButtons) {
                try {
                    if (closeBtn.isDisplayed()) {
                        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", closeBtn);
                        wait.until(ExpectedConditions.invisibilityOf(closeBtn));
                    }
                } catch (Exception e) {
                    // If one fails, continue with others
                    System.out.println("Could not close one modal: " + e.getMessage());
                }
            }

            // Additional check for any remaining modals
            List<WebElement> modals = driver.findElements(By.cssSelector(".ant-modal-wrap"));
            for (WebElement modal : modals) {
                try {
                    if (modal.isDisplayed()) {
                        // Try to close by clicking outside the modal
                        ((JavascriptExecutor)driver).executeScript(
                                "arguments[0].style.display = 'none';", modal);
                    }
                } catch (Exception e) {
                    System.out.println("Could not hide modal: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error in closeAllModals: " + e.getMessage());
        }
    }

    private void takeScreenshot(String filename) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("target/screenshots/" + filename + ".png"));
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

    private void createDummyFileIfNotExists(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                if (path.endsWith(".pdf")) {
                    java.nio.file.Files.write(file.toPath(), "Dummy PDF content for testing".getBytes());
                } else if (path.endsWith(".jpg") || path.endsWith(".png")) {
                    java.nio.file.Files.write(file.toPath(), "Dummy image content for testing".getBytes());
                } else {
                    java.nio.file.Files.write(file.toPath(), "Dummy file content for testing".getBytes());
                }
                System.out.println("📄 Created dummy file: " + path);
            }
        } catch (Exception e) {
            System.out.println("❌ Could not create dummy file: " + path);
            e.printStackTrace();
        }
    }

  public void selectDate(WebDriver driver, WebDriverWait wait, String dateStr, String inputId) throws InterruptedException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate targetDate = LocalDate.parse(dateStr, formatter);

    WebElement dateInput = driver.findElement(By.id(inputId));

    // Open the date picker explicitly (try multiple clicks if needed)
    boolean calendarOpened = false;
    int attempts = 0;
    while (!calendarOpened && attempts < 3) {
        dateInput.click();
        Thread.sleep(1000);
        List<WebElement> calendars = driver.findElements(By.cssSelector(".ant-picker-panel"));
        for (WebElement cal : calendars) {
            if (cal.isDisplayed()) {
                calendarOpened = true;
                break;
            }
        }
        attempts++;
    }
    if (!calendarOpened) {
        // fallback: JS click
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dateInput);
        Thread.sleep(1000);
        List<WebElement> calendars = driver.findElements(By.cssSelector(".ant-picker-panel"));
        calendarOpened = calendars.stream().anyMatch(WebElement::isDisplayed);
    }
    if (!calendarOpened) {
        throw new RuntimeException("Failed to open calendar popup for input with id: " + inputId);
    }

    // // Wait for calendar panel visible
    // wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-picker-panel")));
    // Thread.sleep(500);

    // (navigate to correct month/year as before...)

    // Select day (pick first visible and enabled matching day)
    String dayString = String.valueOf(targetDate.getDayOfMonth());
    List<WebElement> dayCells = driver.findElements(By.xpath(
        "//td[contains(@class,'ant-picker-cell') and not(contains(@class,'ant-picker-cell-disabled'))]" +
        "[div[contains(@class,'ant-picker-cell-inner') and text()='" + dayString + "']]"));

    WebElement dayCell = null;
    for (WebElement cell : dayCells) {
        if (cell.isDisplayed() && cell.isEnabled()) {
            dayCell = cell;
            break;
        }
    }
    if (dayCell == null) {
        throw new RuntimeException("No enabled day cell found for day: " + dayString);
    }

    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dayCell);
    Thread.sleep(200);
    try {
        dayCell.click();
    } catch (Exception e) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dayCell);
    }
    Thread.sleep(500);
}

}