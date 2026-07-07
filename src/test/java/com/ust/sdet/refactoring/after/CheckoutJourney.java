package com.ust.sdet.refactoring.after;

import com.ust.sdet.pages.CartPage;
import com.ust.sdet.pages.CatalogPage;
import com.ust.sdet.pages.CheckoutPage;
import com.ust.sdet.pages.ProductPage;
import org.openqa.selenium.WebDriver;

public class CheckoutJourney {
    private final WebDriver driver;

    public CheckoutJourney(WebDriver driver) {
        this.driver = driver;
    }

    public CheckoutResult buyFirstCatalogProduct() {
        CatalogPage catalog = new CatalogPage(driver).open();

        ProductPage product = catalog.openFirstProduct();
        String productName = product.name();

        CartPage cart = product.addToCart();
        String cartTotal = cart.total();

        CheckoutPage checkout = cart.proceed().placeOrder();
        return new CheckoutResult(productName, cartTotal, checkout.confirmationText());
    }
}
