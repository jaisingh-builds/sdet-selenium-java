package com.ust.sdet.selenide.tests;

import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.ust.sdet.selenide.support.SelenideTestConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

@ExtendWith(ScreenShooterExtension.class)
class SelenideFailureDemo {

    @BeforeAll
    static void configureSelenide() {
        SelenideTestConfig.apply();
    }

    @Test
    @DisplayName("Intentional failure: wrong result count shows Selenide evidence")
    void wrongResultCountShowsSelenideEvidence() {
        open("/catalog");

        $("[data-test='search-input']").setValue("headphones");
        $("[data-test='search-button']").click();

        $("[data-test='catalog-result-count']").shouldHave(text("Showing 2 products"));
    }
}
