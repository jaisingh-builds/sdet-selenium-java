package com.ust.sdet.tests;

import com.ust.sdet.pages.BasePage;
import com.ust.sdet.pages.CartPage;
import com.ust.sdet.pages.CatalogPage;
import com.ust.sdet.pages.CheckoutPage;
import com.ust.sdet.pages.ProductPage;
import com.ust.sdet.pages.roles.CheckoutCapable;
import com.ust.sdet.pages.roles.Navigable;
import com.ust.sdet.pages.roles.Searchable;
import com.ust.sdet.support.Browser;
import com.ust.sdet.support.DriverFactory;
import com.ust.sdet.support.Waits;
import com.ust.sdet.support.api.ApiClient;
import com.ust.sdet.support.api.HttpApiClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Day4SolidStructureTest {
    @Test
    @DisplayName("DIP: page objects receive WebDriver instead of constructing browsers")
    void pagesDependOnWebDriverAbstraction() {
        Set<Class<? extends BasePage>> pages = Set.of(
            CatalogPage.class,
            ProductPage.class,
            CartPage.class,
            CheckoutPage.class
        );

        assertAll(pages.stream()
            .map(page -> () -> assertDoesNotThrow(
                () -> page.getDeclaredConstructor(WebDriver.class),
                page.getSimpleName() + " should expose a WebDriver constructor"
            )));
    }

    @Test
    @DisplayName("OCP: browser factory exposes a registry of supported browser types")
    void driverFactorySupportsRegistryBrowsers() {
        assertEquals(Set.of(Browser.CHROME, Browser.FIREFOX, Browser.EDGE, Browser.SAFARI),
            DriverFactory.supportedBrowsers());
    }

    @Test
    @DisplayName("SRP/DRY: Waits is a stateless utility class")
    void waitsIsStatelessUtility() throws NoSuchMethodException {
        Constructor<Waits> constructor = Waits.class.getDeclaredConstructor();

        assertAll(
            () -> assertTrue(Modifier.isPrivate(constructor.getModifiers())),
            () -> assertEquals(0, Waits.class.getDeclaredFields().length)
        );
    }

    @Test
    @DisplayName("ISP: pages implement only role interfaces they actually support")
    void pagesUseSmallRoleInterfaces() {
        assertAll(
            () -> assertTrue(Navigable.class.isAssignableFrom(CatalogPage.class)),
            () -> assertTrue(Searchable.class.isAssignableFrom(CatalogPage.class)),
            () -> assertTrue(CheckoutCapable.class.isAssignableFrom(CartPage.class)),
            () -> assertFalse(CheckoutCapable.class.isAssignableFrom(CatalogPage.class))
        );
    }

    @Test
    @DisplayName("DIP: API utility depends on ApiClient abstraction")
    void apiClientHasReplaceableImplementation() {
        assertTrue(ApiClient.class.isAssignableFrom(HttpApiClient.class));
    }
}
