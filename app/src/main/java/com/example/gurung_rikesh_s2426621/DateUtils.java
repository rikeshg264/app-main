package com.example.gurung_rikesh_s2426621;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for date and time formatting
 * Centralizes all date/time presentation logic following separation of concerns
 */
public final class DateUtils {

    private DateUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Format current time for summary display
     * Example: "Updated: 14:32, 25 Nov 2025"
     * @return Formatted timestamp string
     */
    public static String formatSummaryTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());
        return "Updated: " + dateFormat.format(new Date());
    }

    /**
     * Format current time for detail view
     * Example: "As of 25/11/2025 14:32"
     * @return Formatted timestamp string
     */
    public static String formatDetailTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.UK);
        return "As of " + sdf.format(new Date());
    }

    /**
     * Format current time for ViewModel last update tracking
     * Example: "14:32:15"
     * @return Formatted time string (HH:mm:ss)
     */
    public static String formatLastUpdateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
