// package MiningEngineer;

// import org.openqa.selenium.*;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.testng.Assert;
// import org.testng.annotations.Test;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;

// public class AppointmentsSetDateTest extends AppointmentsTestBase {

//     @Test(priority = 1)
//     public void testSetDateFunctionality() {
//         try {
//             switchToPendingTab();
//             waitForAppointmentsToLoad();
//             clickFirstSetDateButton();
//             verifyDatePickerVisible();
//             selectFutureDate(1); // Select tomorrow's date
//             clickConfirmButton();
//             verifySuccessMessage();
//         } catch (Exception e) {
//             captureScreenshot("set_date_failure");
//             throw new RuntimeException("Set Date test failed", e);
//         }
//     }

//     @Test(priority = 2)
//     public void testPastDatesDisabled() {
//         try {
//             switchToPendingTab();
//             waitForAppointmentsToLoad();
//             clickFirstSetDateButton();
//             verifyDatePickerVisible();
//             verifyDateDisabled(LocalDate.now().minusDays(1));
//         } catch (Exception e) {
//             captureScreenshot("past_dates_disabled_failure");
//             throw new RuntimeException("Past dates disabled test failed", e);
//         }
//     }

//     @Test(priority = 3)
//     public void testConfirmWithoutDateShowsError() {
//         try {
//             switchToPendingTab();
//             waitForAppointmentsToLoad();
//             clickFirstSetDateButton();
//             verifyDatePickerVisible();
//             clickConfirmButton();
//             verifyErrorMessage("Please select a date first!");
//         } catch (Exception e) {
//             captureScreenshot("confirm_without_date_failure");
//             throw new RuntimeException("Confirm without date test failed", e);
//         }
//     }

//     // ─────────── HELPER METHODS ─────────────

//     private void switchToPendingTab() {
//         By pendingTabLocator = By.xpath("//div[contains(@class, 'ant-tabs-tab') and contains(., 'Pending')]");

//         try {
//             WebElement pendingTab = wait.until(ExpectedConditions.elementToBeClickable(pendingTabLocator));
//             scrollIntoView(pendingTab);
//             clickWithRetry(pendingTab);

//             wait.until(ExpectedConditions.or(
//                     ExpectedConditions.attributeContains(pendingTab, "class", "ant-tabs-tab-active"),
//                     ExpectedConditions.presenceOfElementLocated(
//                             By.xpath("//div[contains(@class, 'ant-tabs-tabpane-active')]"))
//             ));

//             waitForAppointmentsToLoad();

//         } catch (TimeoutException e) {
//             System.out.println("Current URL: " + driver.getCurrentUrl());
//             throw new RuntimeException("Failed to switch to Pending tab", e);
//         }
//     }

//     private void clickWithRetry(WebElement element) {
//         int attempts = 0;
//         while (attempts < 2) {
//             try {
//                 element.click();
//                 return;
//             } catch (StaleElementReferenceException e) {
//                 attempts++;
//                 if (attempts >= 2) throw e;
//             }
//         }
//     }

//     private void waitForAppointmentsToLoad() {
//         try {
//             wait.until(ExpectedConditions.visibilityOfElementLocated(
//                     By.xpath("//div[contains(@class, 'ant-table-container')]")));

//             wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
//                     By.xpath("//tr[contains(@class, 'ant-table-row')]"), 0));
//         } catch (TimeoutException e) {
//             throw new RuntimeException("Appointments table failed to load", e);
//         }
//     }

//     private void clickFirstSetDateButton() {
//         By setDateButton = By.xpath("(//button[contains(., 'Set Date')])[1]");
//         clickElement(setDateButton);
//     }

//     private void verifyDatePickerVisible() {
//         try {
//             wait.until(ExpectedConditions.visibilityOfElementLocated(
//                     By.xpath("//div[contains(@class, 'ant-picker-dropdown') and not(contains(@style, 'display: none'))]")));

//             wait.until(ExpectedConditions.visibilityOfElementLocated(
//                     By.xpath("//div[contains(@class, 'ant-picker-header')]")));
//         } catch (TimeoutException e) {
//             throw new RuntimeException("Date picker popup did not appear", e);
//         }
//     }

//     private void selectFutureDate(int daysInFuture) {
//         LocalDate futureDate = LocalDate.now().plusDays(daysInFuture);
//         selectSpecificDate(futureDate);
//     }

//     private void selectSpecificDate(LocalDate date) {
//         String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

//         try {
//             By dateCell = By.xpath(String.format(
//                     "//td[@title='%s' and not(contains(@class, 'disabled'))]", dateStr));

//             WebElement cell = wait.until(ExpectedConditions.elementToBeClickable(dateCell));
//             scrollIntoView(cell);
//             clickWithRetry(cell);

//             wait.until(ExpectedConditions.attributeContains(cell, "class", "ant-picker-cell-selected"));

//         } catch (TimeoutException e) {
//             throw new RuntimeException("Failed to select date: " + dateStr, e);
//         }
//     }

//     private void verifyDateDisabled(LocalDate date) {
//         String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//         By dateCell = By.xpath(String.format("//td[@title='%s']", dateStr));

//         WebElement cell = wait.until(ExpectedConditions.presenceOfElementLocated(dateCell));
//         Assert.assertTrue(cell.getAttribute("class").contains("disabled"),
//                 "Date " + dateStr + " should be disabled");
//     }

//     private void clickConfirmButton() {
//         By confirmButton = By.xpath("//button[contains(., 'Confirm')]");
//         clickElement(confirmButton);
//     }

//     private void verifySuccessMessage() {
//         verifyMessage("Date confirmed and appointment scheduled!");
//     }

//     private void verifyErrorMessage(String expectedMessage) {
//         verifyMessage(expectedMessage);
//     }

//     private void verifyMessage(String expectedText) {
//         By messageLocator = By.xpath(String.format(
//                 "//div[contains(@class, 'ant-message-notice') and contains(., '%s')]",
//                 expectedText));

//         try {
//             wait.until(ExpectedConditions.visibilityOfElementLocated(messageLocator));
//         } catch (TimeoutException e) {
//             throw new RuntimeException("Expected message not displayed: " + expectedText, e);
//         }
//     }
// }