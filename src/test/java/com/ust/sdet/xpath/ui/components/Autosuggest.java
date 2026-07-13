package com.ust.sdet.xpath.ui.components;

import com.codeborne.selenide.SelenideElement;
import com.ust.sdet.xpath.ui.locators.Xp;

import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class Autosuggest {
    private final SelenideElement input;

    public Autosuggest(String label) {
        this.input = $x(Xp.inputAfterLabel(label));
    }

    public Autosuggest select(String city) {
        input.shouldBe(visible).setValue(city);
        $x(Xp.suggestion(city)).shouldBe(visible).click();
        input.shouldHave(value(city));
        return this;
    }

    public Autosuggest shouldHaveValue(String expectedCity) {
        input.shouldHave(value(expectedCity));
        return this;
    }
}
