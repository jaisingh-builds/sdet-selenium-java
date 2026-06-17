package com.ust.sdet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {
    private static final By CART_PAGE = By.cssSelector("[data-test='cart-page']");
    private static final By LINES = By.cssSelector("[data-test='cart-line']");
    private static final By TOTAL = By.cssSelector("[data-test='cart-total']");
    private static final By CHECKOUT = By.cssSelector("[data-test='checkout-button']");

    public CartPage(WebDriver driver) {
        super(driver);
        visible(CART_PAGE);
    }

    public int lineCount() {
        return elements(LINES).size();
    }

    public String total() {
        return text(TOTAL);
    }

    public CheckoutPage proceed() {
        click(CHECKOUT);
        return new CheckoutPage(driver);
    }
}
