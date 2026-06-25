package com.ust.sdet.support;

import java.util.Arrays;

public enum Browser {
    CHROME,
    FIREFOX,
    EDGE,
    SAFARI;

    public static Browser from(String value) {
        return Arrays.stream(values())
            .filter(browser -> browser.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported browser: " + value));
    }
}
