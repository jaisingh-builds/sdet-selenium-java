package com.ust.sdet.xpath.ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.ust.sdet.xpath.support.TripStackFixture;
import com.ust.sdet.xpath.ui.components.AccountPanel;
import com.ust.sdet.xpath.ui.components.Autosuggest;
import com.ust.sdet.xpath.ui.components.Calendar;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SearchPage {
    private final Autosuggest origin = new Autosuggest("From");
    private final Calendar calendar = new Calendar();
    private final SelenideElement title = $("#page-title");
    private final SelenideElement searchFlights = $("#search-flights");

    public SearchPage open() {
        TripStackFixture.openPage();
        title.shouldHave(exactText("Find the right trip, precisely."));
        searchFlights.shouldBe(visible);
        return this;
    }

    public SearchPage selectOrigin(String city) {
        origin.select(city);
        return this;
    }

    public SearchPage selectDeparture(String month, String ariaLabel) {
        calendar.selectDate(month, ariaLabel);
        return this;
    }

    public SearchPage shouldHaveSearchCriteria(String city, String ariaLabel) {
        origin.shouldHaveValue(city);
        calendar.shouldHaveDate(ariaLabel);
        return this;
    }

    public ResultsPage searchFlights() {
        searchFlights.click();
        return new ResultsPage().shouldBeOpen();
    }

    public AccountPanel account() {
        return new AccountPanel();
    }
}
