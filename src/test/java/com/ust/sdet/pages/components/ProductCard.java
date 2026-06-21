package com.ust.sdet.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProductCard {
    private static final By TITLE = By.cssSelector("[data-test='product-title']");
    private static final By PRICE = By.cssSelector("[data-test='product-price']");

    private final WebDriver driver;
    private final By cards;
    private final int index;

    public ProductCard(WebDriver driver, By cards, int index) {
        this.driver = driver;
        this.cards = cards;
        this.index = index;
    }

    public String title() {
        return root().findElement(TITLE).getText();
    }

    public int price() {
        return Integer.parseInt(root().findElement(PRICE).getText().replaceAll("[^0-9]", ""));
    }

    private WebElement root() {
        return driver.findElements(cards).get(index);
    }
}
