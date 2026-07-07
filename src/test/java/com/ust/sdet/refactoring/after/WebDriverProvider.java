package com.ust.sdet.refactoring.after;

import org.openqa.selenium.WebDriver;

@FunctionalInterface
public interface WebDriverProvider {
    WebDriver create();
}
