package com.ust.sdet.xpath.ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.ust.sdet.xpath.ui.locators.Xp;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class SeatMapPage {
    private final SelenideElement seatMap = $("#seat-map");
    private final SelenideElement operator = $("#seat-map .eyebrow");
    private final SelenideElement status = $("#seat-status");

    public SeatMapPage shouldBeOpenFor(String expectedOperator) {
        seatMap.shouldBe(visible);
        operator.shouldHave(exactText(expectedOperator));
        return this;
    }

    public SeatMapPage selectAvailableSeat(String seatNumber) {
        SelenideElement seat = $x(Xp.availableSeat(seatNumber)).shouldBe(visible);
        seat.click();
        seat.shouldHave(cssClass("selected"));
        status.shouldHave(exactText("Seat " + seatNumber + " selected."));
        return this;
    }
}
