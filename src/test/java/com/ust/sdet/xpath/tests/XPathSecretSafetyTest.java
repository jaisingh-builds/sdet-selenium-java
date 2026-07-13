package com.ust.sdet.xpath.tests;

import com.ust.sdet.xpath.data.secret.Secrets;
import com.ust.sdet.xpath.support.XPathDemoConfig;
import com.ust.sdet.xpath.ui.pages.SearchPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class XPathSecretSafetyTest {
    private static final String SECRET_KEY = "tripstack.demo.password";

    @BeforeAll
    static void configureBrowser() {
        XPathDemoConfig.applySecretSafe();
    }

    @AfterEach
    void closeBrowser() {
        closeWebDriver();
    }

    @Test
    @DisplayName("Runtime secret is masked in Allure and cleared from the password field")
    void signsInWithoutLeakingTheRuntimeSecret() {
        assumeTrue(
            Secrets.find(SECRET_KEY).isPresent(),
            "Set TRIPSTACK_DEMO_PASSWORD or create ignored secrets.local.properties"
        );

        String secret = Secrets.required(SECRET_KEY);
        new SearchPage()
            .open()
            .account()
            .signIn("alice@example.com", secret)
            .shouldShowSafeRequestFor("alice@example.com");
    }
}
