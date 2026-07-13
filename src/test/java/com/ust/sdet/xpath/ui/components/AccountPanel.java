package com.ust.sdet.xpath.ui.components;

import com.codeborne.selenide.SelenideElement;
import com.ust.sdet.xpath.data.secret.Secrets;
import com.ust.sdet.xpath.ui.locators.Xp;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Parameter;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class AccountPanel {
    private final SelenideElement email = $x(Xp.inputAfterLabel("Account email"));
    private final SelenideElement password = $x(Xp.inputAfterLabel("Account password"));
    private final SelenideElement signIn = $("#account-form button[type='submit']");
    private final SelenideElement status = $("#account-status");

    public AccountPanel signIn(String accountEmail, String secret) {
        Allure.parameter("Account", accountEmail);
        Allure.parameter("Password", Secrets.mask(secret), Parameter.Mode.MASKED);

        email.shouldBe(visible).setValue(accountEmail);
        password.shouldBe(visible).setValue(secret);
        signIn.click();
        return this;
    }

    public AccountPanel shouldShowSafeRequestFor(String accountEmail) {
        status.shouldHave(exactText("Secure sign-in request prepared for " + accountEmail + "."));
        password.shouldBe(empty);
        return this;
    }
}
