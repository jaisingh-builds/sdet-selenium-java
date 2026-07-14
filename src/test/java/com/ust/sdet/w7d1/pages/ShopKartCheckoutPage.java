package com.ust.sdet.w7d1.pages;

import com.ust.sdet.pages.BasePage;
import com.ust.sdet.w7d1.api.SeededCart;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;

public class ShopKartCheckoutPage extends BasePage {
    private static final By CART_TABLE = By.cssSelector(".cart-table");
    private static final By CART_TOTAL = By.cssSelector("[data-role='cart-total']");
    private static final By CHECKOUT = By.xpath("//button[normalize-space()='Checkout']");
    private static final By ADDRESS = By.id("address");
    private static final By COUPON = By.id("coupon");
    private static final By APPLY_COUPON = By.xpath("//button[normalize-space()='Apply coupon']");
    private static final By COUPON_STATUS = By.xpath("//*[@role='status' and contains(.,'Coupon')]");
    private static final By CHECKOUT_TOTAL = By.cssSelector("[data-field='checkout-total']");
    private static final By PLACE_ORDER = By.xpath("//button[normalize-space()='Place order']");
    private static final By ORDER_STATUS = By.cssSelector("[data-field='order-status']");
    private static final By ORDER_TOTAL = By.cssSelector("[data-field='order-total']");

    private final String baseUrl;

    public ShopKartCheckoutPage(WebDriver driver, String baseUrl) {
        super(driver);
        this.baseUrl = baseUrl;
    }

    public ShopKartCheckoutPage openSeededCart(SeededCart cart, long expectedSubtotalPaise) {
        driver.get(baseUrl + "/login");
        ((JavascriptExecutor) driver).executeScript("""
            sessionStorage.setItem('shopkart.session', JSON.stringify({
              token: arguments[0],
              customerId: Number(arguments[1]),
              customer: {
                id: Number(arguments[1]),
                persona: 'alice',
                email: arguments[2],
                displayName: arguments[3]
              }
            }));
            sessionStorage.setItem('shopkart.cart.' + arguments[1], String(arguments[4]));
            """, cart.token(), cart.customerId(), cart.email(), cart.displayName(), cart.cartId());

        driver.get(baseUrl + "/cart");
        visible(CART_TABLE);
        assertDisplayedPaise(CART_TOTAL, expectedSubtotalPaise);
        click(CHECKOUT);
        visible(ADDRESS);
        assertDisplayedPaise(CHECKOUT_TOTAL, expectedSubtotalPaise);
        return this;
    }

    public ShopKartCheckoutPage applyCoupon(String couponCode, long expectedTotalPaise) {
        type(COUPON, couponCode);
        click(APPLY_COUPON);
        visible(COUPON_STATUS);
        new WebDriverWait(driver, DEFAULT_TIMEOUT).until(
            ignored -> displayedPaise(CHECKOUT_TOTAL) == expectedTotalPaise
        );
        return this;
    }

    public long placeOrder(String address, long expectedTotalPaise) {
        type(ADDRESS, address);
        click(PLACE_ORDER);
        waitForUrlContaining("/orders/");
        waitForText(ORDER_STATUS, "PLACED");
        assertDisplayedPaise(ORDER_TOTAL, expectedTotalPaise);

        String path = driver.getCurrentUrl().replaceFirst("[?#].*$", "");
        return Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
    }

    private void assertDisplayedPaise(By locator, long expectedPaise) {
        new WebDriverWait(driver, DEFAULT_TIMEOUT).until(
            ignored -> displayedPaise(locator) == expectedPaise
        );
    }

    private long displayedPaise(By locator) {
        String decimal = visible(locator).getText().replaceAll("[^0-9.]", "");
        return new BigDecimal(decimal).movePointRight(2).longValueExact();
    }
}
