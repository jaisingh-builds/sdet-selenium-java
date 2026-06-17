package com.ust.sdet.pages.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ProductCard {
    private static final By TITLE = By.cssSelector("[data-test='product-title']");
    private static final By PRICE = By.cssSelector("[data-test='product-price']");

    private final WebElement root;

    public ProductCard(WebElement root) {
        this.root = root;
    }

    public String title() {
        return root.findElement(TITLE).getText();
    }

    public int price() {
        return Integer.parseInt(root.findElement(PRICE).getText().replaceAll("[^0-9]", ""));
    }
}
