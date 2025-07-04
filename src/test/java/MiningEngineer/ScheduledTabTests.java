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
import java.util.List;

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

//    @Test(priority = 13)
//    public void testFillAndSubmitApprovalForm() {
//        try {
//            // 1. Log browser console warnings/errors (for debugging)
//            driver.manage().logs().get("browser").forEach(
//                    log -> System.out.println("BROWSER LOG: " + log));
//
//            // 2. Try multiple modal selectors with fallback strategy
//            WebElement modal = null;
//            String[] modalSelectors = {
//                    ".ant-modal-content", // Common content class
//                    ".ant-modal-wrap:not([style*='display: none'])", // Visible wrapper
//                    "[role='dialog']", // Generic dialog role
//                    "//div[contains(@class,'modal') and .//*[contains(text(),'Approve')]]" // Modal with "Approve"
//            };
//
//            for (String selector : modalSelectors) {
//                try {
//                    if (selector.startsWith("//")) {
//                        modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
//                    } else {
//                        modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
//                    }
//                    System.out.println("Modal found with selector: " + selector);
//                    break;
//                } catch (TimeoutException e) {
//                    System.out.println("Modal not found with selector: " + selector);
//                }
//            }
//
//            if (modal == null) {
//                takeScreenshot("modal_not_found");
//                Assert.fail("Could not locate approval modal with any selector");
//            }
//
//            // 3. Fill out the remarks field (textarea)
//            WebElement remarksInput = wait.until(ExpectedConditions.elementToBeClickable(
//                    modal.findElement(By.xpath(".//textarea"))));
//            remarksInput.clear();
//            remarksInput.sendKeys("Approved for next phase");
//
//            // 4. Handle the date picker
//            WebElement datePicker = wait.until(ExpectedConditions.elementToBeClickable(
//                    modal.findElement(By.cssSelector(".ant-picker"))));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", datePicker);
//
//            WebElement today = wait.until(ExpectedConditions.elementToBeClickable(
//                    By.cssSelector(".ant-picker-cell-today")));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", today);
//
//            // 5. Click the submit button (supports multilingual)
//            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                    modal.findElement(By.xpath(".//button[contains(.,'Submit') or contains(.,'ඉදිරිපත් කරන්න') or contains(.,'சமர்ப்பிக்கவும்')]"))));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
//            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
//
//            // 6. Verify modal closes and success toast appears
//            try {
//                // Wait for modal content to be invisible (more reliable than wrap)
//                wait.until(ExpectedConditions.invisibilityOfElementLocated(
//                        By.cssSelector(".ant-modal-content")));
//
//                // Wait for success toast (in any language)
//                WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                        By.xpath("//div[contains(@class,'ant-message') and " +
//                                "(contains(.,'success') or contains(.,'සාර්ථකව') or contains(.,'வெற்றி'))]")));
//
//                Assert.assertTrue(successToast.isDisplayed(), "Success toast not shown");
//
//            } catch (TimeoutException e) {
//                takeScreenshot("submit_timeout");
//                Assert.fail("Failed to detect submission success: " + e.getMessage());
//            }
//
//        } catch (Exception e) {
//            takeScreenshot("approval_form_failure");
//            Assert.fail("Failed to fill and submit approval form: " + e.getMessage());
//        }
//    }



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


//    @Test(priority = 17)
//    public void testModalHoldButtonFunctionality() {
//        try {
//            // 1. First verify the hold button exists and is visible
//            WebElement holdBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                    By.xpath("//button[contains(.,'Hold')]")));
//
//            System.out.println("Hold button found, attempting to click...");
//            ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", holdBtn);
//            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", holdBtn);
//
//            // 2. Try multiple modal detection strategies
//            WebElement modal = null;
//            String[] modalSelectors = {
//                    "//div[contains(@class,'ant-modal') and .//*[contains(text(),'Hold Mining License')]]", // Strict
//                    "//div[contains(@class,'ant-modal')]", // Just modal
//                    "//div[@role='dialog' and .//*[contains(text(),'Hold')]]", // Role-based
//                    "//div[contains(@class,'ant-modal-wrap') and not(contains(@style,'display: none'))]" // Wrapper
//            };
//
//            for (String selector : modalSelectors) {
//                try {
//                    modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
//                    System.out.println("Modal found with selector: " + selector);
//                    break;
//                } catch (TimeoutException e) {
//                    System.out.println("Modal not found with selector: " + selector);
//                    continue;
//                }
//            }
//
//            if (modal == null) {
//                takeScreenshot("hold_modal_not_found");
//                fail("Could not locate Hold modal with any selector");
//            }
//
//            // 3. Find confirm button with multiple strategies
//            WebElement confirmHoldBtn = null;
//            String[] buttonSelectors = {
//                    ".//button[.//span[contains(.,'Hold')]]",
//                    ".//button[contains(.,'Hold')]",
//                    ".//button[contains(@class,'ant-btn-primary')]"
//            };
//
//            for (String selector : buttonSelectors) {
//                try {
//                    confirmHoldBtn = modal.findElement(By.xpath(selector));
//                    System.out.println("Confirm button found with selector: " + selector);
//                    break;
//                } catch (NoSuchElementException e) {
//                    continue;
//                }
//            }
//
//            if (confirmHoldBtn == null) {
//                takeScreenshot("confirm_button_not_found");
//                fail("Could not locate Confirm Hold button");
//            }
//
//            // 4. Click confirm button
//            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", confirmHoldBtn);
//            System.out.println("Confirm Hold button clicked");
//
//            // 5. Verify modal closed with multiple conditions
//            wait.until(ExpectedConditions.invisibilityOf(modal));
//            System.out.println("Modal closed successfully");
//
//        } catch (Exception e) {
//            takeScreenshot("hold_button_failure");
//            // Print browser console logs
//            driver.manage().logs().get("browser").forEach(l -> System.out.println("BROWSER LOG: " + l));
//            throw new AssertionError("Hold button test failed: " + e.getMessage(), e);
//        }
//    }

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

}
