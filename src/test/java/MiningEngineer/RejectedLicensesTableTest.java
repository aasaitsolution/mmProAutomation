package MiningEngineer;

import MiningEngineer.RejectedLicensesTablePage;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class RejectedLicensesTableTest extends AppointmentsTestBase {

    RejectedLicensesTablePage page;

    @BeforeClass
    public void setUpPageObject() {
        page = new RejectedLicensesTablePage(driver);
    }

    @Test(priority = 1)
    public void testTableIsVisible() {
        page.goToRejectedLicensesTab(); // ⬅️ navigate first
        Assert.assertTrue(page.isTableVisible(), "Table should be visible on the page");
    }

    @Test(priority = 2)
    public void testAllStatusesAreRejected() {
        page.goToRejectedLicensesTab();
        List<WebElement> rejectedTags = page.getLicenseTags();
        for (WebElement tag : rejectedTags) {
            Assert.assertTrue(tag.getText().equalsIgnoreCase("Rejected") || tag.getText().contains("நிராகரிக்கப்பட்டது"));
        }
    }

    @Test(priority = 3)
    public void testGoogleMapLinksWork() {
        page.goToRejectedLicensesTab();
        WebElement link = page.getGoogleMapLink(0);
        Assert.assertTrue(link.getAttribute("href").contains("google.com/maps"), "Link should point to Google Maps");
    }

    @Test(priority = 4)
    public void testViewDetailsButtonWorks() {
        page.goToRejectedLicensesTab();
        int size = page.getViewDetailsButtons().size();
        Assert.assertTrue(size > 0, "There should be at least one View Details button");
        page.clickViewDetails(0);
        // Optional: add assertion for modal
    }

    @Test(priority = 5)
    public void testTamilLanguageSwitch() {
        page.goToRejectedLicensesTab();
        page.changeLanguage("ta");
        Assert.assertTrue(page.isTamilLanguageUsed(), "Tamil headers should be visible after switching language");
    }

    @Test(priority = 6)
    public void testEnglishLanguageSwitch() {
        page.goToRejectedLicensesTab();
        page.changeLanguage("en");
        Assert.assertFalse(page.isTamilLanguageUsed(), "English headers should be visible after switching language");
    }
}
