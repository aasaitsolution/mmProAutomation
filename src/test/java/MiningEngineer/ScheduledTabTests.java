package MiningEngineer;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

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
        WebElement viewDetailsButton = driver.findElement(By.xpath("//*[@id=\"rc-tabs-1-panel-scheduled\"]/div/div/div/div[2]/div/div/div/div/div/div/div/div/table/tbody/tr[3]/td[5]/div/div[1]/button/span"));
        viewDetailsButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-modal")));
        Assert.assertTrue(modal.isDisplayed(), "Modal should be visible after clicking 'View Details'.");

        WebElement closeButton = modal.findElement(By.cssSelector("button[aria-label='Close']"));
        closeButton.click();
    }

/*    private WebElement getFirstRowActionButton(String buttonText) {
        WebElement firstRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".ant-table-tbody > tr")));
        WebElement actionsCell = firstRow.findElement(By.xpath(".//td[last()]"));
        List<WebElement> buttons = actionsCell.findElements(By.tagName("button"));

        for (WebElement btn : buttons) {
            if (btn.getText().trim().equalsIgnoreCase(buttonText)) {
                return btn;
            }
        }
        throw new NotFoundException(buttonText + " button not found.");
    }
*/
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
            // Open modal by clicking the "Schedule" or similar button
            WebElement scheduleBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[span[contains(text(),'Schedule')]]")));
            scheduleBtn.click();

            // Wait for modal to appear
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-modal-content")));

            // Fill form fields in modal
            WebElement remarksInput = modal.findElement(By.xpath(".//textarea[contains(@id, 'remarks')]"));
            remarksInput.sendKeys("Approved for next phase");

            WebElement datePicker = modal.findElement(By.cssSelector(".ant-picker"));
            datePicker.click();
            WebElement today = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".ant-picker-cell-inner[title='Today']")));
            today.click();

            // Wait for submit button
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[span[text()='Submit']]")));

            // Wait for modal mask to disappear if visible
            List<WebElement> masks = driver.findElements(By.cssSelector(".ant-modal-mask"));
            if (!masks.isEmpty() && masks.get(0).isDisplayed()) {
                wait.until(ExpectedConditions.invisibilityOf(masks.get(0)));
            }

            // Submit the form using JavaScript to avoid overlay issues
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

            // Optionally wait for success message or confirmation
            WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-notification-notice-message")));
            String successText = successToast.getText();
            Assert.assertTrue(successText.toLowerCase().contains("success"), "Form submission failed or not confirmed.");

            System.out.println("Approval form submitted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to fill and submit approval form: " + e.getMessage());
        }
    }
    @Test(priority = 14)
    public void testCloseApproveModalButton() throws InterruptedException {
        // Wait for the close (X) button to be clickable
        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/div[3]/div/div[2]/div/div[1]/div/button/span/span")));
        Assert.assertTrue(closeBtn.isDisplayed(), "Close (X) button should be visible.");

        // Allow for animation to complete
        Thread.sleep(1000);

        // Click the close button
        closeBtn.click();

        // Wait for modal to disappear
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal-content')]")));
    }

    @Test(priority = 15)
    public void testHoldButtonFunctionality() {
        WebElement holdButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table/tbody/tr[2]/td[last()]//button[span[text()='Hold']]")));
        Assert.assertTrue(holdButton.isDisplayed(), "Hold button should be visible.");
        holdButton.click();
    }

    @Test(priority = 16)
    public void testHoldModalVisibleAfterClick() {
        WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//div[text()='Hold Appointment']")));
        Assert.assertTrue(modalTitle.isDisplayed(), "Modal should be visible after clicking Hold.");
    }

    private void takeScreenshot(String filename) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("target/screenshots/" + filename + ".png"));
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

/*
    @Test(priority = 17)
    public void testModalHoldButtonFunctionality() {
        try {
            // Open hold modal using JavaScript click
            WebElement holdBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[.//span[contains(.,'Hold')]]")));
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", holdBtn);

            // Wait for modal with content verification
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'ant-modal')][.//*[contains(.,'Hold Mining License')]]")));

            // Find and click confirm hold button using JavaScript
            WebElement confirmHoldBtn = modal.findElement(
                    By.xpath(".//button[.//span[contains(.,'Hold')]]"));
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", confirmHoldBtn);

            // Verify modal closed with polling
            wait.until(ExpectedConditions.invisibilityOf(modal));

        } catch (Exception e) {
            takeScreenshot("hold_button_failure");
            throw new AssertionError("Hold button test failed: " + e.getMessage(), e);
        }
    }
*/
@Test(priority = 18)
public void testCloseHoldModalButton() throws InterruptedException {
    // Wait for the close (X) button to be clickable
    WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("/html/body/div[2]/div/div[2]/div/div[1]/div/button/span/span")));
    Assert.assertTrue(closeBtn.isDisplayed(), "Close (X) button should be visible.");

    // Allow for animation to complete
    Thread.sleep(1000);

    // Click the close button
    closeBtn.click();

    // Wait for modal to disappear
    wait.until(ExpectedConditions.invisibilityOfElementLocated(
            By.xpath("//div[contains(@class,'ant-modal-content')]")));
}

    @Test(priority = 20)
    public void testRejectButtonFunctionality() {
        WebElement rejectButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table/tbody/tr[2]/td[last()]//button[span[text()='Reject']]")));
        Assert.assertTrue(rejectButton.isDisplayed(), "Reject button should be visible.");
        rejectButton.click();
    }

}
