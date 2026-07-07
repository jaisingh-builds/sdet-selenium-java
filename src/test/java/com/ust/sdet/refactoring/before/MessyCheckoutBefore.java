package com.ust.sdet.refactoring.before;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Teaching sample only: this is the "before" code used in W6D1.
 * It is intentionally not a JUnit test because it demonstrates smells.
 */
public class MessyCheckoutBefore {
    public String checkoutFirstProduct() throws InterruptedException {
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("http://localhost:5173/catalog");

            Thread.sleep(2000);
            driver.findElement(By.cssSelector("[data-test='product-card'] a")).click();

            Thread.sleep(2000);
            String productName = driver.findElement(By.cssSelector("[data-test='detail-name']")).getText();

            driver.findElement(By.cssSelector("[data-test='add-to-cart']")).click();
            Thread.sleep(2000);

            driver.findElement(By.cssSelector("[data-test='checkout-button']")).click();
            Thread.sleep(2000);

            driver.findElement(By.cssSelector("[data-test='place-order']")).click();
            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='order-confirmation']")));

            return productName + " -> " + driver.findElement(By.cssSelector("[data-test='order-confirmation']")).getText();
        } finally {
            driver.quit();
        }
    }
}
