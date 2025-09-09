package MLOwner;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;


import java.io.File;
import java.time.Duration;

public class MLRequestFormTest extends BaseTest {

//    private WebDriver driver;
//    private WebDriverWait wait;
//
//    @BeforeClass
//    public void setup() {
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--incognito");
//        driver = new ChromeDriver(options);
//        driver.manage().window().maximize();
//        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//    }

    private void waitABit() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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

    @Test(priority = 2, dependsOnMethods = "loginToDashboard")
    public void openMLRequestFormPage() {
        driver.get("https://mmpro.aasait.lk/mlowner/home/mlrequest");
        wait.until(driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("land_name")));
        waitABit();
        System.out.println("ML Request form is loaded.");
    }

    @Test(priority = 3, dependsOnMethods = "openMLRequestFormPage")
    public void fillMLRequestFormFields() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("exploration_nb"))).sendKeys("EXP-2025-001");
        waitABit();
        driver.findElement(By.id("land_name")).sendKeys("Green Hills Estate");
        waitABit();
        driver.findElement(By.id("land_google")).sendKeys("https://maps.app.goo.gl/examplelocation");
        waitABit();
        driver.findElement(By.id("land_owner_name")).sendKeys("Mr. Silva");
        waitABit();
        driver.findElement(By.id("village_name")).sendKeys("Kalubowila");
        waitABit();
        driver.findElement(By.id("grama_niladari")).sendKeys("Grama Niladhari Colombo 05");
        waitABit();
        System.out.println("📝 Filled all text fields.");
    }

    @Test(priority = 4, dependsOnMethods = "fillMLRequestFormFields")
    public void selectDistrictAndDivision() {
        WebElement districtDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='district']/ancestor::div[contains(@class,'ant-select')]")));
        districtDropdown.click();
        waitABit();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.ant-select-dropdown")));
        WebElement colomboOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='ant-select-item-option-content' and normalize-space(text())='Colombo']")));
        colomboOption.click();
        waitABit();
        System.out.println("✅ Selected 'Colombo' from district list");

        WebElement divisionDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='divisional_secretary_division']/ancestor::div[contains(@class,'ant-select')]")));
        divisionDropdown.click();
        waitABit();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.ant-select-dropdown")));
        WebElement dehiwalaOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='ant-select-item-option-content' and normalize-space(text())='Dehiwala']")));
        dehiwalaOption.click();
        waitABit();
        System.out.println("✅ Selected 'Dehiwala' from division list");
    }

    @Test(priority = 5, dependsOnMethods = "selectDistrictAndDivision")
    public void uploadRequiredFiles() {
        createDummyFile("test-files/deed-plan.pdf");
        createDummyFile("test-files/detailed-plan.pdf");
        createDummyFile("test-files/economic-report.pdf");
        createDummyFile("test-files/survey-plan.pdf");

        uploadFile("Deed_plan", "test-files/deed-plan.pdf");
        waitABit();
        uploadFile("detailed_mine_plan", "test-files/detailed-plan.pdf");
        waitABit();
        uploadFile("economic_viability_report", "test-files/economic-report.pdf");
        waitABit();
        uploadFile("license_boundary_survey", "test-files/survey-plan.pdf");
        waitABit();
    }

    @Test(priority = 6, dependsOnMethods = "uploadRequiredFiles")
    public void submitFormSuccessfully() throws InterruptedException {
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
        System.out.println("🚀 Submitting the form...");

        Thread.sleep(3000);
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-message-success")));
        System.out.println("✅ Form submission successful! Message: " + successMessage.getText());
    }

    @Test(priority = 7)
    public void testMissingMandatoryField() {
        driver.get("https://mmpro.aasait.lk/mlowner/home/mlrequest");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("land_name"))).sendKeys("");
        waitABit();
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        submitButton.click();
        waitABit();

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-form-item-explain-error")));
        Assert.assertTrue(errorMsg.isDisplayed(), "❌ Validation message not shown for empty required field");
        System.out.println("⚠️ Validation error displayed as expected when required field is missing.");
    }

    private void uploadFile(String inputId, String relativeFilePath) {
        try {
            String absolutePath = new File(relativeFilePath).getAbsolutePath();
            WebElement uploadInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(inputId)));
            uploadInput.sendKeys(absolutePath);
            System.out.println("📎 Uploaded '" + relativeFilePath + "' to input #" + inputId);
        } catch (Exception e) {
            System.err.println("❌ Failed to upload file for input #" + inputId + " - " + e.getMessage());
            throw new RuntimeException("File upload failed for " + inputId, e);
        }
    }

    private void createDummyFile(String relativePath) {
        try {
            File file = new File(relativePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (Exception e) {
            System.err.println("Could not create dummy file: " + relativePath);
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            //driver.quit();
            System.out.println("🚪 Browser closed.");
        }
    }
}
