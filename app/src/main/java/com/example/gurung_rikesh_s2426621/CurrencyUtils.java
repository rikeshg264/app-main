package com.example.gurung_rikesh_s2426621;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;

import com.example.gurung_rikesh_s2426621.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Utility class for currency-related operations
 * Centralizes common functionality used across Views
 *
 * This class contains three types of utilities:
 * 1. FORMATTING: Pure functions for formatting rates and amounts (formatRate, formatAmount)
 * 2. COLOR CODING: Functions for determining colors based on exchange rates
 * 3. VIEW HELPERS: Android-specific helpers for setting View properties (setFlagIcon, setColorIndicatorGradient)
 * 4. CONVERSION: Pure mathematical currency conversion functions
 *
 * Note: View helper methods require Context and manipulate Views directly.
 * This is a common Android pattern for reusable View configuration.
 */
public final class CurrencyUtils {

    private CurrencyUtils() {
        // Private constructor to prevent instantiation
    }

    // ==================== CURRENCY TO COUNTRY MAPPING ====================

    /**
     * Static mapping of currency codes (ISO 4217) to country codes (ISO 3166-1 alpha-2)
     * Uses HashMap for O(1) lookup performance
     */
    private static final Map<String, String> CURRENCY_TO_COUNTRY = new HashMap<>();
    static {
        // Major currencies
        CURRENCY_TO_COUNTRY.put("USD", "us");    // United States Dollar
        CURRENCY_TO_COUNTRY.put("EUR", "eu");    // Euro
        CURRENCY_TO_COUNTRY.put("GBP", "gb");    // British Pound
        CURRENCY_TO_COUNTRY.put("JPY", "jp");    // Japanese Yen
        CURRENCY_TO_COUNTRY.put("CHF", "ch");    // Swiss Franc

        // Americas
        CURRENCY_TO_COUNTRY.put("CAD", "ca");    // Canadian Dollar
        CURRENCY_TO_COUNTRY.put("MXN", "mx");    // Mexican Peso
        CURRENCY_TO_COUNTRY.put("BRL", "br");    // Brazilian Real
        CURRENCY_TO_COUNTRY.put("ARS", "ar");    // Argentine Peso
        CURRENCY_TO_COUNTRY.put("CLP", "cl");    // Chilean Peso
        CURRENCY_TO_COUNTRY.put("COP", "co");    // Colombian Peso
        CURRENCY_TO_COUNTRY.put("PEN", "pe");    // Peruvian Sol
        CURRENCY_TO_COUNTRY.put("VEF", "ve");    // Venezuelan Bolívar
        CURRENCY_TO_COUNTRY.put("BOB", "bo");    // Bolivian Boliviano
        CURRENCY_TO_COUNTRY.put("UYU", "uy");    // Uruguayan Peso
        CURRENCY_TO_COUNTRY.put("ANG", "ang");   // Netherlands Antillean Guilder
        CURRENCY_TO_COUNTRY.put("XCD", "xcd");   // East Caribbean Dollar

        // Europe
        CURRENCY_TO_COUNTRY.put("NOK", "no");    // Norwegian Krone
        CURRENCY_TO_COUNTRY.put("SEK", "se");    // Swedish Krona
        CURRENCY_TO_COUNTRY.put("DKK", "dk");    // Danish Krone
        CURRENCY_TO_COUNTRY.put("ISK", "is");    // Icelandic Króna
        CURRENCY_TO_COUNTRY.put("CZK", "cz");    // Czech Koruna
        CURRENCY_TO_COUNTRY.put("PLN", "pl");    // Polish Złoty
        CURRENCY_TO_COUNTRY.put("HUF", "hu");    // Hungarian Forint
        CURRENCY_TO_COUNTRY.put("RON", "ro");    // Romanian Leu
        CURRENCY_TO_COUNTRY.put("BGN", "bg");    // Bulgarian Lev
        CURRENCY_TO_COUNTRY.put("HRK", "hr");    // Croatian Kuna
        CURRENCY_TO_COUNTRY.put("RSD", "rs");    // Serbian Dinar
        CURRENCY_TO_COUNTRY.put("UAH", "ua");    // Ukrainian Hryvnia
        CURRENCY_TO_COUNTRY.put("TRY", "tr");    // Turkish Lira
        CURRENCY_TO_COUNTRY.put("RUB", "ru");    // Russian Ruble

        // Asia-Pacific
        CURRENCY_TO_COUNTRY.put("CNY", "cn");    // Chinese Yuan
        CURRENCY_TO_COUNTRY.put("HKD", "hk");    // Hong Kong Dollar
        CURRENCY_TO_COUNTRY.put("TWD", "tw");    // Taiwan Dollar
        CURRENCY_TO_COUNTRY.put("KRW", "kr");    // South Korean Won
        CURRENCY_TO_COUNTRY.put("INR", "in");    // Indian Rupee
        CURRENCY_TO_COUNTRY.put("PKR", "pk");    // Pakistani Rupee
        CURRENCY_TO_COUNTRY.put("BDT", "bd");    // Bangladeshi Taka
        CURRENCY_TO_COUNTRY.put("LKR", "lk");    // Sri Lankan Rupee
        CURRENCY_TO_COUNTRY.put("NPR", "np");    // Nepalese Rupee
        CURRENCY_TO_COUNTRY.put("IDR", "id");    // Indonesian Rupiah
        CURRENCY_TO_COUNTRY.put("MYR", "my");    // Malaysian Ringgit
        CURRENCY_TO_COUNTRY.put("SGD", "sg");    // Singapore Dollar
        CURRENCY_TO_COUNTRY.put("THB", "th");    // Thai Baht
        CURRENCY_TO_COUNTRY.put("VND", "vn");    // Vietnamese Dong
        CURRENCY_TO_COUNTRY.put("PHP", "ph");    // Philippine Peso
        CURRENCY_TO_COUNTRY.put("AUD", "au");    // Australian Dollar
        CURRENCY_TO_COUNTRY.put("NZD", "nz");    // New Zealand Dollar
        CURRENCY_TO_COUNTRY.put("XPF", "xpf");         //Pacific Franc

        // Middle East
        CURRENCY_TO_COUNTRY.put("SAR", "sa");    // Saudi Riyal
        CURRENCY_TO_COUNTRY.put("AED", "ae");    // UAE Dirham
        CURRENCY_TO_COUNTRY.put("QAR", "qa");    // Qatari Riyal
        CURRENCY_TO_COUNTRY.put("KWD", "kw");    // Kuwaiti Dinar
        CURRENCY_TO_COUNTRY.put("BHD", "bh");    // Bahraini Dinar
        CURRENCY_TO_COUNTRY.put("OMR", "om");    // Omani Rial
        CURRENCY_TO_COUNTRY.put("JOD", "jo");    // Jordanian Dinar
        CURRENCY_TO_COUNTRY.put("ILS", "il");    // Israeli Shekel
        CURRENCY_TO_COUNTRY.put("IQD", "iq");    // Iraqi Dinar
        CURRENCY_TO_COUNTRY.put("IRR", "ir");    // Iranian Rial

        // Africa
        CURRENCY_TO_COUNTRY.put("ZAR", "za");    // South African Rand
        CURRENCY_TO_COUNTRY.put("EGP", "eg");    // Egyptian Pound
        CURRENCY_TO_COUNTRY.put("NGN", "ng");    // Nigerian Naira
        CURRENCY_TO_COUNTRY.put("KES", "ke");    // Kenyan Shilling
        CURRENCY_TO_COUNTRY.put("TZS", "tz");    // Tanzanian Shilling
        CURRENCY_TO_COUNTRY.put("UGX", "ug");    // Ugandan Shilling
        CURRENCY_TO_COUNTRY.put("GHS", "gh");    // Ghanaian Cedi
        CURRENCY_TO_COUNTRY.put("MAD", "ma");    // Moroccan Dirham
        CURRENCY_TO_COUNTRY.put("TND", "tn");    // Tunisian Dinar
        CURRENCY_TO_COUNTRY.put("DZD", "dz");    // Algerian Dinar
        CURRENCY_TO_COUNTRY.put("AOA", "ao");    // Angolan Kwanza
        CURRENCY_TO_COUNTRY.put("ETB", "et");    // Ethiopian Birr
        CURRENCY_TO_COUNTRY.put("XOF", "xof");   // West African CFA franc

        // Cryptocurrencies (no country flag - use fallback)
        CURRENCY_TO_COUNTRY.put("BTC", "bc");    // Bitcoin
        CURRENCY_TO_COUNTRY.put("ETH", "xx");    // Ethereum
        CURRENCY_TO_COUNTRY.put("XRP", "xx");    // Ripple
    }

