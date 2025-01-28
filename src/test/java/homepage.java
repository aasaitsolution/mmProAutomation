import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

public class homepage {
    private static final Log log = LogFactory.getLog(homepage.class);
    WebDriver driver;

    @Test
    public void openHomePage() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Open the URL
        driver.get("http://localhost:5173/");

    }

    @Test
    public void testServiceSection() throws InterruptedException{


        Thread.sleep(2000);
        WebElement serviceButton = driver.findElement(By.xpath("/html/body/div/div/header/div/div[2]/ul/li[2]"));
        WebElement serviceButton1 = serviceButton;
        serviceButton1.click();

        // Verify if the page scrolled to the "Service" section (replace 'service-section' with the correct ID or locator)
        WebElement serviceSection = driver.findElement(By.xpath("/html/body/div/div/div[2]/div[1]"));

        // Perform a check (optional) to verify that the section is visible
        if (serviceSection.isDisplayed()) {
            System.out.println("Successfully scrolled to the Service section.");
        }
        else {
            System.out.println("Failed to scroll to the Service section.");
        }

        // Pause for demonstration purposes (optional)
        Thread.sleep(2000);


    }

    @Test
    public void testAboutSection() throws InterruptedException{
        Thread.sleep(2000);
        WebElement aboutButton = driver.findElement(By.xpath("/html/body/div/div/header/div/div[2]/ul/li[3]"));
        WebElement aboutButton1 = aboutButton;
        aboutButton1.click();

        // Verify if the page scrolled to the "Service" section (replace 'about-section' with the correct ID or locator)
        WebElement aboutSection = driver.findElement(By.xpath("//*[@id=\"about\"]"));

        // Perform a check (optional) to verify that the section is visible
        if (aboutSection.isDisplayed()) {
            System.out.println("Successfully scrolled to the About section.");
        }
        else {
            System.out.println("Failed to scroll to the About section.");
        }

        // Pause for demonstration purposes (optional)
        Thread.sleep(2000);


    }

    @Test
    public void testContactSection() throws InterruptedException {
        Thread.sleep(2000);
        WebElement contactButton = driver.findElement(By.xpath("/html/body/div/div/header/div/div[2]/ul/li[4]"));
        WebElement contactButton1 = contactButton;
        contactButton1.click();

        // Verify if the page scrolled to the "Service" section (replace 'about-section' with the correct ID or locator)
        WebElement contactSection = driver.findElement(By.xpath("/html/body/div/div/footer"));

        // Perform a check (optional) to verify that the section is visible
        if (contactSection.isDisplayed()) {
            System.out.println("Successfully scrolled to the contact section.");
        } else {
            System.out.println("Failed to scroll to the contact section.");
        }

        // Pause for demonstration purposes (optional)
        Thread.sleep(2000);


    }
    @Test
    public void testTamilVersion() throws InterruptedException {
        // Click the Tamil button
        WebElement tamilButton = driver.findElement(By.xpath("/html/body/div/div/header/div/div[3]/button[1]")); // Update the XPath for your Tamil button
        tamilButton.click();

        // Verify the Tamil version by checking a specific element or text in Tamil
        WebElement tamilHeader = driver.findElement(By.xpath("/html/body/div/div")); // Update with a Tamil-specific element
        if (tamilHeader.isDisplayed()) {
            System.out.println("Successfully switched to the Tamil version.");
        } else {
            System.out.println("Failed to switch to the Tamil version.");
        }

        Thread.sleep(2000);
    }

    @Test
    public void testSinhalaVersion() throws InterruptedException {
        // Click the Sinhala button
        WebElement sinhalaButton = driver.findElement(By.xpath("/html/body/div/div/header/div/div[3]/button[2]")); // Update the XPath for your Sinhala button
        sinhalaButton.click();

        // Verify the Sinhala version by checking a specific element or text in Sinhala
        WebElement sinhalaHeader = driver.findElement(By.xpath("/html/body/div/div")); // Update with a Sinhala-specific element
        if (sinhalaHeader.isDisplayed()) {
            System.out.println("Successfully switched to the Sinhala version.");
        } else {
            System.out.println("Failed to switch to the Sinhala version.");
        }

        Thread.sleep(2000);
    }




    @Test
    public void testLoginButton() throws InterruptedException{
        WebElement loginButton = driver.findElement(By.xpath("/html/body/div/div/header/div/div[3]/a/button"));
        loginButton.click();

        driver.get("http://localhost:5173/signin");

        Thread.sleep(2000);
        driver.navigate().back();


    }

    @AfterTest
    public void tearDown() {
        // Close the browser
        if (driver != null) {
            driver.quit();
        }
    }






}





