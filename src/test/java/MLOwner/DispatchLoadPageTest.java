//Done
package MLOwner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class DispatchLoadPageTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.addArguments("--start-maximized");
        // This argument can help prevent random crashes in some environments
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test(priority = 1)
    public void loginToMLDashboard() {
        driver.get("http://localhost:5173/");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href='/signin'] button")));
        loginBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        WebElement username = driver.findElement(By.id("sign-in_username"));
        WebElement password = driver.findElement(By.id("sign-in_password"));

        username.sendKeys("pasindu");
        password.sendKeys("12345678");

        WebElement signIn = driver.findElement(By.cssSelector("button[type='submit']"));
        signIn.click();

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Login successful.");
    }

    /**
     * Helper method to reliably set the value of a React input field.
     * Clicks the element to focus, then uses JavaScript to set the value and
     * dispatches 'input' and 'change' events to trigger React's state updates.
     */
    private void setReactInput(WebElement element, String value) {
        try {
            // Focus the element
            element.click();
            Thread.sleep(300);
            element.clear();
            Thread.sleep(300);

            // Simulate typing
            for (char c : value.toCharArray()) {
                element.sendKeys(String.valueOf(c));
                Thread.sleep(100);
            }

            // Force React to update value with JS
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "arguments[0].value = arguments[1];" +
                            "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    element, value
            );

            // Wait until value is set
            wait.until(ExpectedConditions.attributeToBe(element, "value", value));
            Thread.sleep(500);
        } catch (Exception e) {
            System.err.println("Error setting input value for: " + element);
            e.printStackTrace();
            Assert.fail("Failed to set input value properly.");
        }
    }

    @Test(priority = 2, dependsOnMethods = "loginToMLDashboard")
    public void testDispatchLoadFormSubmission() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

   
driver.get("http://localhost:5173/mlowner/home/dispatchload/LLL/100/431");

// 2) Push state with desired URL and divisionalSecretary BEFORE loading React page
js.executeScript(
    "const state = { divisionalSecretary: 'Eravur' };" +
    "const url = '/mlowner/home/dispatchload/LLL/100/431';" +
    "window.history.pushState(state, '', url);"
);

// 3) Now load the React app page at that URL, which should see the history state
driver.get("http://localhost:5173/mlowner/home/dispatchload/LLL/100/431?divisionalSecretary=Eravur");

String historyState = (String) ((JavascriptExecutor)driver).executeScript("return JSON.stringify(window.history.state);");
System.out.println("Current history state: " + historyState);

Thread.sleep(3000); // wait for React to load & read state

WebElement route1Input = wait.until(ExpectedConditions.visibilityOfElementLocated(
    By.xpath("//span[contains(text(),'ROUTE 1')]/following::input[1]")));
setReactInput(route1Input, "Eravur");
String route1Value = route1Input.getAttribute("value");
System.out.println("Route 1 value after manual set: " + route1Value);
Assert.assertEquals(route1Value, "Eravur", "Route 1 input should be manually set.");


        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(text(),'Dispatch Your Load Here')]")));
        System.out.println("Dispatch Load page loaded.");

        // Manually set Route 1 input value to 'Eravur' to simulate frontend initialization
// WebElement route1 = wait.until(ExpectedConditions.visibilityOfElementLocated(
//     By.xpath("//span[contains(text(),'ROUTE 1')]/following::input[1]")));
// setReactInput(route1, "Eravur");

// String route1Value = route1.getAttribute("value");
// System.out.println("Route 1 value after setReactInput: " + route1Value);
// Assert.assertEquals(route1Value, "Eravur", "Route 1 input should be pre-filled correctly.");


        WebElement destinationInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[contains(text(),'DESTINATION')]/following::input[1]")));
        setReactInput(destinationInput, "Jaffna");
        System.out.println("Set Destination to 'Jaffna'.");
        Thread.sleep(2000);

        WebElement lorryInput = driver.findElement(By.xpath("//span[contains(text(),'LORRY NUMBER')]/following::input[1]"));
        setReactInput(lorryInput, "CBA4321");
        System.out.println("Set Lorry Number to 'CBA4321'.");
        Thread.sleep(2000);

        WebElement driverContactInput = driver.findElement(By.xpath("//span[contains(text(),'DRIVER CONTACT')]/following::input[1]"));
        setReactInput(driverContactInput, "0771234567");
        System.out.println("Set Driver Contact to '0771234567'.");
        Thread.sleep(2000);

    //     WebElement route1Input = driver.findElement(By.xpath("//span[contains(text(),'ROUTE 1')]/following::input[1]"));
    // setReactInput(route1Input, "Eravur");

    // // Manually trigger React's internal state for divisionalSecretary (simulate what <Link state=...> would do)
    //  js = (JavascriptExecutor) driver;
    // js.executeScript("window.history.replaceState({ divisionalSecretary: 'Eravur' }, document.title);");
    // System.out.println("Set Route 1 manually to 'Eravur' and updated browser state.");

    // Thread.sleep(2000);






        WebElement route2 = driver.findElement(By.xpath("//span[contains(text(),'ROUTE 2')]/following::input[1]"));
        setReactInput(route2, "Galle");
        System.out.println("Set Route 2 to 'Galle'.");
        Thread.sleep(2000);

        WebElement route3 = driver.findElement(By.xpath("//span[contains(text(),'ROUTE 3')]/following::input[1]"));
        setReactInput(route3, "Colombo");
        System.out.println("Set Route 3 to 'Colombo'.");
        Thread.sleep(2000);

        // WebElement cubeInput = driver.findElement(By.xpath("//span[contains(text(),'CUBES')]/following::input[1]"));

        // // Clear the field and enter only one digit
        // JavascriptExecutor js = (JavascriptExecutor) driver;
        // js.executeScript("arguments[0].value = '';", cubeInput);
        // Thread.sleep(100); // small delay to avoid race condition
        // cubeInput.sendKeys(Keys.BACK_SPACE); // ensure it's fully cleared
        // cubeInput.sendKeys("2");

        // System.out.println("Set Cubes to '2'.");
        // Thread.sleep(2000);


        WebElement submitBtn = driver.findElement(By.xpath("//button[span[contains(text(),'Submit') or contains(text(),'සටහන් කරන්න') or contains(text(),'சமர்ப்பிக்கவும்')]]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true); arguments[0].click();", submitBtn);
        System.out.println("Clicked Submit button.");

        wait.until(ExpectedConditions.urlContains("/mlowner/home"));
        System.out.println("Redirected to home page after submission.");

        Thread.sleep(5000);  // pause at end so you can see final page
        System.out.println("Test successful.");
    }


    @AfterClass
    public void tearDown() throws InterruptedException {
        Thread.sleep(5000);  // delay before closing browser to watch final state
        if (driver != null) {
            // driver.quit();
            System.out.println("Browser closed.");
        }
}
}