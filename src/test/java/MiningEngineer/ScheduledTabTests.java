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
import org.apache.commons.io.FileUtils;


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
        System.out.println("✅ Scheduled Tab is ready.");
    }

    @Test(priority = 1)
    public void verifyScheduledAppointmentsExist() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector(".appointments-table .ant-table-tbody > tr")
        ));
        Assert.assertTrue(rows.size() > 0, "Scheduled appointments should be visible.");
        System.out.println("✅ Found " + rows.size() + " scheduled appointments.");

    }

    @Test(priority = 2)
    public void verifyScheduledDateColumnExists() {
        WebElement header = driver.findElement(By.xpath("//th[contains(text(), 'Scheduled Date')]"));
        Assert.assertTrue(header.isDisplayed(), "Scheduled Date column should be displayed.");
        System.out.println("✅ Scheduled Date column is displayed.");
    }

    @Test(priority = 3)
    public void verifyViewOnMapLinkWorks() {
        WebElement link = driver.findElement(By.cssSelector("a[href*='https://www.google.com/maps']"));
        String href = link.getAttribute("href");
        Assert.assertTrue(href.contains("https://www.google.com/maps/search/"), "Google Maps link should be valid.");
        System.out.println("✅ Google Maps link is valid.");
    }

    @Test(priority = 4)
    public void testViewDetailsButtonOpensModal() throws InterruptedException {
        WebElement viewDetailsButton = driver.findElement(By.xpath("//*[@id=\"rc-tabs-1-panel-scheduled\"]/div/div/div/div[2]/div/div/div/div/div/div/div/div/table/tbody/tr[3]/td[5]/div/div[1]/button/span"));
        viewDetailsButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-modal")));
        Assert.assertTrue(modal.isDisplayed(), "Modal should be visible after clicking 'View Details'.");
        System.out.println("✅ View Details modal appeared.");
        Thread.sleep(3000);

        WebElement closeButton = modal.findElement(By.cssSelector("button[aria-label='Close']"));
        closeButton.click();
        System.out.println("✖️ Closed the modal.");
    }

    @Test(priority = 5)
    public void testApproveButtonFunctionality() {
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table/tbody/tr[2]/td[last()]//button[span[text()='Approve']]")));
        Assert.assertTrue(approveButton.isDisplayed(), "Approve button should be visible.");
        approveButton.click();
        System.out.println("✅ Approve button clicked.");
    }

    @Test(priority = 6)
    public void testModalVisibleAfterClick() {
        WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//div[text()='Approve Mining License']")));
        Assert.assertTrue(modalTitle.isDisplayed(), "Modal should be visible after clicking Approve.");
        System.out.println("✅ Approve modal is visible.");
    }

    @Test(priority = 7)
    public void testTotalCapacityInputPresence() {
        WebElement totalCapacity = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Enter total capacity in cubic meters']")));
        Assert.assertTrue(totalCapacity.isDisplayed(), "Total Capacity input should be present.");
         System.out.println("✅ Total Capacity input is present.");
    }

    @Test(priority = 8)
    public void testMonthlyMaximumCapacityInputPresence() {
        WebElement monthlyCapacity = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Enter monthly maximum capacity']")));
        Assert.assertTrue(monthlyCapacity.isDisplayed(), "Monthly Maximum Capacity input should be present.");
        System.out.println("✅ Monthly Maximum Capacity input is present.");
    }

    @Test(priority = 9)
    public void testStartDateAndExpiryDatePresence() {
        WebElement startDate = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Select date' and ancestor::div[contains(., 'Start Date')]]")));
        WebElement expiryDate = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Select date' and ancestor::div[contains(., 'Expiry Date')]]")));
        Assert.assertTrue(startDate.isDisplayed(), "Start Date input should be present.");
        Assert.assertTrue(expiryDate.isDisplayed(), "Expiry Date input should be present.");
        System.out.println("✅ Start Date and Expiry Date inputs are present.");
    }

    @Test(priority = 10)
    public void testUploadReportPresence() {
        WebElement uploadBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[text()='Select Files']/ancestor::button")));
        Assert.assertTrue(uploadBtn.isDisplayed(), "Upload button should be visible.");
        System.out.println("✅ Upload Report button is visible.");
    }

    @Test(priority = 11)
    public void testCommentsFieldPresence() {
        WebElement commentsField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//textarea[@placeholder='Enter additional comments']")));
        Assert.assertTrue(commentsField.isDisplayed(), "Comments textarea should be visible.");
        System.out.println("✅ Comments textarea is visible.");
    }

    @Test(priority = 12)
    public void testSubmitButtonFunctionality() throws InterruptedException {
        // Fill Total Capacity
        WebElement totalCapacity = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Enter total capacity in cubic meters']")));
        totalCapacity.clear();
        totalCapacity.sendKeys("5000");
        System.out.println("📥 Filled Total Capacity");

        // Fill Monthly Maximum Capacity
        WebElement monthlyCapacity = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Enter monthly maximum capacity']")));
        monthlyCapacity.clear();
        monthlyCapacity.sendKeys("500");
        System.out.println("📥 Filled Monthly Capacity");

        // Select Start Date
        selectDateByIdAndTitle("startDate", "2025-06-28");

        // Small delay to ensure old calendar is gone
        Thread.sleep(500);
        System.out.println("📅 Selected Start Date");


        // Select Expiry Date
        selectDateByIdAndTitle("dueDate", "2025-06-30");
        System.out.println("📅 Selected Expiry Date");

        // Upload dummy report file
        createDummyFile("test-files/upload-report.pdf");
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("attachments")));
        fileInput.sendKeys(new File("test-files/upload-report.pdf").getAbsolutePath());
        Thread.sleep(1000); // optional wait for upload to reflect
        System.out.println("📎 Uploaded dummy report file");

        // Fill Comments
        WebElement commentsField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//textarea[@placeholder='Enter additional comments']")));
        commentsField.clear();
        commentsField.sendKeys("Approved based on valid documentation and capacity criteria.");
        System.out.println("💬 Entered Comments");

        // Scroll to Submit button and click
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='Submit']]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
        Assert.assertTrue(submitBtn.isDisplayed(), "Submit button should be present.");
        submitBtn.click();
        System.out.println("🚀 Submitted the form successfully ✅");

    }

    @Test(priority = 13)
    public void testHoldButtonFunctionality() {
        WebElement holdButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table/tbody/tr[2]/td[last()]//button[span[text()='Hold']]")));
        Assert.assertTrue(holdButton.isDisplayed(), "Hold button should be visible.");
        holdButton.click();
        System.out.println("✅ Hold button clicked.");
    }

    @Test(priority = 14)
    public void testHoldModalVisibleAfterClick() {
        // Wait for modal title to be visible
        WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//div[text()='Hold Appointment']")));
        Assert.assertTrue(modalTitle.isDisplayed(), "Modal should be visible after clicking Hold.");
        System.out.println("✅ Hold modal is visible.");

        // Fill in the "Reason for holding"
        WebElement reasonTextarea = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("comment")));
        reasonTextarea.clear();
        reasonTextarea.sendKeys("Test reason for holding this appointment.");
        System.out.println("📝 Entered reason for holding");

        // Click the HOLD button
        WebElement holdConfirmButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-modal-footer')]//button[span[text()='HOLD']]")));
        holdConfirmButton.click();
        System.out.println("✅ Hold confirmed");

    }


    private void takeScreenshot(String filename) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("target/screenshots/" + filename + ".png"));
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
        }
    }

   @Test(priority = 15)
    public void testRejectButtonFunctionality() throws IOException, InterruptedException {
        // Step 1: Click the Reject button in the second row
        WebElement rejectButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table/tbody/tr[2]/td[last()]//button[span[text()='Reject']]")));
        Assert.assertTrue(rejectButton.isDisplayed(), "Reject button should be visible.");
        rejectButton.click();
        System.out.println("✅ Reject button clicked.");

        // Step 2: Wait for modal title
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//div[contains(text(),'Reject Appointment')]")));
        System.out.println("🔍 Reject modal visible.");

        // Step 3: Find textarea by label: Reason for rejection
        WebElement reasonTextArea = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(text(),'Reason for rejection')]/ancestor::div[contains(@class,'ant-form-item')]//textarea")));
        reasonTextArea.sendKeys("Documentation insufficient for approval.");
        System.out.println("📝 Entered reason for rejection.");

        // Step 4: Upload dummy PDF file
        String filePath = "test-files/reject-report.pdf";
        createDummyFile(filePath);  // Creates file only if not already there

        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[type='file'][accept='.pdf']")));
        fileInput.sendKeys(new File(filePath).getAbsolutePath());

        Thread.sleep(1000); // wait for file list to render

        // Step 5: Click Confirm ML Rejection
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='Confirm ML Rejection']]")));
        confirmBtn.click();
        System.out.println("🚀 Confirm ML Rejection clicked ✅");

    }


    private void waitUntilClickable(WebElement element) {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.elementToBeClickable(element));
    }

    private void selectDateByIdAndTitle(String inputId, String dateTitle) {
    // Count how many dropdowns are open now
    int dropdownCountBefore = driver.findElements(By.className("ant-picker-dropdown")).size();

    WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id(inputId)));
    input.click();

    // Wait for a new calendar dropdown to appear
    wait.until(driver -> driver.findElements(By.className("ant-picker-dropdown")).size() > dropdownCountBefore);

    // Get the most recent dropdown (the last one)
    List<WebElement> allDropdowns = driver.findElements(By.className("ant-picker-dropdown"));
    WebElement activePopup = allDropdowns.get(allDropdowns.size() - 1);

    // Click the date
    WebElement dateCell = activePopup.findElement(By.xpath(".//td[@title='" + dateTitle + "']"));
    wait.until(ExpectedConditions.elementToBeClickable(dateCell));
    dateCell.click();

    // Wait for that popup to disappear
    wait.until(ExpectedConditions.invisibilityOf(activePopup));
}

private void createDummyFile(String path) {
    try {
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs(); // Ensure directory exists
            FileUtils.writeStringToFile(file, "This is a dummy PDF file for testing upload.", "UTF-8");
        }
    } catch (IOException e) {
        throw new RuntimeException("Failed to create dummy file: " + path, e);
    }
}





}
