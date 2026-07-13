package com.ust.sdet.xpath.ui.components;

import com.codeborne.selenide.SelenideElement;
import com.ust.sdet.xpath.ui.locators.Xp;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class Calendar {
    private static final int MAX_MONTH_MOVES = 12;

    private final SelenideElement trigger = $x(Xp.buttonAfterLabel("Departure"));
    private final SelenideElement monthHeading = $(".calendar-month");
    private final SelenideElement nextMonth = $(".DayPicker-NavButton--next");

    public Calendar selectDate(String targetMonth, String ariaLabel) {
        trigger.shouldBe(visible).click();
        moveToMonth(targetMonth);
        $x(Xp.calendarDay(ariaLabel)).shouldBe(visible, enabled).click();
        trigger.shouldHave(exactText(ariaLabel), attribute("data-selected-date", ariaLabel));
        return this;
    }

    public Calendar shouldHaveDate(String expectedDate) {
        trigger.shouldHave(exactText(expectedDate), attribute("data-selected-date", expectedDate));
        return this;
    }

    private void moveToMonth(String targetMonth) {
        monthHeading.shouldBe(visible);

        for (int move = 0; move < MAX_MONTH_MOVES; move++) {
            if (targetMonth.equals(monthHeading.getText())) {
                return;
            }
            nextMonth.shouldBe(visible, enabled).click();
        }

        monthHeading.shouldHave(exactText(targetMonth));
    }
}
