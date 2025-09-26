package Home;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.search.FlagTerm;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Resetpassword extends BaseTest {

    private static final String BASE_URL = "https://mmpro.aasait.lk/";
    private String resetLink;

    @BeforeMethod
    public void navigateToHomepage() {
        driver.get(BASE_URL);
        waitForPageLoadComplete();

        // Verify homepage elements are present
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//button[.//span[contains(text(),'தமிழ்')]]")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//span[text()='සිංහල']/..")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'logo')] | //img[contains(@alt, 'logo')]"))
        ));
        System.out.println("✅ Homepage loaded successfully for reset password tests.");
    }

    private void clickElementSafely(By locator, String elementName) throws InterruptedException {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        wait.until(ExpectedConditions.elementToBeClickable(element));

        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }

        Thread.sleep(1000);
        System.out.println("✅ Clicked: " + elementName);
    }

    @Test(priority = 1)
    public void testRequestPasswordReset() throws InterruptedException {
        try {
            System.out.println("🌐 Opened page: " + driver.getTitle());

            // Navigate to login page
            By loginBtn = By.xpath("//a/button[.//span[contains(text(),'Login') or contains(text(),'உள்நுழைய') or contains(text(),'පිවිසුම')]] | //a[contains(@href,'/signin')]");
            clickElementSafely(loginBtn, "Login button");

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/signin"),
                    ExpectedConditions.presenceOfElementLocated(By.id("sign-in_username"))
            ));
            System.out.println("➡️ Navigated to login page");

            // Click forgot password
            WebElement forgotPasswordBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".links")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", forgotPasswordBtn);
            System.out.println("🔑 Clicked 'Forgot Password'");

            // Enter email
            WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".fp-modal input[type='text']")));
            emailInput.clear();
            emailInput.sendKeys("frtestemailuse@gmail.com");
            System.out.println("✉️ Entered email");

            // Submit request
            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".fp-modal .submit-button")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
            System.out.println("📤 Submitted password reset request");

            // Wait for confirmation
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".fp-modal")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".confirmation-modal")));
            System.out.println("✅ Confirmation modal displayed");

        } catch (Exception e) {
            System.err.println("❌ Failed at requesting password reset: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 2, dependsOnMethods = "testRequestPasswordReset")
    public void testFetchResetLinkFromEmail() throws Exception {
        try {
            resetLink = getResetLinkFromGmailWithRetry(5, 5);
            System.out.println("📩 Reset link fetched: " + resetLink);

            String token = resetLink.substring(resetLink.indexOf("token=") + 6);
            System.out.println("🔑 Extracted token: [" + token + "]");
        } catch (Exception e) {
            System.err.println("❌ Failed to fetch reset link: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 3, dependsOnMethods = "testFetchResetLinkFromEmail")
    public void testResetPasswordWithToken() throws InterruptedException {
        try {
            driver.get(resetLink);
            waitForPageLoadComplete();
            System.out.println("🔗 Navigated to reset password page");

            WebElement newPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newPassword")));
            WebElement confirmPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmPassword")));

            newPasswordInput.clear();
            newPasswordInput.sendKeys("NewPassword123");
            confirmPasswordInput.clear();
            confirmPasswordInput.sendKeys("NewPassword123");
            System.out.println("🔐 Entered new password");

            WebElement resetSubmitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".reset-password-modal .submit-button")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", resetSubmitButton);
            System.out.println("🚀 Submitted password reset form");

            Thread.sleep(5000); // Reduced wait time

            wait.until(ExpectedConditions.urlContains("/signin"));
            Assert.assertTrue(driver.getCurrentUrl().contains("signin"), "❌ Password reset failed - not redirected to login.");
            System.out.println("✅ Password reset successful and redirected to sign-in");
        } catch (Exception e) {
            System.err.println("❌ Failed at resetting password: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 4)
    public void testRequestPasswordResetWithInvalidEmail() throws InterruptedException {
        try {
            driver.get(BASE_URL + "signin");
            waitForPageLoadComplete();

            WebElement forgotPasswordBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".links")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", forgotPasswordBtn);

            WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".fp-modal input[type='text']")));
            emailInput.clear();
            emailInput.sendKeys("invalid-email-format");
            System.out.println("✉️ Entered invalid email");

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".fp-modal .submit-button")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
            System.out.println("📤 Submitted password reset request with invalid email");

            // Wait and check for error message
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("#email_help .ant-form-item-explain-error")
            ));
            String errorText = errorMsg.getText();
            Assert.assertTrue(errorText.contains("Please enter a valid email!"), "❌ Invalid email error not displayed properly.");
            System.out.println("❗ Proper error message displayed for invalid email");

        } catch (Exception e) {
            System.err.println("❌ Failed invalid email test: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 5)
    public void testRequestPasswordResetWithUnregisteredEmail() throws InterruptedException {
        try {
            driver.get(BASE_URL + "signin");
            waitForPageLoadComplete();

            WebElement forgotPasswordBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".links")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", forgotPasswordBtn);

            WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".fp-modal input[type='text']")));
            emailInput.clear();
            emailInput.sendKeys("unregisteredemail@example.com");
            System.out.println("✉️ Entered unregistered email");

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".fp-modal .submit-button")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
            System.out.println("📤 Submitted password reset request with unregistered email");

            // Wait for the AntD error toast message
            WebElement toastMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-message-error span:last-of-type")));

            String msgText = toastMsg.getText().trim().toLowerCase();
            System.out.println("🔔 Toast message received: " + msgText);

            Assert.assertTrue(msgText.contains("no user found for this email"), "❌ Unregistered email error not displayed properly.");
            System.out.println("❗ Proper toast error displayed for unregistered email");

        } catch (Exception e) {
            System.err.println("❌ Failed unregistered email test: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 6)
    public void testFetchResetLinkTimeout() {
        try {
            // Try fetching reset link with very few retries and short wait
            getResetLinkFromGmailWithRetry(2, 2);
            Assert.fail("Expected exception due to missing reset email, but got none");
        } catch (Exception e) {
            System.out.println("⌛ Expected failure occurred while fetching reset link: " + e.getMessage());
            Assert.assertTrue(e.getMessage().contains("Failed to get reset link"), "❌ Expected timeout error not received.");
        }
    }

    @Test(priority = 7)
    public void testResetPasswordWithInvalidToken() throws InterruptedException {
        try {
            String invalidResetLink = BASE_URL + "reset-password?token=invalidtoken123456";
            driver.get(invalidResetLink);
            waitForPageLoadComplete();
            System.out.println("🔗 Navigated to reset password page with invalid token");

            // Wait for password fields to appear
            WebElement newPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newPassword")));
            WebElement confirmPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmPassword")));

            // Enter any passwords
            newPasswordInput.clear();
            newPasswordInput.sendKeys("TestPassword123");
            confirmPasswordInput.clear();
            confirmPasswordInput.sendKeys("TestPassword123");
            System.out.println("🔐 Entered passwords");

            // Click reset button
            WebElement resetSubmitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".reset-password-modal .submit-button")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", resetSubmitButton);
            System.out.println("🚀 Submitted reset password form with invalid token");

            // Wait for AntD error message toast
            WebElement toastMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-message-error span:last-of-type")));

            String msgText = toastMsg.getText().trim().toLowerCase();
            System.out.println("🔔 Toast message received: " + msgText);

            Assert.assertTrue(msgText.contains("invalid") || msgText.contains("expired"),
                    "❌ Invalid token error not displayed properly.");
            System.out.println("❗ Proper error toast displayed for invalid/expired token");

        } catch (Exception e) {
            System.err.println("❌ Failed invalid token test: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 8)
    public void testResetPasswordWithMismatchedPasswords() throws InterruptedException {
        try {
            // Skip if resetLink is null
            if (resetLink == null) {
                System.out.println("⚠️ Skipping mismatched passwords test as resetLink is null");
                return;
            }

            driver.get(resetLink);
            waitForPageLoadComplete();
            System.out.println("🔗 Navigated to reset password page for mismatched passwords test");

            WebElement newPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newPassword")));
            WebElement confirmPasswordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("confirmPassword")));

            newPasswordInput.clear();
            newPasswordInput.sendKeys("Password123");
            confirmPasswordInput.clear();
            confirmPasswordInput.sendKeys("DifferentPassword123");
            System.out.println("🔐 Entered mismatched passwords");

            WebElement resetSubmitButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".reset-password-modal .submit-button")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", resetSubmitButton);

            // Wait for validation error message
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector(".ant-form-item-explain-error")
            ));
            String errorText = errorMsg.getText().toLowerCase();
            Assert.assertTrue(errorText.contains("passwords do not match") || errorText.contains("the two passwords do not match"),
                    "❌ Mismatched password error not displayed properly.");
            System.out.println("❗ Proper validation error displayed for mismatched passwords");
        } catch (Exception e) {
            System.err.println("❌ Failed mismatched passwords test: " + e.getMessage());
            throw e;
        }
    }

    private void waitForPageLoadComplete() {
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(30)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete"));
    }

    // Email fetching methods remain the same
    public String getResetLinkFromGmail() throws Exception {
        String host = "imap.gmail.com";
        String username = "frtestemailuse@gmail.com";
        String password = "zhgp ichx lbim pvch"; // Google App Password

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");

        Session session = Session.getDefaultInstance(properties);
        Store store = session.getStore("imaps");
        store.connect(host, username, password);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);

        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        System.out.println("📬 Unread messages count: " + messages.length);

        for (int i = messages.length - 1; i >= 0; i--) {
            Message message = messages[i];
            if (message.getSubject() != null && message.getSubject().contains("Password Reset Request")) {
                String content = "";

                Object msgContent = message.getContent();
                if (msgContent instanceof String) {
                    content = (String) msgContent;
                } else if (msgContent instanceof Multipart) {
                    Multipart multipart = (Multipart) msgContent;
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        if (bodyPart.getContentType().toLowerCase().contains("text/plain") ||
                                bodyPart.getContentType().toLowerCase().contains("text/html")) {
                            content = bodyPart.getContent().toString();
                            break;
                        }
                    }
                }

                if (!content.isEmpty()) {
                    Pattern pattern = Pattern.compile("https?://[^\\s\"<>]+reset-password\\?token=[^\\s\"<>]+");
                    Matcher matcher = pattern.matcher(content);

                    if (matcher.find()) {
                        String link = matcher.group();
                        System.out.println("🔗 Reset link found: " + link);
                        message.setFlag(Flags.Flag.SEEN, true);
                        inbox.close(false);
                        store.close();
                        return link;
                    }
                }
            }
        }

        inbox.close(false);
        store.close();
        throw new Exception("❌ Reset password email not found!");
    }

    public String getResetLinkFromGmailWithRetry(int maxRetries, int waitSeconds) throws Exception {
        for (int i = 0; i < maxRetries; i++) {
            try {
                String link = getResetLinkFromGmail();
                System.out.println("📩 Got reset link on try " + (i + 1));
                return link;
            } catch (Exception e) {
                System.out.println("⌛ Attempt " + (i + 1) + " failed: " + e.getMessage());
                if (i < maxRetries - 1) Thread.sleep(waitSeconds * 1000L);
            }
        }
        throw new Exception("❌ Failed to get reset link after " + maxRetries + " attempts");
    }
}