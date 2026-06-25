package com.ust.sdet.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public final class DriverFactory {
    // Tracks live drivers so an interrupted run (Ctrl-C / JVM exit) still closes Chrome
    // instead of leaving orphaned chromedriver + Chrome processes behind.
    private static final Set<WebDriver> LIVE_DRIVERS =
        Collections.newSetFromMap(new WeakHashMap<>());
    private static final Map<Browser, Supplier<WebDriver>> LOCAL_REGISTRY = localRegistry();
    private static final Map<Browser, Supplier<AbstractDriverOptions<?>>> OPTIONS_REGISTRY =
        optionsRegistry();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (WebDriver d : LIVE_DRIVERS) {
                try {
                    d.quit();
                } catch (RuntimeException ignored) {
                    // best-effort cleanup on shutdown
                }
            }
        }));
    }

    private DriverFactory() {
    }

    public static WebDriver createChromeDriver() {
        return create(Browser.CHROME);
    }

    public static WebDriver createDriver() {
        return create(Config.browser());
    }

    public static WebDriver create(Browser browser) {
        WebDriver driver;
        if (Config.gridEnabled()) {
            try {
                driver = new RemoteWebDriver(
                    URI.create(Config.gridUrl()).toURL(),
                    OPTIONS_REGISTRY.get(browser).get()
                );
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("Invalid Selenium Grid URL: " + Config.gridUrl(), e);
            }
        } else {
            Supplier<WebDriver> supplier = LOCAL_REGISTRY.get(browser);
            if (supplier == null) {
                throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
            driver = supplier.get();
        }
        LIVE_DRIVERS.add(driver);
        return driver;
    }

    public static Set<Browser> supportedBrowsers() {
        return Collections.unmodifiableSet(LOCAL_REGISTRY.keySet());
    }

    private static Map<Browser, Supplier<WebDriver>> localRegistry() {
        Map<Browser, Supplier<WebDriver>> registry = new EnumMap<>(Browser.class);
        registry.put(Browser.CHROME, () -> new ChromeDriver(chromeOptions()));
        registry.put(Browser.FIREFOX, () -> new FirefoxDriver(firefoxOptions()));
        registry.put(Browser.EDGE, () -> new EdgeDriver(edgeOptions()));
        registry.put(Browser.SAFARI, () -> new SafariDriver(safariOptions()));
        return Collections.unmodifiableMap(registry);
    }

    private static Map<Browser, Supplier<AbstractDriverOptions<?>>> optionsRegistry() {
        Map<Browser, Supplier<AbstractDriverOptions<?>>> registry = new EnumMap<>(Browser.class);
        registry.put(Browser.CHROME, DriverFactory::chromeOptions);
        registry.put(Browser.FIREFOX, DriverFactory::firefoxOptions);
        registry.put(Browser.EDGE, DriverFactory::edgeOptions);
        registry.put(Browser.SAFARI, DriverFactory::safariOptions);
        return Collections.unmodifiableMap(registry);
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,900");
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (Config.headless()) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static EdgeOptions edgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,900");
        return options;
    }

    private static SafariOptions safariOptions() {
        return new SafariOptions();
    }
}
