package MiningEngineer;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class HoldModalScheduled extends AppointmentsTestBase {

    private void openHoldModal() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Click "Scheduled" tab
        // Wait for any previous modal to close
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".ant-modal-wrap")));

        // Now safely click the "Scheduled" tab
        WebElement scheduledTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'ant-tabs-tab') and normalize-space()='Scheduled']")));
        scheduledTab.click();


        // Wait for the Scheduled tab content to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ant-tabs-tabpane-active .appointments-table")));

        // Add explicit wait for table data to load
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if table has data
        List<WebElement> tableRows = driver.findElements(By.xpath("//table/tbody/tr"));
        if (tableRows.isEmpty()) {
            System.out.println("No data found in the Scheduled tab table");
            throw new RuntimeException("No data found in the Scheduled tab table");
        }

        // Try to find and click the Hold button
        WebElement holdBtn = null;
        try {
            // First try - exact match
            holdBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//table/tbody/tr//button[span[text()='Hold']]")));
        } catch (TimeoutException e) {
            System.out.println("Hold button not found with exact match, trying alternatives...");

            // Print available buttons for debugging
            List<WebElement> buttons = driver.findElements(By.xpath("//table/tbody/tr//button"));
            System.out.println("Available buttons in table: " + buttons.size());
            for (WebElement btn : buttons) {
                System.out.println("Button text: '" + btn.getText() + "'");
            }

            // Try alternative selectors
            try {
                holdBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//table/tbody/tr//button[contains(text(),'Confirm Hold')]")));
            } catch (TimeoutException e2) {
                throw new RuntimeException("No Hold button found in the table");
            }
        }

        // Click the Hold button
        try {
            holdBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", holdBtn);
        }

        // Wait for the Hold Mining License modal
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/div[2]/div/div[2]/div/div[1]")));
    }

    @Test(priority = 1)
    public void testHoldModalFieldPresence() {
        openHoldModal();

        Assert.assertTrue(driver.findElement(By.xpath("//textarea[@placeholder='Type the reason for hold here']")).isDisplayed(), "Reason field missing.");
        Assert.assertTrue(driver.findElement(By.xpath("//button[span[text()='Hold']]")).isDisplayed(), "Hold button missing.");
    }

    @Test(priority = 2)
    public void testHoldModalRequiredValidation() {
        WebElement holdButton = driver.findElement(By.xpath("//button[span[text()='Confirm Hold']]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", holdButton);

        // Click Hold without filling the form
        holdButton.click();

        // Wait for toast message to appear (common toast selectors for Ant Design)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            // Check for Ant Design toast/message notification
            WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'ant-message')] | //div[contains(@class,'ant-notification')] | //div[contains(@class,'toast')]")));

            Assert.assertTrue(toastMessage.isDisplayed(), "Validation toast message not shown for required fields.");

            // Optionally verify the toast message content contains validation text
            String toastText = toastMessage.getText().toLowerCase();
            Assert.assertTrue(toastText.contains("required") || toastText.contains("field") ||
                            toastText.contains("empty") || toastText.contains("validation") ||
                            toastText.contains("error"),
                    "Toast message doesn't contain expected validation text. Actual text: " + toastText);

        } catch (TimeoutException e) {
            // If toast doesn't appear, check for form field validation errors as fallback
            try {
                WebElement fieldError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'ant-form-item-has-error')] | //div[contains(@class,'ant-form-item-explain-error')]")));
                Assert.assertTrue(fieldError.isDisplayed(), "Neither toast message nor field validation error shown.");
            } catch (TimeoutException e2) {
                Assert.fail("No validation feedback found - neither toast message nor field errors appeared.");
            }
        }
    }

    @Test(priority = 3)
    public void testHoldModalCloseButton() {

        WebElement closeBtn = driver.findElement(
                By.cssSelector(".ant-modal-wrap:not([style*='display: none']) .ant-modal-close"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);

        // Verify modal closed
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".ant-modal-wrap:not([style*='display: none'])")));
    }

 @Test(priority = 4)
 public void testHoldModalValidSubmission() {
 openHoldModal();

 try {

 // Add comments
 waitAndSendKeys(By.xpath("//textarea[@placeholder='Type the reason for hold here']"), "Test Hold");

 // Submit
 WebElement submitButton = new WebDriverWait(driver, Duration.ofSeconds(10))
 .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[span[text()='Confirm Hold']]")));
 submitButton.click();

 // Wait for success or error message
 // Wait for any toast or message box to appear
 By toastLocator = By.cssSelector("div.ant-message > div"); // or tweak based on actual DOM

 WebElement messageBox = new WebDriverWait(driver, Duration.ofSeconds(30))
 .until(ExpectedConditions.visibilityOfElementLocated(toastLocator));

 // Print message content for debugging
 System.out.println("Message displayed: " + messageBox.getText());

 // Fail only if it's an error
 if (messageBox.getAttribute("class").contains("ant-message-error")) {
 Assert.fail("Submission failed with error: " + messageBox.getText());
 } else {
 Assert.assertTrue(messageBox.getText().toLowerCase().contains("success")
 || messageBox.getAttribute("class").contains("success"),
 "Expected success message, but got: " + messageBox.getText());
 }


 } catch (Exception e) {
 takeScreenshot("hold_modal_failure");
 throw new RuntimeException("Test failed due to exception: ", e);
 }
 }


 private boolean isElementPresent(By locator) {
 try {
 return driver.findElement(locator).isDisplayed();
 } catch (NoSuchElementException e) {
 return false;
 }
 }

 private void waitAndSendKeys(By locator, String text) {
 WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
 WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

 ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
 wait.until(ExpectedConditions.elementToBeClickable(element)).click();

 element.clear();
 element.sendKeys(text);
 }


 private void takeScreenshot(String name) {
 try {
 File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
 FileUtils.copyFile(screenshot, new File("screenshots/" + name + "_" + System.currentTimeMillis() + ".png"));
 } catch (Exception e) {
 System.err.println("Failed to take screenshot: " + e.getMessage());
 }
 }

}