package net.leozeballos.FastFood.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormattingUtils {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    public static final Locale ES_LOCALE = Locale.forLanguageTag("es-ES");

    private FormattingUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Formats a price value. Example: $10,00
     * @param price the price to format
     * @return the formatted price string
     */
    public static String formatPrice(double price) {
        return "$" + String.format(ES_LOCALE, "%.2f", price);
    }

    /**
     * Formats a LocalDateTime object. Example: 03/08/2026 14:30
     * @param dateTime the date time to format
     * @return the formatted date time string or "Not set" if null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "Not set" : dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * Formats an Enum state to Title Case. Example: CREATED -> Created
     * @param state the state to format
     * @return the formatted state string
     */
    public static String formatState(Enum<?> state) {
        if (state == null) return "Unknown";
        String s = state.toString();
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
