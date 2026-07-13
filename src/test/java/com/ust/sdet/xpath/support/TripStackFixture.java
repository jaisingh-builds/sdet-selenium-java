package com.ust.sdet.xpath.support;

import java.net.URL;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.open;

public final class TripStackFixture {
    private TripStackFixture() {
    }

    public static void openPage() {
        URL page = Objects.requireNonNull(
            TripStackFixture.class.getClassLoader().getResource("xpath-demo/tripstack.html"),
            "TripStack fixture was not copied to the test classpath"
        );
        open(page.toExternalForm());
    }
}
