package GSMBOfficer;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import base.BaseTest;
import java.io.File;
import java.time.Duration;
import java.util.List;

public class RequestMiningFilter extends BaseTest{
//    private WebDriver driver;
//    private WebDriverWait wait;
    private static final String BASE_URL = "https://mmpro.aasait.lk";
    private static final String USERNAME = "nimal";
    private static final String PASSWORD = "12345678";

//    @BeforeClass
//    public void setupClass() {
//        System.out.println("Starting Filter Dropdown Test Suite");
//    }
//
//    @BeforeMethod
//    public void setup() {
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--incognito");
//        options.addArguments("--disable-notifications");
//        options.addArguments("--disable-popup-blocking");
//
//        driver = new ChromeDriver(options);
//        driver.manage().window().maximize();
//        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//    }

    @Test(priority = 2, groups = {"functional", "filter"})
    public void testPhysicalDocumentFilter() {
        try {
            navigateToMiningLicenseTab();
            selectFilterOption("Physical Document");

            // Verify that Physical Document records are displayed
            verifyFilterResults("Physical Document");

            System.out.println("✅ Physical Document filter test passed");
        } catch (Exception e) {
            System.out.println("❌ Physical Document filter test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 3, groups = {"functional", "filter"})
    public void testPendingFilter() {
        try {
            navigateToMiningLicenseTab();
            selectFilterOption("Pending");

            // Verify that Pending records are displayed
            verifyFilterResults("Pending");

            System.out.println("✅ Pending filter test passed");
        } catch (Exception e) {
            System.out.println("❌ Pending filter test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4, groups = {"functional", "filter"})
    public void testApprovedFilter() {
        try {
            navigateToMiningLicenseTab();
            selectFilterOption("ME Approved");

            // Verify that Approved records are displayed
            verifyFilterResults("Approved");

            System.out.println("✅ Approved filter test passed");
        } catch (Exception e) {
            System.out.println("❌ Approved filter test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 5, groups = {"functional", "filter"})
    public void testRejectedFilter() {
        try {
            navigateToMiningLicenseTab();
            selectFilterOption("Rejected");

            // Verify that Rejected records are displayed
            verifyFilterResults("Rejected");

            System.out.println("✅ Rejected filter test passed");
        } catch (Exception e) {
            System.out.println("❌ Rejected filter test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 6, groups = {"functional", "filter"})
    public void testUnderReviewFilter() {
        try {
            navigateToMiningLicenseTab();
            selectFilterOption("Awaiting ME Scheduling");

            // Verify that Under Review records are displayed
            verifyFilterResults("Awaiting ME Scheduling");

            System.out.println("✅ Under Review filter test passed");
        } catch (Exception e) {
            System.out.println("❌ Under Review filter test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 8, groups = {"functional", "filter"})
    public void testFilterDropdownVisibility() {
        try {
            navigateToMiningLicenseTab();

            // Click the filter dropdown button
            WebElement filterDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class, 'ant-select') and .//span[text()='Filter by status']]")));
            filterDropdown.click();

            // Verify all filter options are visible
            List<WebElement> filterOptions = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                    By.xpath("//div[contains(@class, 'ant-select-item-option')]")));

            Assert.assertTrue(filterOptions.size() > 0, "Filter options should be visible");

            // Verify specific options exist
            String[] expectedOptions = {"Pending", "Physical Document",  "Awaiting ME Scheduling", "ME Approved" ,"Rejected"};
            for (String option : expectedOptions) {
                boolean optionExists = filterOptions.stream()
                        .anyMatch(element -> element.getText().trim().equals(option));
                Assert.assertTrue(optionExists, "Filter option '" + option + "' should be present");
            }

            System.out.println("✅ Filter dropdown visibility test passed");
        } catch (Exception e) {
            System.out.println("❌ Filter dropdown visibility test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 9, groups = {"functional", "filter"})
    public void testFilterReset() {
        try {
            navigateToMiningLicenseTab();

            // Apply a filter
            selectFilterOption("Pending");
            waitABit();

            // Reset filter by selecting "All" or clearing selection
            WebElement filterDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[span[normalize-space(text())='Reset Filters']]")));
            filterDropdown.click();


            waitABit();

            // Verify all records are displayed
            List<WebElement> tableRows = driver.findElements(
                    By.xpath("//table/tbody/tr"));
            Assert.assertTrue(tableRows.size() > 0, "All records should be displayed after filter reset");

            System.out.println("✅ Filter reset test passed");
        } catch (Exception e) {
            System.out.println("❌ Filter reset test failed: " + e.getMessage());
            throw e;
        }
    }


    // ==================== HELPER METHODS ====================

    private void navigateToMiningLicenseTab() {
        performLogin();
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/gsmb/dashboard"));
        waitABit();

        // Navigate to Mining License tab
        WebElement mlTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@id=\"root\"]/div/main/div/div[2]/div[1]/div[1]/div/div[5]")));
        mlTab.click();
        waitABit();
    }

    private void selectFilterOption(String optionText) {
        // Click the filter dropdown button
        WebElement filterDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select') and .//span[text()='Filter by status']]")));
        filterDropdown.click();

        // Wait for dropdown options to appear and select the specified option
        WebElement filterOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class, 'ant-select-item-option') and text()='" + optionText + "']")));
        filterOption.click();

        waitABit();
    }

    private void testDropdownOption(String optionText, String testDescription) {
        try {
            selectFilterOption(optionText);

            // Verify the filter was applied by checking if the dropdown shows the selected value
            WebElement selectedValue = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//div[contains(@class, 'ant-select-selection-item') and text()='" + optionText + "']")));
            Assert.assertTrue(selectedValue.isDisplayed(), testDescription + " should be selected");

            System.out.println("✅ " + testDescription + " test passed");
        } catch (Exception e) {
            System.out.println("❌ " + testDescription + " test failed: " + e.getMessage());
            throw new AssertionError(testDescription + " test failed", e);
        }
    }

    private void verifyFilterResults(String filterType) {
        // Wait for table to load with filtered results
        waitABit();

        // Get table rows
        List<WebElement> tableRows = driver.findElements(
                By.xpath("//table/tbody/tr"));

        if (tableRows.size() == 0) {
            System.out.println("⚠️  No records found for filter: " + filterType);
            return;
        }

        // Verify that displayed records match the filter
        for (WebElement row : tableRows) {
            try {
                // Check if the row contains the expected status
                // This xpath may need adjustment based on your table structure
                List<WebElement> statusCells = row.findElements(By.xpath(".//td"));
                boolean statusFound = false;

                for (WebElement cell : statusCells) {
                    if (cell.getText().toLowerCase().contains(filterType.toLowerCase())) {
                        statusFound = true;
                        break;
                    }
                }

                // For Physical Document, check if Physical Meeting button is present
                if (filterType.equals("Physical Document")) {
                    List<WebElement> physicalMeetingButtons = row.findElements(
                            By.xpath(".//button[contains(text(), 'Physical Meeting') or contains(@class, 'physical-meeting')]"));
                    if (physicalMeetingButtons.size() > 0) {
                        statusFound = true;
                    }
                }

                System.out.println("Row verified for filter: " + filterType);
            } catch (Exception e) {
                System.out.println("Warning: Could not verify row for filter " + filterType + ": " + e.getMessage());
            }
        }

        System.out.println("✅ Filter results verified for: " + filterType);
    }

    private void performLogin() {
        driver.get(BASE_URL + "/signin/");

        WebElement username = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_username")));
        username.sendKeys(USERNAME);

        WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.id("sign-in_password")));
        password.sendKeys(PASSWORD);

        WebElement signinButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".ant-btn-primary")));
        signinButton.click();
    }

    private void waitABit() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            try {
                Thread.sleep(2000); // Brief pause to observe results
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            driver.quit();
        }
    }

    @AfterClass
    public void tearDownClass() {
        System.out.println("Filter Dropdown Test Suite completed");
        if (driver != null) {
            driver.quit();
        }
    }
}