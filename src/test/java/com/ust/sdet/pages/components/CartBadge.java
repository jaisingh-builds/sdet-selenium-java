package com.ust.sdet.pages.components;

import com.ust.sdet.support.Config;
import com.ust.sdet.support.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class CartBadge {
    private static final By COUNT = By.cssSelector("[data-test='cart-count']");
    private static final Duration TIMEOUT = Duration.ofSeconds(Config.timeoutSeconds());

    private final WebDriver driver;

    public CartBadge(WebDriver driver) {
        this.driver = driver;
    }

    public int count() {
        return Integer.parseInt(Waits.visible(driver, COUNT, TIMEOUT).getText());
    }

    public void expectCount(int expectedCount) {
        Waits.textToBe(driver, COUNT, String.valueOf(expectedCount), TIMEOUT);
    }
}
