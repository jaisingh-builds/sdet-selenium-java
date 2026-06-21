package com.ust.sdet.bdd;

import com.ust.sdet.pages.CartPage;
import com.ust.sdet.pages.CatalogPage;
import com.ust.sdet.pages.CheckoutPage;
import com.ust.sdet.pages.ProductPage;
import com.ust.sdet.pages.components.Header;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

public class World {
    public WebDriver driver;
    public CatalogPage catalog;
    public ProductPage product;
    public CartPage cart;
    public CheckoutPage checkout;
    public Scenario scenario;
    public String cartTotal;

    public Header header() {
        return new Header(driver);
    }
}
