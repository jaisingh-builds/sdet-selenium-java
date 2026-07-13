package com.ust.sdet.xpath.ui.locators;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class Xp {
    private static final String SUGGESTION =
        "//li[contains(@class,'sg-item')][.//span[normalize-space()=%s]]";
    private static final String CALENDAR_DAY =
        "//button[contains(@class,'DayPicker-Day') and @aria-label=%s]";
    private static final String FLIGHT_CARD =
        "//*[normalize-space()=%s]/ancestor::*[contains(@class,'flight-card')]";
    private static final String BUS_CARD =
        "//*[normalize-space()=%s]/ancestor::*[contains(@class,'bus-card')]";

    private Xp() {
    }

    public static String inputAfterLabel(String label) {
        return "//label[normalize-space()=" + literal(label) + "]/following::input[1]";
    }

    public static String buttonAfterLabel(String label) {
        return "//label[normalize-space()=" + literal(label) + "]/following::button[1]";
    }

    public static String suggestion(String city) {
        return SUGGESTION.formatted(literal(city));
    }

    public static String calendarDay(String ariaLabel) {
        return CALENDAR_DAY.formatted(literal(ariaLabel));
    }

    public static String flightCard(String flightNumber) {
        return FLIGHT_CARD.formatted(literal(flightNumber));
    }

    public static String flightPrice(String flightNumber) {
        return flightCard(flightNumber) + "//*[contains(@class,'price')]";
    }

    public static String flightBookButton(String flightNumber) {
        return flightCard(flightNumber) + "//button[normalize-space()='Book']";
    }

    public static String busSelectSeatsButton(String operator) {
        return BUS_CARD.formatted(literal(operator))
            + "//button[normalize-space()='Select Seats']";
    }

    public static String availableSeat(String seatNumber) {
        return "//*[@data-seat=" + literal(seatNumber)
            + " and contains(@class,'available') and not(contains(@class,'booked'))]";
    }

    public static String firstAvailableSleeper() {
        return "(//*[contains(@class,'seat') and contains(@class,'sleeper')"
            + " and contains(@class,'available') and not(contains(@class,'booked'))])[1]";
    }

    public static String firstAvailableNotLadies() {
        return "(//*[contains(@class,'seat') and contains(@class,'available')"
            + " and not(contains(@class,'booked')) and not(contains(@class,'ladies'))])[1]";
    }

    public static String tab(String name) {
        return "//*[@role='tab' and normalize-space()=" + literal(name) + "]";
    }

    static String literal(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }

        return "concat("
            + Arrays.stream(value.split("'", -1))
                .map(part -> "'" + part + "'")
                .collect(Collectors.joining(", \"'\", "))
            + ")";
    }
}
