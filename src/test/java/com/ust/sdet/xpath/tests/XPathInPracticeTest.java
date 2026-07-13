package com.ust.sdet.xpath.tests;

import com.codeborne.selenide.junit5.ScreenShooterExtension;
import com.ust.sdet.xpath.support.XPathDemoConfig;
import com.ust.sdet.xpath.ui.locators.Xp;
import com.ust.sdet.xpath.ui.pages.ResultsPage;
import com.ust.sdet.xpath.ui.pages.SearchPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ScreenShooterExtension.class)
class XPathInPracticeTest {
    private static final String CITY = "Delhi";
    private static final String MONTH = "August 2026";
    private static final String DATE = "Fri Aug 21 2026";

    @BeforeAll
    static void configureBrowser() {
        XPathDemoConfig.apply();
    }

    @AfterEach
    void closeBrowser() {
        closeWebDriver();
    }

    @Test
    @DisplayName("Autosuggest and calendar use text, attributes and a capped month loop")
    void searchesWithAutosuggestAndCalendar() {
        ResultsPage results = completedSearch();

        assertEquals(3, results.flightCount());
        results.shouldShowFlight("6E-2043");
    }

    @Test
    @DisplayName("Flight details remain inside the card anchored by flight number")
    void anchorsPriceAndBookActionToNamedFlight() {
        completedSearch()
            .shouldShowFlight("6E-2043")
            .shouldShowPrice("6E-2043", "\u20b9 4,899")
            .bookFlight("6E-2043");
    }

    @Test
    @DisplayName("Bus operator and seat state prevent selecting the wrong repeated control")
    void selectsAnAvailableSeatForNamedOperator() {
        completedSearch()
            .showBuses()
            .selectSeatsForOperator("GreenLine Travels")
            .selectAvailableSeat("L12");
    }

    @Test
    @DisplayName("Exercise locators each resolve to one intended element")
    void exerciseLocatorsAreUnique() {
        completedSearch().showBuses().selectSeatsForOperator("GreenLine Travels");

        $$x(Xp.busSelectSeatsButton("GreenLine Travels")).shouldHave(size(1));
        $$x(Xp.availableSeat("L12")).shouldHave(size(1));
        $$x(Xp.firstAvailableSleeper()).shouldHave(size(1));
        $$x(Xp.firstAvailableNotLadies()).shouldHave(size(1));
        $$x(Xp.inputAfterLabel("Full name")).shouldHave(size(1));
    }

    private ResultsPage completedSearch() {
        return new SearchPage()
            .open()
            .selectOrigin(CITY)
            .selectDeparture(MONTH, DATE)
            .shouldHaveSearchCriteria(CITY, DATE)
            .searchFlights();
    }
}
