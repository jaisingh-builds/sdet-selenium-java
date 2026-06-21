package com.ust.sdet.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static WebDriver createChromeDriver() {
        return createDriver();
    }

    public static WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,900");
        if (Config.gridEnabled()) {
            try {
                return new RemoteWebDriver(URI.create(Config.gridUrl()).toURL(), options);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid Selenium Grid URL: " + Config.gridUrl(), e);
            }
        }
        return new ChromeDriver(options);
    }
}
