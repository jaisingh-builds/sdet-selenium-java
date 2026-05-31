package com.ust.sdet.utils;

public final class TestConfig {
    private TestConfig() {
    }

    public static String baseUrl() {
        return System.getProperty("baseUrl", "http://localhost:4000");
    }
}
