package com.ust.sdet.selenide.tests;

import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.ust.sdet.selenide.pages.SelenideCatalogPage;
import com.ust.sdet.selenide.support.SelenideTestConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(ScreenShooterExtension.class)
class SelenideCatalogTest {

    @BeforeAll
    static void configureSelenide() {
        SelenideTestConfig.apply();
    }

    @Test
    @DisplayName("Selenide search uses $ and auto-waited assertions")
    void searchFindsHeadphoneProducts() {
        SelenideCatalogPage catalog = new SelenideCatalogPage()
            .openCatalog()
            .searchFor("headphones")
            .shouldShowResultCount("Showing 1 product")
            .shouldHaveProducts(1);

        List<String> titles = catalog.titles();

        assertAll(
            () -> assertEquals(1, titles.size()),
            () -> assertTrue(titles.get(0).toLowerCase().contains("headphone"))
        );
    }

    @Test
    @DisplayName("Selenide collection assertions verify catalog rows")
    void collectionAssertionsVerifyCatalogResults() {
        SelenideCatalogPage catalog = new SelenideCatalogPage()
            .openCatalog();

        catalog.cards().shouldHave(sizeGreaterThan(2));
        assertFalse(catalog.titles().isEmpty(), "catalog should render product titles");
    }

    @Test
    @DisplayName("Selenide page object keeps sort flow driver-free")
    void sortLowToHighKeepsPricesOrdered() {
        List<Integer> prices = new SelenideCatalogPage()
            .openCatalog()
            .sortByPriceLowToHigh()
            .prices();

        assertEquals(prices.stream().sorted().toList(), prices);
    }

    @Test
    @DisplayName("Selenide negative search waits for empty state")
    void emptySearchShowsNoProductsMessage() {
        SelenideCatalogPage catalog = new SelenideCatalogPage()
            .openCatalog()
            .searchFor("zzzz-no-product")
            .shouldShowResultCount("Showing 0 products")
            .shouldShowEmptySearchMessage();

        catalog.cards().shouldHave(size(0));
    }
}
