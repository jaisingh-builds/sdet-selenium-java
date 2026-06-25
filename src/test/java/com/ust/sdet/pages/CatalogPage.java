package com.ust.sdet.pages;

import com.ust.sdet.pages.roles.Navigable;
import com.ust.sdet.pages.roles.Searchable;
import com.ust.sdet.pages.components.ProductCard;
import com.ust.sdet.support.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.IntStream;

public class CatalogPage extends BasePage implements Navigable<CatalogPage>, Searchable<CatalogPage> {
    private static final By SEARCH = By.cssSelector("[data-test='search-input']");
    private static final By SEARCH_BUTTON = By.cssSelector("[data-test='search-button']");
    private static final By RESULT_COUNT = By.cssSelector("[data-test='catalog-result-count']");
    private static final By EMPTY_SEARCH = By.cssSelector("[data-test='empty-search']");
    private static final By CARDS = By.cssSelector("[data-test='product-card']");
    private static final By FIRST_LINK = By.cssSelector("[data-test='product-card'] a");
    private static final By SORT = By.cssSelector("[data-test='sort-select']");

    public CatalogPage(WebDriver driver) {
        super(driver);
    }

    public CatalogPage open() {
        driver.get(Config.catalogUrl());
        visible(SEARCH);
        visible(RESULT_COUNT);
        return this;
    }

    public CatalogPage searchFor(String query) {
        String previousResultCount = text(RESULT_COUNT);
        type(SEARCH, query);
        click(SEARCH_BUTTON);
        com.ust.sdet.support.Waits.until(driver, (ignored) -> {
            String currentResultCount = text(RESULT_COUNT);
            return !currentResultCount.equals("Searching products...")
                && !currentResultCount.equals(previousResultCount);
        }, DEFAULT_TIMEOUT);
        return this;
    }

    public CatalogPage searchFor(String query, String expectedResultCount) {
        searchFor(query);
        waitForText(RESULT_COUNT, expectedResultCount);
        return this;
    }

    public CatalogPage sortBy(String visibleText) {
        WebElement oldFirstCard = visible(CARDS);
        new Select(visible(SORT)).selectByVisibleText(visibleText);
        waitForStaleness(oldFirstCard);
        waitForMoreThan(CARDS, 0);
        return this;
    }

    public List<ProductCard> cards() {
        int cardCount = visibleElements(CARDS).size();
        return IntStream.range(0, cardCount)
            .mapToObj(index -> new ProductCard(driver, CARDS, index))
            .toList();
    }

    public List<String> titles() {
        return cards().stream()
            .map(ProductCard::title)
            .toList();
    }

    public List<Integer> prices() {
        return cards().stream()
            .map(ProductCard::price)
            .toList();
    }

    public String emptySearchMessage() {
        return text(EMPTY_SEARCH);
    }

    public ProductPage openFirstProduct() {
        click(FIRST_LINK);
        return new ProductPage(driver);
    }
}
