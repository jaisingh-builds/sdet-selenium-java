package com.ust.sdet.xpath.support;

import com.codeborne.selenide.Configuration;

public final class XPathDemoConfig {
    private XPathDemoConfig() {
    }

    public static void apply() {
        Configuration.browser = System.getProperty("browser", "chrome");
        Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        Configuration.timeout = Long.parseLong(System.getProperty("timeoutSeconds", "10")) * 1000;
        Configuration.browserSize = "1440x900";
        Configuration.reportsFolder = "target/xpath-demo-reports";
        Configuration.screenshots = true;
        Configuration.savePageSource = true;
    }

    public static void applySecretSafe() {
        apply();
        Configuration.screenshots = false;
        Configuration.savePageSource = false;
    }
}
