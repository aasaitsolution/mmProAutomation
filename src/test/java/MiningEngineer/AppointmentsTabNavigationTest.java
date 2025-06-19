package MiningEngineer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

public class AppointmentsTabNavigationTest extends AppointmentsTestBase {

    @Test
    public void testSwitchToPendingTab() {
        // Click on the "Pending Scheduling" tab
        WebElement pendingTab = driver.findElement(
                By.xpath("//div[@data-tab-key='pending-scheduling']")
        );
        pendingTab.click();

        // Wait for the header to appear after tab switch
        WebElement pendingTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(., 'Pending Scheduling Appointments')]")
        ));

        // Assertion to confirm the tab content is shown
        assert pendingTitle.isDisplayed();
    }

}
/*
    @Test
    public void testSwitchToScheduledTab()s {
        WebElement scheduledTab = driver.findElement(By.xpath("//div[contains(@class, 'ant-tabs-tab') and contains(., 'Scheduled')]"));
        scheduledTab.click();

        WebElement scheduledTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(., 'Scheduled Appointments')]")
        ));
        assert scheduledTitle.isDisplayed();
    }

    @Test
    public void testSwitchToHoldTab() {
        WebElement holdTab = driver.findElement(By.xpath("//div[contains(@class, 'ant-tabs-tab') and contains(., 'On Hold')]"));
        holdTab.click();

        WebElement holdTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(., 'On Hold Appointments')]")
        ));
        assert holdTitle.isDisplayed();
    }
}
*/