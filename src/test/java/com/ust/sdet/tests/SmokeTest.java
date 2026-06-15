package com.ust.sdet.tests;

import com.ust.sdet.support.Config;
import com.ust.sdet.support.DriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SmokeTest {
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
    @DisplayName("Product catalog loads in a real Chrome session")
    void catalogLoads() {
        driver.get(Config.catalogUrl());

        By catalogHeading = By.cssSelector("[data-test='catalog-title']");

        assertAll(
            () -> assertTrue(driver.getTitle().contains("Catalog")),
            () -> assertTrue(driver.findElement(catalogHeading).isDisplayed()),
            () -> assertEquals("Product Catalog", driver.findElement(catalogHeading).getText())
        );
    }
}
