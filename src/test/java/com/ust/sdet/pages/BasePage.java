package com.ust.sdet.pages;

import com.ust.sdet.pages.components.Header;
import com.ust.sdet.support.Config;
import com.ust.sdet.support.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {
    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(Config.timeoutSeconds());

    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    public Header header() {
        return new Header(driver);
    }

    protected WebElement visible(By by) {
        return Waits.visible(driver, by, DEFAULT_TIMEOUT);
    }

    protected List<WebElement> visibleElements(By by) {
        return Waits.visibleAll(driver, by, DEFAULT_TIMEOUT);
    }

    protected List<WebElement> elements(By by) {
        return driver.findElements(by);
    }

    protected void click(By by) {
        Waits.clickable(driver, by, DEFAULT_TIMEOUT).click();
    }

    protected void type(By by, CharSequence... text) {
        WebElement element = visible(by);
        element.clear();
        element.sendKeys(text);
    }

    protected String text(By by) {
        return visible(by).getText();
    }

    protected void waitForText(By by, String expectedText) {
        Waits.textToBe(driver, by, expectedText, DEFAULT_TIMEOUT);
    }

    protected void waitForStaleness(WebElement element) {
        Waits.stalenessOf(driver, element, DEFAULT_TIMEOUT);
    }

    protected void waitForMoreThan(By by, int count) {
        Waits.numberOfElementsToBeMoreThan(driver, by, count, DEFAULT_TIMEOUT);
    }

    protected void waitForUrlContaining(String fragment) {
        Waits.urlContains(driver, fragment, DEFAULT_TIMEOUT);
    }
}
