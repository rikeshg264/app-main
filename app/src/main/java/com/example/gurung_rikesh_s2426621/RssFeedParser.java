package com.example.gurung_rikesh_s2426621;

import android.util.Log;

import com.example.gurung_rikesh_s2426621.CurrencyRate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for RSS feed containing currency exchange rates
 * Extracted from CurrencyRepository for better separation of concerns
 */
public class RssFeedParser {

    private static final String TAG = "RssFeedParser";

    // Pattern to extract currency codes from title: "British Pound Sterling(GBP)/United Arab Emirates Dirham(AED)"
    private static final Pattern TITLE_PATTERN = Pattern.compile("([^(]+)\\(([A-Z]{3})\\)/([^(]+)\\(([A-Z]{3})\\)");

    // Pattern to extract rate from description: "1 British Pound Sterling = 4.9354 United Arab Emirates Dirham"
    private static final Pattern RATE_PATTERN = Pattern.compile("1\\s+[^=]+=\\s+([0-9.]+)");

    /**
     * Parses XML data containing currency exchange rates using PullParser approach
     * @param dataToParse XML string containing RSS feed data
     * @return List of parsed CurrencyRate objects
     */
    public List<CurrencyRate> parse(String dataToParse) {
        // Sanitize XML to handle malformed entities (e.g., unescaped & characters)
        dataToParse = sanitizeXml(dataToParse);

        List<CurrencyRate> results = new ArrayList<>();
        CurrencyRate current = null;
        String currentText = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            // Enable relaxed mode to handle malformed XML
            factory.setFeature("http://xmlpull.org/v1/doc/features.html#relaxed", true);

            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(dataToParse));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        String name = xpp.getName();

                        if ("item".equalsIgnoreCase(name)) {
                            current = new CurrencyRate();
                            Log.d(TAG, "New Currency Rate item found!");
                        }
                        break;
                    }

                    case XmlPullParser.TEXT: {
                        currentText = xpp.getText();
                        break;
                    }

                    case XmlPullParser.END_TAG: {
                        String name = xpp.getName();

                        if (current != null) {
                            if ("title".equalsIgnoreCase(name)) {
                                String title = safe(currentText);
                                current.setTitle(title);
                                parseTitle(current, title);
                                Log.d(TAG, "Title is " + title);
                            } else if ("link".equalsIgnoreCase(name)) {
                                String link = safe(currentText);
                                current.setLink(link);
                                Log.d(TAG, "Link is " + link);
                            } else if ("pubDate".equalsIgnoreCase(name)) {
                                String pubDate = safe(currentText);
                                current.setPubDate(pubDate);
                                Log.d(TAG, "PubDate is " + pubDate);
                            } else if ("description".equalsIgnoreCase(name)) {
                                String description = safe(currentText);
                                current.setDescription(description);
                                parseRate(current, description);
                                Log.d(TAG, "Description is " + description);
                            } else if ("item".equalsIgnoreCase(name)) {
                                // Only add valid entries with proper currency codes
                                if (isValidCurrencyRate(current)) {
                                    results.add(current);
                                    Log.d(TAG, "Currency Rate parsing completed: " + current.toString());
                                } else {
                                    Log.w(TAG, "Skipping invalid currency rate entry: " + current.getTitle());
                                }
                                current = null;
                            }
                        }
                        break;
                    }
                }

                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Parsing error: " + e, e);
        } catch (IOException e) {
            Log.e(TAG, "IO error during parsing", e);
        }

        return results;
    }

    /**
     * Validates that a CurrencyRate has all required fields
     * @param rate The currency rate to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidCurrencyRate(CurrencyRate rate) {
        if (rate == null) return false;

        String baseCode = rate.getBaseCode();
        String targetCode = rate.getTargetCode();

        // Must have valid 3-letter currency codes
        return baseCode != null && !baseCode.isEmpty() &&
               targetCode != null && !targetCode.isEmpty() &&
               baseCode.length() == 3 && targetCode.length() == 3;
    }

    /**
     * Extract currency names and codes from title
     * Example: "British Pound Sterling(GBP)/United Arab Emirates Dirham(AED)"
     */
    private void parseTitle(CurrencyRate rate, String title) {
        Matcher m = TITLE_PATTERN.matcher(title);
        if (m.find()) {
            rate.setBaseCurrency(safe(m.group(1)));
            rate.setBaseCode(safe(m.group(2)));
            rate.setTargetCurrency(safe(m.group(3)));
            rate.setTargetCode(safe(m.group(4)));
        } else {
            Log.w(TAG, "Could not parse currencies from title: " + title);
        }
    }

    /**
     * Extract exchange rate from description
     * Example: "1 British Pound Sterling = 4.9354 United Arab Emirates Dirham"
     */
    private void parseRate(CurrencyRate rate, String description) {
        Matcher m = RATE_PATTERN.matcher(description);
        if (m.find()) {
            rate.setRate(parseDoubleSafe(m.group(1)));
        } else {
            Log.w(TAG, "Could not parse rate from description: " + description);
        }
    }

    /**
     * Sanitizes XML data to handle common malformed entities
     */
    private String sanitizeXml(String xml) {
        if (xml == null || xml.isEmpty()) {
            return xml;
        }

        // Replace unescaped & characters (but preserve valid XML entities)
        return xml.replaceAll("&(?!(amp|lt|gt|quot|apos|#\\d+|#x[0-9a-fA-F]+);)", "&amp;");
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private static double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
