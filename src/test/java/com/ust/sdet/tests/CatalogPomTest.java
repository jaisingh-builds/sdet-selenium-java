package com.ust.sdet.tests;

import com.ust.sdet.pages.CartPage;
import com.ust.sdet.pages.CatalogPage;
import com.ust.sdet.pages.ProductPage;
import com.ust.sdet.support.DriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogPomTest {
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = DriverFactory.createChromeDriver();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("POM search query returns only matching catalog titles")
    void searchFindsOnlyMatchingProducts() {
        CatalogPage catalog = new CatalogPage(driver)
            .open()
            .searchFor("headphones", "Showing 1 product");

        List<String> titles = catalog.titles();

        assertAll(
            () -> assertFalse(titles.isEmpty(), "search returned no products"),
            () -> assertTrue(
                titles.stream().allMatch((title) -> title.toLowerCase().contains("headphone")),
                "search results should be related to headphones"
            )
        );
    }

    @Test
    @DisplayName("POM sort hides the stale-element handling inside the page")
    void sortLowToHighOnPom() {
        List<Integer> prices = new CatalogPage(driver)
            .open()
            .sortBy("Price: Low to High")
            .prices();

        assertEquals(prices.stream().sorted().toList(), prices);
    }

    @Test
    @DisplayName("POM header component exposes cart badge and cart navigation")
    void headerComponentOpensCart() {
        CatalogPage catalog = new CatalogPage(driver).open();
        catalog.header().cartBadge().expectCount(0);

        CartPage cart = catalog.header().openCart();

        assertEquals(0, cart.lineCount());
    }

    @Test
    @DisplayName("POM completes catalog to cart to checkout journey")
    void catalogToConfirmedOrder() {
        CatalogPage catalog = new CatalogPage(driver)
            .open()
            .searchFor("headphones", "Showing 1 product");

        ProductPage product = catalog.openFirstProduct();
        assertTrue(product.name().toLowerCase().contains("headphone"));

        CartPage cart = product.addToCart();
        cart.header().cartBadge().expectCount(1);

        assertAll(
            () -> assertEquals(1, cart.lineCount()),
            () -> assertFalse(cart.total().isBlank())
        );

        String confirmation = cart.proceed()
            .placeOrder()
            .confirmationText();

        assertTrue(confirmation.toLowerCase().contains("confirmed"));
    }
}
