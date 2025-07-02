package com.blitz.springboot4.spider;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.Random;
import java.util.Set;

public class SeleniumLoginService {

    static WebDriver driver;
    private static WebDriverWait wait;
    private static final Random random = new Random();



    public static String getCookie() throws Exception {
        login();
        Thread.sleep(3000);

        Set<Cookie> seleniumCookies = driver.manage().getCookies();

        StringBuilder cookieHeader = new StringBuilder();
        for (Cookie cookie : seleniumCookies) {
            cookieHeader.append(cookie.getName())
                    .append("=")
                    .append(cookie.getValue())
                    .append("; ");
        }
        // 删除最后的 "; "
        if (!cookieHeader.isEmpty()) {
            cookieHeader.setLength(cookieHeader.length() - 2);
        }


        return cookieHeader.toString();
    }

    public static void login() {

        // 初始化 ChromeDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        driver = new ChromeDriver(options);

        // 防止检测 webdriver
        ((ChromeDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument",
                java.util.Map.of("source", "Object.defineProperty(navigator, 'webdriver', { get: () => undefined })"));

        wait = new WebDriverWait(driver, Duration.ofSeconds(5));


        driver.get("https://www.gigab2b.com/index.php?route=account/login");

        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Email']")));
        emailInput.sendKeys("demodeu@test.com");

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Password']")));
        passwordInput.sendKeys("ASD@@0514bnm");

        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Login Now')]")));
        loginButton.click();


        handleSliderCaptcha();

    }

    private static int random(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private static int jitter(double scale) {
        return (int) (random.nextDouble() * 2 * scale - scale);
    }

    private static int randomInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    public static boolean handleSliderCaptcha(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            Actions actions = new Actions(driver);


            Thread.sleep(2000); // 等待加载滑块

            WebElement sliderContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nc_1_n1t")));
            WebElement slider = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nc_1_n1z")));

            Point containerLocation = sliderContainer.getLocation();
            int containerX = containerLocation.getX();
            int containerWidth = sliderContainer.getSize().getWidth();

            Point sliderLocation = slider.getLocation();
            int sliderX = sliderLocation.getX();
            int sliderWidth = 20; // 估算滑块宽度

            int currentSliderPosition = sliderX - containerX;
            int targetPosition = containerWidth - sliderWidth;
            int slideDistance = targetPosition - currentSliderPosition;

            actions.clickAndHold(slider).perform();
            Thread.sleep(random(200, 500));

            int remaining = slideDistance;
            int fastSteps = randomInt(3, 5);
            int fastStepDistance = (int) (remaining * 0.6 / fastSteps);

            for (int i = 0; i < fastSteps; i++) {
                actions.moveByOffset(jitter(1), jitter(0.5)).perform();
                Thread.sleep(random(50, 150));
                actions.moveByOffset(fastStepDistance, 0).perform();
                Thread.sleep(random(100, 300));
                remaining -= fastStepDistance;
            }

            int mediumSteps = randomInt(3, 7);
            int mediumStep = (int) (remaining * 0.3 / mediumSteps);
            for (int i = 0; i < mediumSteps; i++) {
                actions.moveByOffset(jitter(2), jitter(1)).perform();
                Thread.sleep(random(100, 200));
                actions.moveByOffset(mediumStep, 0).perform();
                Thread.sleep(random(150, 300));
                remaining -= mediumStep;
            }

            int finalSteps = randomInt(2, 4);
            int finalStep = remaining / finalSteps;
            for (int i = 0; i < finalSteps; i++) {
                actions.moveByOffset(jitter(3), jitter(1.5)).perform();
                Thread.sleep(random(150, 300));
                actions.moveByOffset(finalStep, 0).perform();
                Thread.sleep(random(200, 400));
                remaining -= finalStep;
            }

            actions.moveByOffset(2, 0).perform();
            Thread.sleep(random(100, 200));

            int back = randomInt(2, 5);
            actions.moveByOffset(-back, 0).perform();
            Thread.sleep(random(100, 200));

            actions.moveByOffset(back + 1, 0).perform();
            Thread.sleep(random(100, 200));

            actions.release().perform();
            Thread.sleep(random(1000, 3000));
            System.out.println("检测到滑块，已跳过");
            return true;
        } catch (TimeoutException e) {
            System.out.println("没有检测到滑块，继续执行");
            return false;
        } catch (Exception e) {
            System.out.println("滑块验证处理时出错: " + e.getMessage());
            return false;
        }
    }


    public static WebDriver openPageWithSlider(String productCode) {
        if(driver != null){
            driver.quit();
        }

        // 初始化新 driver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        driver = new ChromeDriver(options);

        // 访问一个会触发滑块的页面（例如搜索页）
        driver.get("https://www.gigab2b.com/index.php?route=product/product&product_id=" + productCode);

        return driver;
    }



}