    // ==================== FORMATTING ====================

    /**
     * Format exchange rate using UK/US format (period as decimal separator)
     * Examples: 1.24 | 157.8 | 4718.5
     */
    public static String formatRate(double rate) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.UK);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat("#,##0.##", symbols);
        return df.format(rate);
    }

    /**
     * Format exchange rate with 3 decimal places
     * Used for detailed rate display
     */
    public static String formatRateDetailed(double rate) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.UK);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("0.000", symbols);
        return df.format(rate);
    }

    /**
     * Format rate for summary display (1-2 decimal places based on magnitude)
     */
    public static String formatRateSummary(double rate) {
        if (rate >= 100) {
            return String.format(Locale.UK, "%.1f", rate);
        } else {
            return String.format(Locale.UK, "%.2f", rate);
        }
    }

    /**
     * Format amount for display (2 decimal places, period separator)
     */
    public static String formatAmount(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.UK);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(amount);
    }

    // ==================== COLOR CODING ====================

    /**
     * Determine background color based on exchange rate strength
     * Uses color resources from colors.xml following lab pattern
     * Color coding scale (5 colors as per requirements):
     * - Very High (rate >= 100): Dark Red/Pink - very weak target currency
     * - High (rate >= 10): Light Red/Pink - weak target currency
     * - Medium (rate >= 2): Light Yellow
     * - Low (rate >= 1): Light Green - strong target currency
     * - Very Low (rate < 1): Dark Green - very strong target currency
     */
    public static int getColorForRate(Context context, double rate) {
        if (rate >= 100) {
            return context.getResources().getColor(R.color.rate_very_high, null);
        } else if (rate >= 10) {
            return context.getResources().getColor(R.color.rate_high, null);
        } else if (rate >= 2) {
            return context.getResources().getColor(R.color.rate_medium, null);
        } else if (rate >= 1) {
            return context.getResources().getColor(R.color.rate_low, null);
        } else {
            return context.getResources().getColor(R.color.rate_very_low, null);
        }
    }

    /**
     * Set gradient color indicator bar based on exchange rate
     */
    public static void setColorIndicatorGradient(Context context, double rate) {
        int[] colors;

        if (rate >= 100) {
            colors = new int[]{
                context.getResources().getColor(R.color.gradient_very_high_start, null),
                context.getResources().getColor(R.color.gradient_very_high_mid, null),
                context.getResources().getColor(R.color.gradient_very_high_end, null)
            };
        } else if (rate >= 10) {
            colors = new int[]{
                context.getResources().getColor(R.color.gradient_high_start, null),
                context.getResources().getColor(R.color.gradient_high_mid, null),
                context.getResources().getColor(R.color.gradient_high_end, null)
            };
        } else if (rate >= 2) {
            colors = new int[]{
                context.getResources().getColor(R.color.gradient_medium_start, null),
                context.getResources().getColor(R.color.gradient_medium_mid, null),
                context.getResources().getColor(R.color.gradient_medium_end, null)
            };
        } else if (rate >= 1) {
            colors = new int[]{
                context.getResources().getColor(R.color.gradient_low_start, null),
                context.getResources().getColor(R.color.gradient_low_mid, null),
                context.getResources().getColor(R.color.gradient_low_end, null)
            };
        } else {
            colors = new int[]{
                context.getResources().getColor(R.color.gradient_very_low_start, null),
                context.getResources().getColor(R.color.gradient_very_low_mid, null),
                context.getResources().getColor(R.color.gradient_very_low_end, null)
            };
        }
    }

    // ==================== FLAG ICONS ====================

    /**
     * Set flag icon on an ImageView based on currency code
     */
    public static void setFlagIcon(Context context, ImageView imageView, String currencyCode) {
        if (context == null || imageView == null) return;

        int iconResource = getFlagResourceForCurrency(context, currencyCode);
        imageView.setImageResource(iconResource);
    }

    /**
     * Get flag icon resource for currency code
     * Maps 3-letter currency codes (ISO 4217) to flag drawable resources
     *
     * NOTE: Uses getIdentifier() for dynamic resource lookup - this is intentional.
     * With 260+ flag resources, dynamic lookup is more maintainable than hardcoded mappings.
     * This is a legitimate use case for runtime resource resolution where reflection is appropriate.
     *
     * @param context Android context for resource access
     * @param currencyCode 3-letter ISO 4217 currency code (e.g., "USD", "EUR")
     * @return Resource ID of the flag drawable, or default icon if not found
     */
    @SuppressLint("DiscouragedApi")  // Justified: Dynamic lookup for 260+ flags
    public static int getFlagResourceForCurrency(Context context, String currencyCode) {
        if (context == null) return android.R.drawable.ic_menu_mapmode;

        String countryCode = getCurrencyToCountryCode(currencyCode);
        String resourceName = "flag_" + countryCode;

        int resourceId = context.getResources().getIdentifier(
            resourceName,
            "drawable",
            context.getPackageName()
        );

        if (resourceId != 0) {
            return resourceId;
        } else {
            return android.R.drawable.ic_menu_mapmode;
        }
    }

    /**
     * Map currency codes (ISO 4217) to country codes (ISO 3166-1 alpha-2)
     * Uses HashMap lookup for O(1) performance
     */
    public static String getCurrencyToCountryCode(String currencyCode) {
        if (currencyCode == null) return "xx";

        String countryCode = CURRENCY_TO_COUNTRY.get(currencyCode);
        if (countryCode != null) {
            return countryCode;
        }

        // Fallback: use first 2 characters of currency code as country code
        if (currencyCode.length() >= 2) {
            return currencyCode.substring(0, 2).toLowerCase();
        }
        return "xx";  // Fallback to unknown country
    }

    // ==================== CONVERSION ====================

    /**
     * Convert amount from base currency to target currency
     * @param amount The amount to convert
     * @param rate The exchange rate (base to target)
     * @return Converted amount
     */
    public static double convertToTarget(double amount, double rate) {
        return amount * rate;
    }

    /**
     * Convert amount from target currency to base currency
     * @param amount The amount to convert
     * @param rate The exchange rate (base to target)
     * @return Converted amount
     */
    public static double convertToBase(double amount, double rate) {
        return amount / rate;
    }
}
