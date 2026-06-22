package com.cydeo.utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.util.Map;

public class Driver {

    private Driver() {}

    private static WebDriver driver;

    public synchronized static WebDriver getDriver() {
        if (driver == null) {
            String browser = ConfigurationReader.getProperty("browser");
            if (browser == null || browser.trim().isEmpty()) {
                browser = "chrome";
            }

            switch (browser.toLowerCase().trim()) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                    chromeOptions.addArguments("--start-maximized");
                    chromeOptions.addArguments("--remote-allow-origins=*");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-popup-blocking");
                    chromeOptions.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/149.0.0.0 Safari/537.36");
                    chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                    chromeOptions.setExperimentalOption("useAutomationExtension", false);
                    ChromeDriver chromeDriver = new ChromeDriver(chromeOptions);
                    chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of(
                        "source",
                        "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});" +
                        "Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]});" +
                        "Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en']});" +
                        "window.chrome = { runtime: {} };"
                    ));
                    driver = chromeDriver;
                    break;
                case "chrome-headless":
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--disable-blink-features=AutomationControlled");
                    options.addArguments("--remote-allow-origins=*");
                    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                    options.setExperimentalOption("useAutomationExtension", false);
                    driver = new ChromeDriver(options);
                    break;
                case "firefox":
                    driver = new FirefoxDriver();
                    break;
                case "safari":
                    driver = new SafariDriver();
                    break;
                default:
                    throw new RuntimeException("Unsupported browser: " + browser);
            }

            driver.manage().window().maximize();
        }
        return driver;
    }

    public synchronized static void closeDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}

