package com.ust.sdet.support;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public final class Waits {
    private Waits() {
    }

    public static WebElement visible(WebDriver driver, By by, Duration timeout) {
        return wait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public static List<WebElement> visibleAll(WebDriver driver, By by, Duration timeout) {
        return wait(driver, timeout).until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    public static WebElement clickable(WebDriver driver, By by, Duration timeout) {
        return wait(driver, timeout).until(ExpectedConditions.elementToBeClickable(by));
    }

    public static Boolean textToBe(WebDriver driver, By by, String text, Duration timeout) {
        return wait(driver, timeout).until(ExpectedConditions.textToBe(by, text));
    }

    public static Boolean stalenessOf(WebDriver driver, WebElement element, Duration timeout) {
        return wait(driver, timeout).until(ExpectedConditions.stalenessOf(element));
    }

    public static List<WebElement> numberOfElementsToBeMoreThan(
        WebDriver driver,
        By by,
        int count,
        Duration timeout
    ) {
        return wait(driver, timeout).until(ExpectedConditions.numberOfElementsToBeMoreThan(by, count));
    }

    public static Boolean urlContains(WebDriver driver, String fragment, Duration timeout) {
        return wait(driver, timeout).until(ExpectedConditions.urlContains(fragment));
    }

    public static <T> T until(WebDriver driver, ExpectedCondition<T> condition, Duration timeout) {
        return wait(driver, timeout).until(condition);
    }

    private static WebDriverWait wait(WebDriver driver, Duration timeout) {
        return new WebDriverWait(driver, timeout);
    }
}
