package com.ust.sdet.refactoring;

import com.ust.sdet.pages.BasePage;
import com.ust.sdet.pages.CartPage;
import com.ust.sdet.pages.CatalogPage;
import com.ust.sdet.pages.CheckoutPage;
import com.ust.sdet.pages.ProductPage;
import com.ust.sdet.refactoring.after.CheckoutJourney;
import com.ust.sdet.refactoring.after.DefaultWebDriverProvider;
import com.ust.sdet.refactoring.after.WebDriverProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class W6D1RefactoringStructureTest {
    @Test
    @DisplayName("Refactored pages share BasePage instead of duplicating waits")
    void pagesExtendSharedBasePage() {
        List<Class<?>> pages = List.of(CatalogPage.class, ProductPage.class, CartPage.class, CheckoutPage.class);

        assertTrue(
            pages.stream().allMatch((page) -> page.getSuperclass().equals(BasePage.class)),
            "Every concrete page should reuse BasePage behaviour"
        );
    }

    @Test
    @DisplayName("Pages own their locators in one place")
    void pagesOwnLocatorsInPrivateStaticFields() {
        List<Field> catalogLocators = Arrays.stream(CatalogPage.class.getDeclaredFields())
            .filter((field) -> field.getType().equals(By.class))
            .toList();

        assertAll(
            () -> assertFalse(catalogLocators.isEmpty(), "CatalogPage should expose locator ownership"),
            () -> assertTrue(catalogLocators.stream().allMatch((field) -> java.lang.reflect.Modifier.isPrivate(field.getModifiers()))),
            () -> assertTrue(catalogLocators.stream().allMatch((field) -> java.lang.reflect.Modifier.isStatic(field.getModifiers()))),
            () -> assertTrue(catalogLocators.stream().allMatch((field) -> java.lang.reflect.Modifier.isFinal(field.getModifiers())))
        );
    }

    @Test
    @DisplayName("Driver creation is inverted behind a provider")
    void driverCreationIsInverted() throws NoSuchMethodException {
        WebDriverProvider provider = new DefaultWebDriverProvider();
        Constructor<CheckoutJourney> constructor = CheckoutJourney.class.getConstructor(WebDriver.class);

        assertAll(
            () -> assertTrue(provider instanceof WebDriverProvider),
            () -> assertEquals("create", WebDriverProvider.class.getDeclaredMethods()[0].getName()),
            () -> assertEquals(WebDriver.class, constructor.getParameterTypes()[0]),
            () -> assertThrows(NoSuchMethodException.class, () -> CheckoutJourney.class.getConstructor())
        );
    }

    @Test
    @DisplayName("CheckoutJourney is a small DSL around page objects")
    void checkoutJourneyReadsAsIntent() throws NoSuchMethodException {
        assertTrue(
            Arrays.stream(CheckoutJourney.class.getDeclaredFields())
                .anyMatch((field) -> field.getType().getSimpleName().equals("WebDriver")),
            "CheckoutJourney should receive WebDriver, not create it"
        );
        assertEquals("buyFirstCatalogProduct", CheckoutJourney.class.getDeclaredMethod("buyFirstCatalogProduct").getName());
    }
}
