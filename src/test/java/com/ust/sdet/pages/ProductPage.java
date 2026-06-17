package com.ust.sdet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductPage extends BasePage {
    private static final By NAME = By.cssSelector("[data-test='detail-name']");
    private static final By ADD = By.cssSelector("[data-test='add-to-cart']");

    public ProductPage(WebDriver driver) {
        super(driver);
        visible(NAME);
    }

    public String name() {
        return text(NAME);
    }

    public CartPage addToCart() {
        click(ADD);
        wait.until(ExpectedConditions.urlContains("/cart"));
        return new CartPage(driver);
    }
}
