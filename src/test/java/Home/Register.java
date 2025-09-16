package Home;

import base.BaseTest;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Register extends BaseTest {

    String projectPath = System.getProperty("user.dir");
    String nicFrontPath = projectPath + "/src/test/resources/test_images/nic_front.jpg";
    String nicBackPath = projectPath + "/src/test/resources/test_images/nic_back.jpg";
    String workIdPath = projectPath + "/src/test/resources/test_images/work_id.jpg";

    @BeforeMethod
    public void navigateToRegisterPage() {
        super.setup(); // Call parent setup method
        driver.get("https://mmpro.aasait.lk/");
    }

    @AfterMethod
    public void cleanupAfterTest() {
        navigateHome();
        super.tearDown(); // Call parent teardown method
    }

    private void navigateHome() {
        try {
            // Try logging out (update selector if needed)
            WebElement logoutBtn = driver.findElement(By.cssSelector("button.logout, a.logout, .logout-btn"));
            logoutBtn.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href='/signin'] button")));
        } catch (Exception e) {
            // Fallback if logout not possible
            driver.get("https://mmpro.aasait.lk/");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href='/signin'] button")));
        }
    }

    private void registerUserByRole(int roleIndex, String usernameSuffix, boolean fillDesignation, boolean uploadFiles) throws InterruptedException, IOException {
        String[] roles = {"GSMB Officer", "Police", "Mining License Owner", "Mining Engineer"};
        System.out.println("🚀 Attempting to register as: " + roles[roleIndex]);

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
        loginButton.click();

        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#sign-in > div:nth-child(6) > div > div > div > div > button > span")));
        signInButton.click();

        List<WebElement> actorButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".ant-modal-body > div > button")));
        if (roleIndex >= actorButtons.size()) {
            throw new RuntimeException("No actor button found for role index: " + roleIndex);
        }
        actorButtons.get(roleIndex).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("form.ant-form")));
        Thread.sleep(1000);

        fillFieldIfPresent("firstName", "John");
        fillFieldIfPresent("lastName", "Doe");
        fillFieldIfPresent("username", "johndoe" + usernameSuffix);
        fillFieldIfPresent("email", "john.doe" + usernameSuffix + "@example.com");
        fillFieldIfPresent("nic", "123456789V");
        fillFieldIfPresent("mobile", "0712345678");
        fillFieldIfPresent("password", "Password@123");
        fillFieldIfPresent("confirmPassword", "Password@123");

        if (fillDesignation) {
            fillFieldIfPresent("designation", roles[roleIndex] + " Tester");
        }

        if (uploadFiles) {
            uploadFileIfFieldExists("nicFront", nicFrontPath);
            uploadFileIfFieldExists("nicBack", nicBackPath);
            uploadFileIfFieldExists("workId", workIdPath);
        }

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        submitButton.click();

        try {
            WebElement messageBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-message-success")));
            System.out.println("✅ Registration successful for role: " + roles[roleIndex]);
            System.out.println("💬 Success message: " + messageBox.getText());
        } catch (Exception e) {
            handleRegistrationError(roles[roleIndex]);
            throw new RuntimeException("❌ Registration failed for role: " + roles[roleIndex]);
        }
    }

    private void fillFieldIfPresent(String id, String value) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
            element.clear();
            element.sendKeys(value);
            ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'))", element);
        } catch (Exception e) {
            System.out.println("⚠️ Field with ID '" + id + "' not found or not clickable");
        }
    }

    private void uploadFileIfFieldExists(String fieldId, String filePath) {
        try {
            WebElement fileInput = driver.findElement(By.id(fieldId));
            fileInput.sendKeys(filePath);
        } catch (NoSuchElementException e) {
            System.out.println("⚠️ File upload field '" + fieldId + "' not found");
        }
    }

    private void handleRegistrationError(String role) {
        List<WebElement> errors = driver.findElements(By.cssSelector(".ant-form-item-explain-error"));
        if (!errors.isEmpty()) {
            System.out.println("⚠️ Validation errors for role " + role + ":");
            for (WebElement error : errors) {
                System.out.println("- " + error.getText());
            }
        } else {
            System.out.println("❌ Unknown error occurred for role: " + role);
            try {
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(screenshot, new File("registration_error_" + role.replaceAll(" ", "_") + ".png"));
            } catch (IOException ioException) {
                System.out.println("⚠️ Failed to capture screenshot: " + ioException.getMessage());
            }
        }
    }

    @Test(priority = 1)
    public void testRegisterGSMBOfficer() throws InterruptedException, IOException {
        registerUserByRole(0, String.valueOf(System.currentTimeMillis()), true, true);
    }

    @Test(priority = 2)
    public void testRegisterPolice() throws InterruptedException, IOException {
        registerUserByRole(1, String.valueOf(System.currentTimeMillis()), true, true);
    }

    @Test(priority = 3)
    public void testRegisterMiningLicenseOwner() throws InterruptedException, IOException {
        registerUserByRole(2, String.valueOf(System.currentTimeMillis()), false, false);
    }

    @Test(priority = 4)
    public void testRegisterMiningEngineer() throws InterruptedException, IOException {
        registerUserByRole(3, String.valueOf(System.currentTimeMillis()), true, true);
    }

    @Test(priority = 5)
    public void testRegisterMissingRequiredField() throws InterruptedException, IOException {
        System.out.println("🚀 Attempting to register with missing email field");

        int roleIndex = 0;
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
        loginButton.click();

        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#sign-in > div:nth-child(6) > div > div > div > div > button > span")));
        signInButton.click();

        List<WebElement> actorButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".ant-modal-body > div > button")));
        actorButtons.get(roleIndex).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("form.ant-form")));
        Thread.sleep(1000);

        // Fill all fields except email
        fillFieldIfPresent("firstName", "John");
        fillFieldIfPresent("lastName", "Doe");
        fillFieldIfPresent("username", "johndoe_missingemail" + System.currentTimeMillis());
        fillFieldIfPresent("nic", "123456789V");
        fillFieldIfPresent("mobile", "0712345678");
        fillFieldIfPresent("password", "Password@123");
        fillFieldIfPresent("confirmPassword", "Password@123");
        fillFieldIfPresent("designation", "GSMB Officer Tester");

        uploadFileIfFieldExists("nicFront", nicFrontPath);
        uploadFileIfFieldExists("nicBack", nicBackPath);
        uploadFileIfFieldExists("workId", workIdPath);

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        submitButton.click();

        List<WebElement> errors = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".ant-form-item-explain-error")));
        boolean emailErrorFound = false;
        for (WebElement error : errors) {
            String text = error.getText().toLowerCase();
            if (text.contains("email") || text.contains("required")) {
                emailErrorFound = true;
                System.out.println("⚠️ Expected validation error found: " + error.getText());
            }
        }
        assert emailErrorFound : "⚠️ Expected email validation error not found";
    }

    @Test(priority = 6)
    public void testRegisterPasswordMismatch() throws InterruptedException, IOException {
        System.out.println("🚀 Attempting to register with password mismatch");

        int roleIndex = 0;
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
        loginButton.click();

        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#sign-in > div:nth-child(6) > div > div > div > div > button > span")));
        signInButton.click();

        List<WebElement> actorButtons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".ant-modal-body > div > button")));
        actorButtons.get(roleIndex).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("form.ant-form")));
        Thread.sleep(1000);

        fillFieldIfPresent("firstName", "John");
        fillFieldIfPresent("lastName", "Doe");
        fillFieldIfPresent("username", "johndoe_pw_mismatch" + System.currentTimeMillis());
        fillFieldIfPresent("email", "john.doe_pw_mismatch" + System.currentTimeMillis() + "@example.com");
        fillFieldIfPresent("nic", "123456789V");
        fillFieldIfPresent("mobile", "0712345678");
        fillFieldIfPresent("password", "Password@123");
        fillFieldIfPresent("confirmPassword", "WrongPassword@123");
        fillFieldIfPresent("designation", "GSMB Officer Tester");

        uploadFileIfFieldExists("nicFront", nicFrontPath);
        uploadFileIfFieldExists("nicBack", nicBackPath);
        uploadFileIfFieldExists("workId", workIdPath);

        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
        submitButton.click();

        List<WebElement> errors = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".ant-form-item-explain-error")));
        boolean pwMismatchFound = false;
        for (WebElement error : errors) {
            String text = error.getText().toLowerCase();
            if (text.contains("password") && (text.contains("match") || text.contains("confirm"))) {
                pwMismatchFound = true;
                System.out.println("⚠️ Expected validation error found: " + error.getText());
            }
        }
        assert pwMismatchFound : "⚠️ Expected password mismatch validation error not found";
    }
}
