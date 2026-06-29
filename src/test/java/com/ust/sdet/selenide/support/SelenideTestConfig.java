package com.ust.sdet.selenide.support;

import com.codeborne.selenide.Configuration;
import com.ust.sdet.support.Config;

public final class SelenideTestConfig {
    private SelenideTestConfig() {
    }

    public static void apply() {
        Configuration.baseUrl = Config.baseUrl();
        Configuration.browser = Config.browser().name().toLowerCase();
        Configuration.headless = Config.headless();
        Configuration.timeout = Config.timeoutSeconds() * 1000;
        Configuration.browserSize = "1440x900";
        Configuration.reportsFolder = "target/selenide-reports";

        if (Config.gridEnabled()) {
            Configuration.remote = Config.gridUrl();
        }
    }
}
