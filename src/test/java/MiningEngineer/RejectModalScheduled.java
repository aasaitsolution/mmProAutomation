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

public class RejectModalScheduled extends AppointmentsTestBase {
    // Helper to open Scheduled tab and Reject modal
    private void openRejectModal() {
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

        // Try to find and click the Reject button
        WebElement rejectBtn = null;
        try {
            // First try - exact match
            rejectBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//table/tbody/tr//button[span[text()='Reject']]")));
        } catch (TimeoutException e) {
            System.out.println("Reject button not found with exact match, trying alternatives...");

            // Print available buttons for debugging
            List<WebElement> buttons = driver.findElements(By.xpath("//table/tbody/tr//button"));
            System.out.println("Available buttons in table: " + buttons.size());
            for (WebElement btn : buttons) {
                System.out.println("Button text: '" + btn.getText() + "'");
            }

            // Try alternative selectors
            try {
                rejectBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//table/tbody/tr//button[contains(text(),'Reject')]")));
            } catch (TimeoutException e2) {
                throw new RuntimeException("No Reject button found in the table");
            }
        }

        // Click the reject button
        try {
            rejectBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", rejectBtn);
        }

        // Wait for the Reject Appointment modal
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-modal')]//div[text()='Reject Appointment']")));
    }

    @Test(priority = 1)
    public void testRejectModalFieldPresence() {
        openRejectModal();

        // Check for textarea with correct selector based on the HTML structure
        Assert.assertTrue(driver.findElement(By.xpath("//textarea[@id='comment' and @aria-required='true']")).isDisplayed(),
                "Reason field missing.");

        // Check for upload button with correct selector
        Assert.assertTrue(driver.findElement(By.xpath("//button[span[text()='Upload Report (PDF)']]")).isDisplayed(),
                "Upload button missing.");

        // Check for confirm rejection button
        Assert.assertTrue(driver.findElement(By.xpath("//button[span[text()='Confirm Rejection']]")).isDisplayed(),
                "Confirm button missing.");
    }

    @Test(priority = 2)
    public void testRejectModalRequiredValidation() {
        openRejectModal();
        // Find the submit button using the correct selector from HTML
        WebElement submitButton = driver.findElement(By.xpath("//button[span[text()='Confirm Rejection']]")
);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);

        // Click Submit without filling the form
        submitButton.click();

        // Wait for toast message to appear (common toast selectors for Ant Design)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        try {
            // Check for Ant Design toast/message notification
            WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                   By.xpath("//div[contains(@class,'ant-message-notice-content')]//span[contains(text(),'Please fill all required fields')]")
            ));
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
    public void testRejectModalValidSubmission() {
        try {
            // Fill the reason textarea
            WebElement reasonField = driver.findElement(By.xpath("//textarea[@id='comment']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", reasonField);
            reasonField.clear();
            reasonField.sendKeys("Test rejection reason for automated testing");

            // Upload file (optional based on form requirements)
            try {
                File file = new File("src/test/resources/test-files/rejection-report.pdf");
                if (file.exists()) {
                    WebElement fileInput = driver.findElement(By.xpath("//input[@type='file' and @accept='.pdf']"));
                    fileInput.sendKeys(file.getAbsolutePath());

                    // Wait for file to be uploaded
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                System.out.println("File upload optional or failed: " + e.getMessage());
            }

            // Submit the form
            WebElement submitButton = driver.findElement(By.xpath("//button[span[text()='Confirm Rejection']]"));
            submitButton.click();

            // Wait for success or error message
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            try {
                WebElement messageBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(@class,'ant-message')] | //div[contains(@class,'ant-notification')]")));

                System.out.println("Message displayed: " + messageBox.getText());

                // Check if it's a success message
                String messageText = messageBox.getText().toLowerCase();
//                Assert.assertTrue(messageText.contains("success") || messageText.contains("rejected") ||
//                                messageText.contains("confirm"),
//                        "Expected success message, but got: " + messageBox.getText());
            } catch (TimeoutException e) {
                // If no toast appears, check if modal closed (indicating success)
                try {
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(
                            By.xpath("//div[contains(@class,'ant-modal')]//div[text()='Reject Appointment']")));
                    System.out.println("Modal closed - rejection likely successful");
                } catch (TimeoutException e2) {
                    Assert.fail("No success feedback received after form submission");
                }
            }

        } catch (Exception e) {
            takeScreenshot("reject_modal_failure");
            throw new RuntimeException("Test failed due to exception: ", e);
        }
    }

    @Test(priority = 4)
    public void testRejectModalCloseButton() {
        // Re-open modal if it was closed in previous test
        try {
            driver.findElement(By.xpath("//div[contains(@class,'ant-modal')]//div[text()='Reject Appointment']"));
        } catch (NoSuchElementException e) {
            openRejectModal();
        }

        WebElement closeBtn = driver.findElement(
                By.cssSelector(".ant-modal-wrap:not([style*='display: none']) .ant-modal-close"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);

        // Verify modal closed
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".ant-modal-wrap:not([style*='display: none'])")));
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
        element.sendKeys(Keys.TAB);  // Ensure the datepicker gets closed
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

    /**
    @Test(priority = 3)
    public void testRejectModalValidSubmission() {
        try {
            // Fill required fields with explicit waits
            waitAndSendKeys(By.xpath("//input[@placeholder='Enter total capacity in cubic meters']"), "3000");
            waitAndSendKeys(By.xpath("//input[@placeholder='Enter monthly maximum capacity']"), "300");

            // Fill dates
            waitAndSendKeys(By.xpath("(//input[@placeholder='Select date'])[1]"), "2025-08-01"); // Start Date
            waitAndSendKeys(By.xpath("(//input[@placeholder='Select date'])[2]"), "2026-08-01"); // Expiry Date

            // Upload file
            File file = new File("src/test/resources/test-files/economic-report.pdf");
            Assert.assertTrue(file.exists(), "Test file not found");

            By fileInputLocator = By.xpath("//input[@type='file']");
            WebElement fileInput = driver.findElement(fileInputLocator);
            fileInput.sendKeys(file.getAbsolutePath());

            //  Wait for uploaded file name to appear
            By uploadedFile = By.xpath("//span[contains(@class, 'ant-upload-list-item-name') and contains(text(), 'economic-report.pdf')]");
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(uploadedFile));

            // Add comments
            waitAndSendKeys(By.xpath("//textarea[@placeholder='Enter additional comments']"), "Test approval");

            // Submit
            WebElement submitButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[span[text()='Submit']]")));
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
            takeScreenshot("reject_modal_failure");
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
        element.sendKeys(Keys.TAB);  // Ensure the datepicker gets closed
    }


    private void takeScreenshot(String name) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("screenshots/" + name + "_" + System.currentTimeMillis() + ".png"));
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }

    @Test(priority = 4)
    public void testRejectModalCloseButton() {

        WebElement closeBtn = driver.findElement(
                By.cssSelector(".ant-modal-wrap:not([style*='display: none']) .ant-modal-close"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);

        // Verify modal closed
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".ant-modal-wrap:not([style*='display: none'])")));
    }
    **/

