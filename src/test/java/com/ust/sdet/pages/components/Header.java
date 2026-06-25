package com.ust.sdet.pages.components;

import com.ust.sdet.pages.BasePage;
import com.ust.sdet.pages.CartPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Header extends BasePage {
    private static final By CART_ICON = By.cssSelector("[data-test='cart-icon']");

    public Header(WebDriver driver) {
        super(driver);
    }

    public CartBadge cartBadge() {
        return new CartBadge(driver);
    }

    public CartPage openCart() {
        click(CART_ICON);
        return new CartPage(driver);
    }
}
