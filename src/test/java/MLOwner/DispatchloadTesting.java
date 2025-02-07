package MLOwner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.*;
import static org.testng.Assert.*;


import java.time.Duration;
import java.util.List;

public class DispatchloadTesting {
    private WebDriver driver;
    private WebDriverWait wait;


    @BeforeMethod
    public void setUp() {
        // Set up ChromeDriver path if not in system PATH
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");

        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("http://localhost:5173/");
    }

    @Test
    public void testDispatchLoadPage() throws InterruptedException {

        // Wait and click on the 'Login' button
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/signin'] button")));
        loginButton.click();

        // Wait for the Sign-in page to load and the username field to be visible
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_username")));
        usernameField.sendKeys("pasindu");

        // Wait for the password field to be visible
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-in_password")));
        passwordField.sendKeys("12345678");

        // Wait and click on the 'Sign in' button
        WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']")));
        signInButton.click();

        WebElement dispatchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='Dispatch Load']]")
        ));
        dispatchButton.click();
        Thread.sleep(2000);
        //click history and back to the home

        WebElement tamilButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='தமிழ்']]")
        ));
        tamilButton.click();
        // Wait for 10 seconds after clicking the button
        Thread.sleep(2000);

        // After 10 seconds, click the same button again
        tamilButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[span[text()='English']]")));
        tamilButton.click();


        WebElement sinhalaButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[span[text()='සිංහල']]")
        ));
        sinhalaButton.click();
        // Wait for 10 seconds after clicking the button
        Thread.sleep(2000);

        // After 10 seconds, click the same button again
        sinhalaButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[span[text()='English']]")));
        sinhalaButton.click();



        // Test the destination input
        WebElement destinationInput = driver.findElement(By.cssSelector(".ant-select-selection-search-input"));
        destinationInput.sendKeys("Colombo");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-select-item-option")));
        driver.findElement(By.cssSelector(".ant-select-item-option")).click();

        // Test the lorry number input
        WebElement lorryNumberInput = driver.findElement(By.xpath("//*[@id='root']/div/main/div/main/div[4]/div/div/input"));
        lorryNumberInput.sendKeys("ABC123");

        // Test the driver contact input
        WebElement driverContactInput = driver.findElement(By.xpath("//*[@id=\"root\"]/div/main/div/main/div[5]/div/div/input"));
        driverContactInput.sendKeys("1234567890");

// Test the due date input
        WebElement dueDateInput = driver.findElement(By.cssSelector(".ant-picker-input input"));
        dueDateInput.click();
        Thread.sleep(1000);

// Select a future date (e.g., next day)
        List<WebElement> futureDates = driver.findElements(By.cssSelector(".ant-picker-cell-inner"));
        futureDates.get(futureDates.size() - 1).click(); // Select the last available future date

        // Test the cubes input
        WebElement cubesInput = driver.findElement(By.cssSelector("input[value='1']"));
        driver.findElement(By.xpath("/html/body/div/div/main/div/main/div[7]/div/div/div/button[2]")).click();
        assertEquals(cubesInput.getAttribute("value"), "2", "Cubes should be incremented");

        // Submit the form
        WebElement submitButton = driver.findElement(By.xpath("/html/body/div[1]/div/main/div/main/div[8]/div/button[2]"));
        submitButton.click();
        Thread.sleep(4000);
        driver.navigate().back();

        Thread.sleep(8000);

//        WebElement dispatchButton1 = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("/html/body/div/div/main/div/div/div[2]/div/div/div/div/div/table/tbody/tr[3]/td[11]/div/div[1]/a/button")
//        ));
//        dispatchButton1.click();
//        Thread.sleep(2000);
//
//        // Test the destination input
//        WebElement destinationInput1 = driver.findElement(By.cssSelector(".ant-select-selection-search-input"));
//        destinationInput1.sendKeys("Colombo");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-select-item-option")));
//        driver.findElement(By.cssSelector(".ant-select-item-option")).click();
//        Thread.sleep(2000);
//
//        // Test the lorry number input
//        WebElement lorryNumberInput1 = driver.findElement(By.id("lorryNumber"));
//        lorryNumberInput1.sendKeys("ABC123");
//        Thread.sleep(2000);
//
//        // Test the driver contact input
//        WebElement driverContactInput1 = driver.findElement(By.id("drivercontact"));
//        driverContactInput1.sendKeys("1234567890");
//        Thread.sleep(2000);
//
////         Test the due date input
//        WebElement dueDateInput1 = driver.findElement(By.cssSelector(".ant-picker-input input"));
//        dueDateInput1.click();
//        Thread.sleep(1000);
//        driver.findElement(By.cssSelector(".ant-picker-cell-inner")).click();
//        Thread.sleep(1000);
////
//
////
//
////
//        // Test the cubes input
//        WebElement cubesInput1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[value='1']")));
//        cubesInput1.sendKeys("10000000");
////        WebElement cubesInput1 = driver.findElement(By.cssSelector("input[value='1']"));
////        driver.findElement(By.xpath("/html/body/div/div/main/div/main/div[7]/div/div/div/button[2]")).click();
////        assertEquals(cubesInput1.getAttribute("value"), "10000000", "Cubes should be incremented");
//        Thread.sleep(1000);
////
//        // Submit the form
//        WebElement submitButton1 = driver.findElement(By.xpath("/html/body/div[1]/div/main/div/main/div[8]/div/button[2]"));
//        submitButton1.click();
//
//        Thread.sleep(4000);
//        driver.navigate().back();
//
//        Thread.sleep(8000);
//
//        WebElement dispatchButton2 = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("/html/body/div/div/main/div/div/div[2]/div/div/div/div/div/table/tbody/tr[3]/td[11]/div/div[1]/a/button")
//        ));
//        Thread.sleep(1000);
//        dispatchButton2.click();
//        Thread.sleep(2000);
//
//        // Test the destination input
//        WebElement destinationInput2 = driver.findElement(By.cssSelector(".ant-select-selection-search-input"));
//        destinationInput2.sendKeys("Colombo");
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-select-item-option")));
//        driver.findElement(By.cssSelector(".ant-select-item-option")).click();
//        Thread.sleep(2000);
//
//        // Test the lorry number input
//        WebElement lorryNumberInput2 = driver.findElement(By.id("lorryNumber"));
//        lorryNumberInput2.sendKeys("ABC123");
//        Thread.sleep(2000);
//
//        // Test the driver contact input
//        WebElement driverContactInput2 = driver.findElement(By.id("drivercontact"));
//        driverContactInput2.sendKeys("1234567890");
//        Thread.sleep(2000);
//
////         Test the due date input
//        WebElement dueDateInput2 = driver.findElement(By.cssSelector(".ant-picker-input input"));
//        dueDateInput2.click();
//        Thread.sleep(1000);
//        driver.findElement(By.cssSelector(".ant-picker-cell-inner")).click();
//        Thread.sleep(1000);
////
//
////
//
////
//        // Test the cubes input
//        WebElement cubesInput2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[value='1']")));
//        cubesInput2.sendKeys("3");
////        WebElement cubesInput1 = driver.findElement(By.cssSelector("input[value='1']"));
////        driver.findElement(By.xpath("/html/body/div/div/main/div/main/div[7]/div/div/div/button[2]")).click();
////        assertEquals(cubesInput1.getAttribute("value"), "10000000", "Cubes should be incremented");
//        Thread.sleep(1000);
////
//        // Submit the form
//        WebElement submitButton2 = driver.findElement(By.xpath("/html/body/div[1]/div/main/div/main/div[8]/div/button[2]"));
//        submitButton2.click();
//
    }


}