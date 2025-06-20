package GSMBManagement;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class ActivationPageTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Helper method to wait for the first valid data row (non-empty, not "No data")
    private WebElement waitForFirstValidRow() {
        return wait.until(driver -> {
            List<WebElement> bodies = driver.findElements(By.cssSelector(".ant-table-tbody"));
            for (WebElement body : bodies) {
                if (body.isDisplayed()) {
                    List<WebElement> rows = body.findElements(By.cssSelector("tr"));
                    for (WebElement row : rows) {
                        String text = row.getText().trim().toLowerCase();
                        if (!text.contains("no data") && !text.isEmpty()) {
                            return row;
                        }
                    }
                }
            }
            return null; // keep polling until timeout
        });
    }

    @Test(priority = 1)
    public void login() {
        driver.get("http://localhost:5173/signin");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username"))).sendKeys("sunil");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password"))).sendKeys("12345678");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        System.out.println("Login successful, dashboard loaded.");
    }

    @Test(priority = 2, dependsOnMethods = {"login"})
    public void navigateToActivation() {
        WebElement activationBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Activation']]")));
        activationBtn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Officer Activation')]")));
        System.out.println("Navigated to Activation page.");
    }

    @Test(priority = 3, dependsOnMethods = {"navigateToActivation"})
public void verifyTabsAndActivateAll() {
    String[] expectedTabLabels = {
        "Police Officers",
        "GSMB Officers",
        "Mining Engineer"
    };

    for (String tabLabel : expectedTabLabels) {
        try {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[contains(@class,'ant-tabs-tab') and normalize-space(text())='" + tabLabel + "']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", tab);
        wait.until(ExpectedConditions.elementToBeClickable(tab));
        Thread.sleep(500);
        tab.click();

        System.out.println("\nSwitched to tab: " + tabLabel);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".ant-spin")));

try {
    WebElement firstRow = waitForFirstValidRow();
    System.out.println("First valid row found: " + firstRow.getText());
    // Continue with activation logic below...
} catch (TimeoutException e) {
    System.out.println("No data found in tab: " + tabLabel);
    continue; // Skip to next tab
}
        boolean rowFound = false;

        // Try multiple times in case of stale element
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                List<WebElement> bodies = driver.findElements(By.cssSelector(".ant-table-tbody"));

                for (WebElement body : bodies) {
                    if (body.isDisplayed()) {
                        List<WebElement> rows = body.findElements(By.cssSelector("tr"));

                        for (WebElement row : rows) {
                            String rowText = row.getText().trim().toLowerCase();
                            if (!rowText.contains("no data") && !rowText.isEmpty()) {
                                List<WebElement> buttons = row.findElements(By.xpath(
                                    ".//*[contains(translate(text(),'ACTIVATE','activate'),'activate') or contains(translate(text(),'DEACTIVATE','deactivate'),'deactivate')]"));

                                if (!buttons.isEmpty()) {
                                    WebElement actionElement = buttons.get(0);
                                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", actionElement);
                                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", actionElement);
                                    wait.until(ExpectedConditions.elementToBeClickable(actionElement));
                                    Thread.sleep(500); // give it time to stabilize
                                    actionElement.click();

                                    WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                                        By.cssSelector(".ant-modal-confirm")));
                                    WebElement modalTitle = modal.findElement(By.cssSelector(".ant-modal-confirm-title"));
                                    System.out.println("Modal title: " + modalTitle.getText());

                                    WebElement okBtn = modal.findElement(By.cssSelector(".ant-modal-confirm-btns button.ant-btn-primary"));
                                    okBtn.click();

                                    wait.until(ExpectedConditions.invisibilityOf(modal));
                                    System.out.println("Activated/Deactivated officer in tab: " + tabLabel);

                                    Thread.sleep(2000); // Wait for table refresh

                                    rowFound = true;
                                    break; // out of row loop
                                }
                            }
                        }
                    }

                    if (rowFound) break; // out of tbody loop
                }

                break; // break retry loop if successful

            } catch (StaleElementReferenceException stale) {
                System.out.println("Stale element encountered in tab: " + tabLabel + " — retrying...");
                Thread.sleep(1000); // small delay before retry
            }
        }

        if (!rowFound) {
            System.out.println("No activatable officers found in tab: " + tabLabel);
        }

    } catch (Exception e) {
        System.out.println("Failed to load or activate tab: " + tabLabel);
        e.printStackTrace();
    }
}
}
    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
