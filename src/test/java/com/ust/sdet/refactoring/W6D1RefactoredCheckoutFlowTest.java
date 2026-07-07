package com.ust.sdet.refactoring;

import com.ust.sdet.refactoring.after.CheckoutJourney;
import com.ust.sdet.refactoring.after.CheckoutResult;
import com.ust.sdet.refactoring.after.DefaultWebDriverProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class W6D1RefactoredCheckoutFlowTest {
    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new DefaultWebDriverProvider().create();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Refactored checkout journey keeps the same user behaviour")
    void refactoredCheckoutJourneyStillPlacesOrder() {
        CheckoutResult result = new CheckoutJourney(driver).buyFirstCatalogProduct();

        assertAll(
            () -> assertFalse(result.productName().isBlank(), "product name should be captured from detail page"),
            () -> assertTrue(result.cartTotal().matches(".*\\d.*"), "cart total should show a numeric amount before checkout"),
            () -> assertTrue(result.confirmationText().toLowerCase().contains("order"), "confirmation should mention order")
        );
    }
}
