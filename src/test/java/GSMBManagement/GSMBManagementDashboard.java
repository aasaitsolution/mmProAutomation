package GSMBManagement;

import base.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.Assert;


import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GSMBManagementDashboard extends BaseTest {

//    WebDriver driver;
//    WebDriverWait wait;
//
//    @BeforeClass
//    public void setUp() {
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--incognito");
//        driver = new ChromeDriver(options);
//        driver.manage().window().maximize();
//        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//        System.out.println("🧪 Browser launched in incognito mode.");
//    }

    @Test(priority = 1)
    public void loginToDashboard() {
        driver.get("https://mmpro.aasait.lk/");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username"))).sendKeys("sunil");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password"))).sendKeys("12345678");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();

        System.out.println("🔐 Successfully logged in!");

        // Optional alert handling
        try {
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
        } catch (NoAlertPresentException ignored) {}
    }

    @Test(priority = 2, dependsOnMethods = "loginToDashboard")
    public void verifyDashboardLoad() {
        wait.until(ExpectedConditions.urlContains("/gsmbmanagement"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h3")));

        System.out.println("📊 Dashboard page loaded.");
    }

   @Test(priority = 3, dependsOnMethods = "verifyDashboardLoad")
    public void verifyKPICards() {
        List<String> expectedTitles = List.of(
            "Overall User Stats (ACTIVE)",
            "Total License Stats",
            "Complaint Stats",
            "Total Royalty"
        );

        // Wait for presence of at least one card to ensure page is loaded
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ant-card")));

        List<WebElement> allCards = driver.findElements(By.cssSelector(".ant-card"));
        Map<String, WebElement> foundCardsByTitle = new HashMap<>();

        for (WebElement card : allCards) {
            try {
                WebElement titleElement = card.findElement(By.tagName("h5"));
                String titleText = titleElement.getText().trim();
                if (expectedTitles.contains(titleText)) {
                    foundCardsByTitle.put(titleText, card);
                }
            } catch (NoSuchElementException e) {
                // Ignore cards without title h5
            }
        }

        System.out.println("🧾 KPI cards found with expected titles: " + foundCardsByTitle.keySet());

        // Assert that all expected titles were found exactly once
        Assert.assertEquals(foundCardsByTitle.size(), expectedTitles.size(), "Mismatch in number of KPI cards found");
        Assert.assertTrue(foundCardsByTitle.keySet().containsAll(expectedTitles), "Not all expected KPI card titles were found");
    }

    @Test(priority = 4, dependsOnMethods = "verifyDashboardLoad")
    public void verifyPieChartsPresence() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Wait until at least one SVG element (pie chart) is present
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("svg"), 0));

        List<WebElement> pies = driver.findElements(By.cssSelector("svg"));
        System.out.println("🥧 Pie charts found (SVG elements): " + pies.size());

        // Adjust the expected number as per your app, here I check for at least 1 pie chart
        assert pies.size() >= 1 : "Expected at least one pie chart (SVG element)";
    }

    @Test(priority = 5, dependsOnMethods = "verifyDashboardLoad")
    public void verifyMonthlySandCubesChart() {
        try {
            // Wait until the chart card title appears
            WebElement chartTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(@class,'ant-card')]//h5[contains(text(),'Monthly Sand Cubes Count')]")
            ));

            System.out.println("📊 Chart Title Found: " + chartTitle.getText());
            assert chartTitle.isDisplayed();

            // Check the presence of SVG chart container inside this card
            WebElement chartSvg = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div.recharts-responsive-container svg")
            ));

            System.out.println("✅ Monthly Sand Cubes Chart is rendered.");
            assert chartSvg.isDisplayed();

            // Wait until there is at least one X-axis tick label (months)
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector(".recharts-cartesian-axis.recharts-xAxis .recharts-cartesian-axis-tick"), 0));

            // Find all X-axis ticks
            List<WebElement> xAxisTicks = driver.findElements(
                By.cssSelector(".recharts-cartesian-axis.recharts-xAxis .recharts-cartesian-axis-tick"));

            System.out.println("🗓️ Months on X-axis: " + xAxisTicks.size());
            assert xAxisTicks.size() > 0 : "Expected at least one X-axis month label.";

        } catch (Exception e) {
            System.err.println("❌ Monthly Sand Cubes Chart test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 6, dependsOnMethods = "verifyMonthlySandCubesChart")
    public void verifyTopMiningLicenseHolders() {
        try {
            // Step 1: Wait for the card title to appear
            WebElement cardTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-card')]//h5[contains(text(),'Top Mining License Holders')]")
            ));
            System.out.println("🏗️ Card title found: " + cardTitle.getText());
            assert cardTitle.isDisplayed();

            // Step 2: Wait for at least one progress bar (license capacity)
            List<WebElement> progressBars = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector(".ant-progress-bg")
            ));

            System.out.println("⛏️ Mining License Holder entries: " + progressBars.size());
            assert progressBars.size() > 0 : "No mining license holders found.";

            // Step 3 (Optional): Print percentage text
            List<WebElement> percentTexts = driver.findElements(By.xpath("//span[contains(text(),'%')]"));
            for (WebElement text : percentTexts) {
                System.out.println("📈 Capacity: " + text.getText());
            }

        } catch (Exception e) {
            System.err.println("❌ Top Mining License Holders test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 7, dependsOnMethods = "verifyTopMiningLicenseHolders")
    public void verifyTopContributors() {
        try {
            // Wait for card title
            By cardTitleLocator = By.xpath("//div[contains(@class,'ant-card-head-title')]//span[contains(text(),'Top Royalty Contributors')]");
            WebElement cardTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(cardTitleLocator));
            System.out.println("💰 Card title found: " + cardTitle.getText());
            assert cardTitle.isDisplayed();

            // Wait for contributors list items
            By contributorsLocator = By.cssSelector("ul.ant-list-items > li.ant-list-item");
            List<WebElement> contributors = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(contributorsLocator));
            System.out.println("👤 Number of top contributors listed: " + contributors.size());
            assert contributors.size() > 0 : "No contributors listed in Top Contributors section.";

            // // Print contributor names
            // List<WebElement> names = driver.findElements(By.cssSelector("h4.ant-list-item-meta-title > span"));
            // for (WebElement name : names) {
            //     System.out.println("🏅 Contributor: " + name.getText());
            // }

        } catch (Exception e) {
            System.err.println("❌ Top Contributors test failed: " + e.getMessage());
            throw e;
        }
    }


    @Test(priority = 8, dependsOnMethods = "verifyTopContributors")
    public void verifyTransportLicenseChart() {
        try {
            // Wait for card title <h5>
            WebElement chartTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-card-body')]//h5[contains(text(),'Top Regions for Mining License Issuance')]")
            ));
            System.out.println("📊 Transport License Chart Title: " + chartTitle.getText());
            assert chartTitle.isDisplayed();

            // Wait for description <p>
            WebElement chartDescription = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-card-body')]//p[contains(text(),'Areas with the Highest Number of Approved Mining Licenses')]")
            ));
            System.out.println("📝 Description found: " + chartDescription.getText());
            assert chartDescription.isDisplayed();

            // Wait for at least one pie chart slice rendered
            List<WebElement> pieSlices = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("path.recharts-sector"), 0
            ));
            System.out.println("🥧 Pie chart sectors rendered: " + pieSlices.size());
            assert pieSlices.size() > 0 : "No data segments rendered in the pie chart.";

        } catch (Exception e) {
            System.err.println("❌ Transport License Chart Test Failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 9, dependsOnMethods = "verifyTransportLicenseChart")
    public void verifyTopDestinationsChart() {
        try {
            // Wait for the chart title <h5> inside .ant-card-body with exact text "Top Destinations"
            WebElement chartTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-card-body')]//h5[text()='Top Destinations']")
            ));
            System.out.println("📍 Top Destinations Chart Title: " + chartTitle.getText());
            assert chartTitle.isDisplayed();

            // Wait for description <p> with exact text (or you can use contains() if partial match preferred)
            WebElement chartDescription = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ant-card-body')]//p[text()='Permitted locations for transport operations under license.']")
            ));
            System.out.println("📝 Description found: " + chartDescription.getText());
            assert chartDescription.isDisplayed();

            // Wait for pie chart sectors with class 'recharts-sector' to be more than 0
            List<WebElement> pieSlices = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("path.recharts-sector"), 0
            ));
            System.out.println("🥧 Pie chart sectors rendered: " + pieSlices.size());
            assert pieSlices.size() > 0 : "No data segments found in Top Destinations pie chart.";

        } catch (Exception e) {
            System.err.println("❌ Top Destinations Chart Test Failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 10, dependsOnMethods = "verifyDashboardLoad")
    public void verifyMapComponent() {
        try {
            // Wait for the map card title to be visible
            WebElement mapTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h5[contains(text(),'Top Sand Mines')] | //h5[contains(text(),'වැලි කාන')] | //h5[contains(text(),'மணல் சுரங்கங்கள்')]")
            ));
            System.out.println("🗺️ MapComponent Title Found: " + mapTitle.getText());
            assert mapTitle.isDisplayed();

            // Wait for the Leaflet map container div (class 'leaflet-container')
            WebElement mapContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.leaflet-container")
            ));
            System.out.println("🗺️ Leaflet map container is visible.");
            assert mapContainer.isDisplayed();

            // Check that multiple markers are present (class 'leaflet-marker-icon')
            List<WebElement> markers = driver.findElements(By.cssSelector("img.leaflet-marker-icon"));
            System.out.println("📍 Number of map markers found: " + markers.size());
            assert markers.size() >= 10 : "Expected at least 10 markers on the map.";

            // Optional: Check for at least one popup tooltip element (leaflet-popup-content)
            // This element is only visible after click, so we just check it exists in DOM
            List<WebElement> popups = driver.findElements(By.cssSelector(".leaflet-popup-content"));
            System.out.println("🔍 Number of popup content elements found: " + popups.size());

        } catch (Exception e) {
            System.err.println("❌ MapComponent test failed: " + e.getMessage());
            throw e;
        }
    }

    @Test(priority = 11, dependsOnMethods = "verifyDashboardLoad")
    public void verifyMiningLicenseChart() {
        try {
            WebElement chartTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h5[contains(text(),'Monthly Mining License Issues Count') or contains(text(),'මාසික පතල් බලපත්‍ර') or contains(text(),'மாதாந்திர சுரங்க உரிமம்')]")
            ));
            System.out.println("📊 Chart Title Found: " + chartTitle.getText());
            assert chartTitle.isDisplayed();

            WebElement hintText = driver.findElement(By.xpath(
                "//p[contains(text(),'Overall Mining License issues have increased') or contains(text(),'මුළු පතල් බලපත්‍ර නිකුතුව වැඩි වී ඇත') or contains(text(),'மொத்த சுரங்க உரிமம் பிரச்சினைகள் அதிகரித்துள்ளன')]"
            ));
            System.out.println("ℹ️ Chart hint text found: " + hintText.getText());
            assert hintText.isDisplayed();

            WebElement svgChart = driver.findElement(By.cssSelector("div.recharts-wrapper svg"));
            System.out.println("📈 Bar chart SVG container found.");
            assert svgChart.isDisplayed();

            // Updated selector to match path elements representing bars
            List<WebElement> bars = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("div.recharts-wrapper svg g.recharts-bar-rectangle path.recharts-rectangle"), 0
            ));
            System.out.println("📊 Number of bars found: " + bars.size());
            assert bars.size() > 0 : "Expected some bars in the chart.";

        } catch (Exception e) {
            System.err.println("❌ MiningLicenseChart test failed: " + e.getMessage());
            throw e;
        }
    }

    private static final String BASE_URL = "https://mmpro.aasait.lk";

    @Test(priority = 12, dependsOnMethods = {"loginToDashboard"})
    public void clickActivation() throws InterruptedException {

        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll down to bottom of page
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        System.out.println("⬇️ Scrolled to bottom of the page.");
        Thread.sleep(2000);  // Wait for any lazy loading

        // Scroll back up to top of page
        js.executeScript("window.scrollTo(0, 0);");
        System.out.println("⬆️ Scrolled back to top of the page.");
        Thread.sleep(2000);  // Wait for UI to settle

        WebElement activationBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//span[text()='Activation']]")));
        activationBtn.click();
        System.out.println("🚀 Activation button clicked.");

        Thread.sleep(1000); // allow for animation
        System.out.println("📍 Current URL: " + driver.getCurrentUrl());
    }

   @Test(priority = 13, dependsOnMethods = "loginToDashboard")
    public void verifyOfficerActivationPage() {
        waitForOfficerActivationPageToLoad();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(),'Officer Activation')]")));
        System.out.println("🎯 Officer Activation page loaded successfully.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//button[span[text()='Activate']]")));
        System.out.println("📄 Officer table loaded with action buttons.");
    }

    public void waitForOfficerActivationPageToLoad() {
    wait.until(ExpectedConditions.urlContains("/gsmbmanagement"));

    try {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(),'Officer Activation')]")));
        System.out.println("🟢 Officer Activation page is fully loaded.");
    } catch (TimeoutException e) {
        System.out.println("❌ Timeout: Officer Activation page did not load.");
        throw e;
    }
}

    @AfterClass
    public void tearDown() {
        try {
            Thread.sleep(5000); // for observation
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();
        System.out.println("🛑 Browser closed. Test complete.");
    }
}