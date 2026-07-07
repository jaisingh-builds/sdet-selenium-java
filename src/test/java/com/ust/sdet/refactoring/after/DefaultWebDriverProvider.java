package com.ust.sdet.refactoring.after;

import com.ust.sdet.support.DriverFactory;
import org.openqa.selenium.WebDriver;

public class DefaultWebDriverProvider implements WebDriverProvider {
    @Override
    public WebDriver create() {
        return DriverFactory.createDriver();
    }
}
