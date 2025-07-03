package MiningEngineer;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ApprovedLicensesTablePage {
    private WebDriver driver;
    private WebDriverWait wait;

    public ApprovedLicensesTablePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void goToApprovedLicensesTab() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement approvedTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'me-tab-label') and (contains(., 'Approved Licenses') or contains(., 'அங்கீகரிக்கப்பட்ட உரிமங்கள்'))]")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", approvedTab);
        approvedTab.click();

        // Wait for the table wrapper (or any known unique element inside the tab) to become visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".ant-table-wrapper")
        ));
    }

    public boolean isTableVisible() {
        try {
            WebElement table = driver.findElement(
                    By.xpath("//div[contains(@class,'ant-tabs-tabpane-active')]//div[contains(@class,'ant-table-wrapper')]")
            );
            return table.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public List<WebElement> getLicenseTags() {
        return driver.findElements(By.cssSelector(".ant-tag-red"));
    }

    public List<WebElement> getViewDetailsButtons() {
        return driver.findElements(By.xpath("//button[span[text()='View Details'] or span[text()='விவரங்களைக் காட்டு']]"));
    }

    public WebElement getGoogleMapLink(int rowIndex) {
        return driver.findElements(By.xpath("//a[contains(@href,'google.com/maps')]")).get(rowIndex);
    }

    public void clickViewDetails(int index) {
        getViewDetailsButtons().get(index).click();
    }

    public boolean isTamilLanguageUsed() {
        return driver.findElement(By.xpath("//thead//th[1]")).getText().contains("அனுமதி");
    }

    public void changeLanguage(String langCode) {
        WebElement langDropdown = driver.findElement(By.id("language-selector")); // Replace with correct selector
        langDropdown.click();
        driver.findElement(By.xpath("//li[text()='" + (langCode.equals("ta") ? "தமிழ்" : "English") + "']")).click();
    }
}
