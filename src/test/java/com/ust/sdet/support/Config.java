package com.ust.sdet.support;

public final class Config {
    private Config() {
    }

    public static String baseUrl() {
        return System.getProperty("baseUrl", "http://localhost:5173").replaceAll("/$", "");
    }

    public static String catalogUrl() {
        return baseUrl() + "/catalog";
    }

    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("headless", "false"));
    }

    public static Browser browser() {
        return Browser.from(System.getProperty("browser", "chrome"));
    }

    public static long timeoutSeconds() {
        return Long.parseLong(System.getProperty("timeoutSeconds", "10"));
    }

    public static String gridUrl() {
        return System.getProperty("selenium.grid.url", "").trim();
    }

    public static boolean gridEnabled() {
        return !gridUrl().isBlank();
    }
}
