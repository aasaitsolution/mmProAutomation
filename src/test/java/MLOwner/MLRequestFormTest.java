package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.time.Duration;

public class MLRequestFormTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test(priority = 1)
    public void loginToDashboard() {
        driver.get("https://mmpro.aasait.lk/");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username"))).sendKeys("pasindu");
        driver.findElement(By.id("sign-in_password")).sendKeys("12345678");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/mlowner/home"));

        System.out.println("✅ Logged into ML Owner Dashboard.");
    }

    // --- CORRECTED TEST METHOD ---
    @Test(priority = 2, dependsOnMethods = "loginToDashboard")
    public void fillAndSubmitMLRequestForm() {
        driver.get("https://mmpro.aasait.lk/mlowner/home/mlrequest");

        // Wait for a specific, required field to be visible before proceeding. This is more reliable.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("land_name")));
        System.out.println("✅ ML Request form is loaded.");

        // --- Fill text fields using By.id and explicit waits ---
        // Ant Design uses the 'name' prop to generate the 'id' of the input field.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("exploration_nb"))).sendKeys("EXP-2025-001");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("land_name"))).sendKeys("Green Hills Estate");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("land_google"))).sendKeys("https://maps.app.goo.gl/examplelocation");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("land_owner_name"))).sendKeys("Mr. Silva");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("village_name"))).sendKeys("Kalubowila");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("grama_niladari"))).sendKeys("Grama Niladhari Colombo 05");
        System.out.println("📝 Filled all text fields.");

        // --- Select District (Colombo) ---
        WebElement districtDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("district")));
        districtDropdown.click();

// Ant Design renders options in a popup div attached to <body>, so we use this:
        WebElement districtOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[role='option'] .ant-select-item-option-content")));
        districtOption.findElement(By.xpath(".[text()='Colombo']")).click();
        System.out.println("📍 Selected District: Colombo");

// --- Select Division (Dehiwala) ---
        WebElement divisionDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("divisional_secretary_division")));
        divisionDropdown.click();

// Wait and click the correct division
        WebElement divisionOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("div[role='option'] .ant-select-item-option-content")));
        divisionOption.findElement(By.xpath(".[text()='Dehiwala']")).click();
        System.out.println("📍 Selected Division: Dehiwala");


// Wait until the new division options are loaded and select
//        WebElement divisionOption = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//div[@class='ant-select-item-option-content' and text()='Dehiwala']")));
//        divisionOption.click();
//        System.out.println("📍 Selected Division: Dehiwala");


        // --- Upload files using the corrected helper method ---
        // Create dummy files if they don't exist for the test to run
        createDummyFile("test-files/deed-plan.pdf");
        createDummyFile("test-files/detailed-plan.pdf");
        createDummyFile("test-files/economic-report.pdf");
        createDummyFile("test-files/survey-plan.pdf");

        uploadFile("Deed_plan", "test-files/deed-plan.pdf");
        uploadFile("detailed_mine_plan", "test-files/detailed-plan.pdf");
        uploadFile("economic_viability_report", "test-files/economic-report.pdf");
        uploadFile("license_boundary_survey", "test-files/survey-plan.pdf");

//        String nicFrontPath = projectPath + "/src/test/resources/test_images/nic_front.jpg";

        // --- Submit form ---
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        // Scroll to the button to ensure it's in view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        // Wait for the button to be clickable before clicking
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
        System.out.println("🚀 Submitting the form...");

        // --- Confirm submission success ---
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-message-success")));
        System.out.println("✅ Form submission successful! Message: " + successMessage.getText());
    }

    // --- CORRECTED HELPER METHOD for Ant Design Uploads ---
    private void uploadFile(String inputId, String relativeFilePath) {
        try {
            // Get the absolute path of the file
            String absolutePath = new File(relativeFilePath).getAbsolutePath();

            // The <input type="file"> is HIDDEN in Ant Design's Upload component.
            // We must wait for it to be PRESENT in the DOM, not visible.
            WebElement uploadInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(inputId)));

            // Send the file path directly to the hidden input element.
            uploadInput.sendKeys(absolutePath);
            System.out.println("📎 Uploaded '" + relativeFilePath + "' to input #" + inputId);
        } catch (Exception e) {
            System.err.println("❌ Failed to upload file for input #" + inputId + " - " + e.getMessage());
            throw new RuntimeException("File upload failed for " + inputId, e);
        }
    }

    // Helper to create dummy files for testing purposes
    private void createDummyFile(String relativePath) {
        try {
            File file = new File(relativePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Create parent directories if they don't exist
                file.createNewFile();
            }
        } catch (Exception e) {
            System.err.println("Could not create dummy file: " + relativePath);
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
