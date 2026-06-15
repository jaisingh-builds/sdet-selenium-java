package com.ust.sdet.tests;

import com.ust.sdet.support.Config;
import com.ust.sdet.support.DriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogFlowTest {
    private static final By SEARCH_INPUT = By.cssSelector("[data-test='search-input']");
    private static final By PRODUCT_CARD = By.cssSelector("[data-test='product-card']");
    private static final By PRODUCT_TITLE = By.cssSelector("[data-test='product-title']");
    private static final By PRODUCT_PRICE = By.cssSelector("[data-test='product-price']");
    private static final By PRODUCT_LINK = By.cssSelector("[data-test='product-card'] a");
    private static final By DETAIL_NAME = By.cssSelector("[data-test='detail-name']");
    private static final By ADD_TO_CART = By.cssSelector("[data-test='add-to-cart']");
    private static final By CART_COUNT = By.cssSelector("[data-test='cart-count']");
    private static final By SORT_SELECT = By.cssSelector("[data-test='sort-select']");
    private static final By QUICK_VIEW = By.cssSelector("[data-test='quick-view']");
    private static final By RESULT_COUNT = By.cssSelector("[data-test='catalog-result-count']");
    private static final By EMPTY_SEARCH = By.cssSelector("[data-test='empty-search']");

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        driver = DriverFactory.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(Config.catalogUrl());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Search waits for catalog cards and validates matching results")
    void searchShowsMatchingCards() {
        search("headphones", "Showing 1 product");

        List<WebElement> cards = driver.findElements(PRODUCT_CARD);
        assertEquals(1, cards.size(), "headphones search should return one classroom product");

        for (WebElement card : cards) {
            String title = card.findElement(PRODUCT_TITLE).getText();
            assertAll(
                () -> assertTrue(title.toLowerCase().contains("headphone"), "unrelated result: " + title),
                () -> assertTrue(card.findElement(PRODUCT_PRICE).isDisplayed(), "price missing for: " + title)
            );
        }
    }

    @Test
    @DisplayName("Search with no matching product shows an empty result message")
    void searchShowsEmptyStateForUnknownProduct() {
        search("zzzz-not-a-product", "Showing 0 products");

        WebElement emptySearch = wait.until(ExpectedConditions.visibilityOfElementLocated(EMPTY_SEARCH));
        assertAll(
            () -> assertTrue(driver.findElements(PRODUCT_CARD).isEmpty(), "cards should be hidden for empty search"),
            () -> assertEquals("No products found", emptySearch.findElement(By.tagName("h2")).getText())
        );
    }

    @Test
    @DisplayName("Product detail opens and add-to-cart updates the cart badge")
    void detailAddToCartUpdatesBadge() {
        wait.until(ExpectedConditions.elementToBeClickable(PRODUCT_LINK)).click();

        WebElement detailName = wait.until(ExpectedConditions.visibilityOfElementLocated(DETAIL_NAME));
        assertAll(
            () -> assertTrue(driver.getCurrentUrl().contains("/product/")),
            () -> assertFalse(detailName.getText().isBlank())
        );

        wait.until(ExpectedConditions.elementToBeClickable(ADD_TO_CART)).click();
        wait.until(ExpectedConditions.urlContains("/cart"));
        wait.until(ExpectedConditions.textToBe(CART_COUNT, "1"));
    }

    @Test
    @DisplayName("Sort dropdown re-renders cards and prices become ascending")
    void sortLowToHighShowsAscendingPrices() {
        WebElement oldFirstCard = wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_CARD));

        new Select(driver.findElement(SORT_SELECT)).selectByVisibleText("Price: Low to High");

        wait.until(ExpectedConditions.stalenessOf(oldFirstCard));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(PRODUCT_CARD, 0));

        List<Integer> prices = driver.findElements(PRODUCT_PRICE).stream()
            .map((element) -> Integer.parseInt(element.getText().replaceAll("[^0-9]", "")))
            .toList();

        assertEquals(prices.stream().sorted().toList(), prices);
    }

    @Test
    @DisplayName("Actions can hover a card and reveal its scoped quick-view marker")
    void hoverRevealsQuickViewMarker() {
        WebElement firstCard = wait.until(ExpectedConditions.visibilityOfElementLocated(PRODUCT_CARD));

        new Actions(driver).moveToElement(firstCard).perform();

        wait.until((ignored) -> firstCard.findElement(QUICK_VIEW).isDisplayed());
        assertEquals("Quick view ready", firstCard.findElement(QUICK_VIEW).getText());
    }

    private void search(String query, String expectedResultCount) {
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT));
        searchInput.clear();
        searchInput.sendKeys(query);
        searchInput.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.textToBe(RESULT_COUNT, expectedResultCount));
    }
}
