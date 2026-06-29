package com.ust.sdet.selenide.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class SelenideCatalogPage {
    private final SelenideElement title = $("[data-test='catalog-title']");
    private final SelenideElement searchInput = $("[data-test='search-input']");
    private final SelenideElement searchButton = $("[data-test='search-button']");
    private final SelenideElement resultCount = $("[data-test='catalog-result-count']");
    private final SelenideElement sortSelect = $("[data-test='sort-select']");
    private final SelenideElement emptySearch = $("[data-test='empty-search']");
    private final ElementsCollection productCards = $$("[data-test='product-card']");
    private final ElementsCollection productTitles = $$("[data-test='product-title']");
    private final ElementsCollection productPrices = $$("[data-test='product-price']");

    public SelenideCatalogPage openCatalog() {
        open("/catalog");
        title.shouldHave(exactText("Product Catalog"));
        resultCount.shouldBe(visible);
        productCards.shouldHave(sizeGreaterThan(0));
        return this;
    }

    public SelenideCatalogPage searchFor(String query) {
        searchInput.shouldBe(visible).setValue(query);
        searchButton.click();
        resultCount.shouldNotHave(exactText("Searching products..."));
        return this;
    }

    public SelenideCatalogPage shouldShowResultCount(String expectedText) {
        resultCount.shouldHave(text(expectedText));
        return this;
    }

    public SelenideCatalogPage shouldHaveProducts(int minimumCount) {
        productCards.shouldHave(sizeGreaterThan(minimumCount - 1));
        return this;
    }

    public SelenideCatalogPage sortByPriceLowToHigh() {
        sortSelect.selectOption("Price: Low to High");
        productCards.shouldHave(sizeGreaterThan(0));
        return this;
    }

    public SelenideCatalogPage shouldShowEmptySearchMessage() {
        emptySearch.shouldBe(visible).shouldHave(text("No products found"));
        return this;
    }

    public List<String> titles() {
        return productTitles.texts();
    }

    public List<Integer> prices() {
        return productPrices.texts().stream()
            .map(SelenideCatalogPage::toNumber)
            .toList();
    }

    public ElementsCollection cards() {
        return productCards;
    }

    private static int toNumber(String priceText) {
        return Integer.parseInt(priceText.replaceAll("[^0-9]", ""));
    }
}
