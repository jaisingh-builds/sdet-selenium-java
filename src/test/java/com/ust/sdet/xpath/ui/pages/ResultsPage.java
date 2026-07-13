package com.ust.sdet.xpath.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.ust.sdet.xpath.ui.locators.Xp;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ResultsPage {
    private final SelenideElement heading = $("#results-title");
    private final SelenideElement bookingStatus = $("#booking-status");
    private final ElementsCollection flightCards = $$("[class*='flight-card']");

    public ResultsPage shouldBeOpen() {
        heading.shouldBe(visible).shouldHave(exactText("Available trips"));
        flightCards.shouldHave(sizeGreaterThan(2));
        return this;
    }

    public ResultsPage shouldShowFlight(String flightNumber) {
        $x(Xp.flightCard(flightNumber)).shouldBe(visible);
        return this;
    }

    public ResultsPage shouldShowPrice(String flightNumber, String expectedPrice) {
        $x(Xp.flightPrice(flightNumber)).shouldHave(exactText(expectedPrice));
        return this;
    }

    public ResultsPage bookFlight(String flightNumber) {
        $x(Xp.flightBookButton(flightNumber)).shouldBe(visible).click();
        bookingStatus.shouldHave(exactText("Flight " + flightNumber + " selected."));
        return this;
    }

    public ResultsPage showBuses() {
        $x(Xp.tab("Buses")).shouldBe(visible).click();
        $("#bus-results").shouldBe(visible);
        return this;
    }

    public SeatMapPage selectSeatsForOperator(String operator) {
        $x(Xp.busSelectSeatsButton(operator)).shouldBe(visible).click();
        bookingStatus.shouldHave(exactText(operator + " seat map opened."));
        return new SeatMapPage().shouldBeOpenFor(operator);
    }

    public int flightCount() {
        return flightCards.size();
    }
}
