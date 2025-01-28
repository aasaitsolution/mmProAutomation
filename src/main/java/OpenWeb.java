//import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class OpenWeb {

    public static void main(String[] args) {


        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver();

        // Open Google
        driver.get("http://localhost:5173/");

        // Optionally, add a wait to observe the browser, then close it
        try {
            Thread.sleep(3000);  // Wait for 3 seconds to let the page load
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Close the browser
        driver.quit();
    }
}
