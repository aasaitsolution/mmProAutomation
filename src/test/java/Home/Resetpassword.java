package Home;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

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



public class Resetpassword {

    public String getResetLinkFromGmail() throws Exception {
        String host = "imap.gmail.com";
        String username = "frtestemailuse@gmail.com";
        String password = "zhgp ichx lbim pvch"; // App password from Google

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

        // Search for UNSEEN emails only
        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
        System.out.println("Unread messages count: " + messages.length);

        for (int i = messages.length - 1; i >= 0; i--) {
            Message message = messages[i];
            System.out.println("Checking message: " + message.getSubject());

            if (message.getSubject() != null && message.getSubject().contains("Password Reset Request")) {
                String content = "";

                Object msgContent = message.getContent();
                if (msgContent instanceof String) {
                    content = (String) msgContent;
                } else if (msgContent instanceof Multipart) {
                    Multipart multipart = (Multipart) msgContent;
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        if (bodyPart.getContentType().toLowerCase().contains("text/plain")) {
                            content = bodyPart.getContent().toString();
                            break;
                        }
                    }
                } else {
                    System.out.println("Message content type unknown: " + msgContent.getClass());
                }

                if (content.isEmpty()) {
                    System.out.println("No content found in message!");
                    continue;
                }

                Pattern pattern = Pattern.compile("https?://[^\\s\"<>]+reset-password\\?token=[^\\s\"<>]+");
                Matcher matcher = pattern.matcher(content);

                if (matcher.find()) {
                    String link = matcher.group();
                    System.out.println("✅ Reset link found: " + link);

                    // Mark message as SEEN now
                    message.setFlag(Flags.Flag.SEEN, true);

                    inbox.close(false);
                    store.close();
                    return link;
                } else {
                    System.out.println("No reset link found in message content!");
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
                System.out.println("Got reset link on try " + (i + 1) + ": " + link);
                return link;
            } catch (Exception e) {
                System.out.println("Attempt " + (i + 1) + " failed: " + e.getMessage());
                if (i < maxRetries - 1) {
                    Thread.sleep(waitSeconds * 1000L);
                }
            }
        }
        throw new Exception("Failed to get reset link after " + maxRetries + " attempts");
    }



    @Test
public void resetPassword() {
    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized");
    WebDriver driver = new ChromeDriver(options);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

    try {
        // STEP 1: Open the home page
        driver.get("http://localhost:5173/");
        System.out.println("Page title: " + driver.getTitle());
        Thread.sleep(1000);  // optional manual wait to observe page

        // STEP 2: Click Sign In
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")
        ));
        loginButton.click();

        
        // STEP 3: Click on "Forgot Password?"
        WebElement forgotPasswordBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector(".links")
        ));
        forgotPasswordBtn.click();

        // STEP 4: Wait for Forgot Password modal and input email
        WebElement emailInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".fp-modal input[type='text']"))
        );
        emailInput.sendKeys("frtestemailuse@gmail.com");

        // STEP 5: Click Submit button
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".fp-modal .submit-button")));
        submitButton.click();

        // After Forgot Password modal closes
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".fp-modal")));
        System.out.println("Forgot Password modal closed");

        // Debug print modals before wait
        List<WebElement> existingModals = driver.findElements(By.cssSelector(".confirmation-modal"));
        System.out.println("Confirmation modals found before wait: " + existingModals.size());

        // Wait specifically for confirmation modal with custom class
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".confirmation-modal")));
        System.out.println("✅ Confirmation modal displayed");


// STEP 6: Click the "Open Email Inbox" button in the modal
            WebElement openEmailButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".confirmation-modal .submit-button")
            ));
            openEmailButton.click();

            // STEP 7: Switch to the new tab/window opened by clicking the button
            String originalWindow = driver.getWindowHandle();
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    System.out.println("Switched to new window/tab: " + windowHandle);
                    break;
                }
            }

            // Here you can add waits or interactions in the new tab if needed
            // Usually, opening Gmail in a new tab is manual and Selenium can't automate reading the email content there

            // STEP 8: Get the reset password link from email directly via IMAP (no need to navigate manually)
            String resetLink = getResetLinkFromGmailWithRetry(5, 10);
            System.out.println("Opening reset link: " + resetLink);
            String token = resetLink.substring(resetLink.indexOf("token=") + 6);
System.out.println("Using token: [" + token + "]");

            // STEP 9: Open the reset password link
            driver.get(resetLink);


            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".reset-password-modal")));
            // STEP 8: Fill in new password and confirm password
            // STEP 8: Fill in new password and confirm password
WebElement newPasswordInput = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.id("newPassword"))
);
WebElement confirmPasswordInput = wait.until(
        ExpectedConditions.visibilityOfElementLocated(By.id("confirmPassword"))
);


            // STEP 10: Fill new password
            newPasswordInput.sendKeys("NewPassword123");
            confirmPasswordInput.sendKeys("NewPassword123");

            // STEP 11: Submit new password
            WebElement resetSubmitButton = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector(".reset-password-modal .submit-button"))
            );
            resetSubmitButton.click();

            // STEP 12: Wait for redirection or success confirmation
            wait.until(ExpectedConditions.urlContains("/signin"));
            System.out.println("✅ Password reset completed and redirected to sign-in");


    } catch (Exception e) {
        System.err.println("❌ Test failed: " + e.getMessage());
    } finally {
        // driver.quit(); // quit only once at the end
    }
}

    
}
