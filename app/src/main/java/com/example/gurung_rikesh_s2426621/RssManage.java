package com.example.gurung_rikesh_s2426621;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Single-file RSS manager that handles BOTH:
 * 1. Downloading RSS XML
 * 2. Parsing RSS XML into FeedItems
 *
 * This replaces RssFeedFetcher.java and RssFeedParser.java
 */
public class RssManage {

    private static final String TAG = "RssFeedManager";

    /**
     * Model class representing an RSS feed entry
     */
    public static class FeedItem {
        public String title = "";
        public String description = "";
        public String pubDate = "";
        public String link = "";

        @Override
        public String toString() {
            return "FeedItem{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", pubDate='" + pubDate + '\'' +
                    ", link='" + link + '\'' +
                    '}';
        }
    }

    /**
     * Fetch RSS feed from URL and return XML InputStream
     */
    private static InputStream fetchRss(String urlString) {
        try {
            Log.d(TAG, "Fetching RSS: " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.connect();

            return new BufferedInputStream(connection.getInputStream());

        } catch (Exception e) {
            Log.e(TAG, "Error fetching feed: ", e);
            return null;
        }
    }

    /**
     * Parse RSS feed InputStream into list of FeedItem objects
     */
    private static ArrayList<FeedItem> parseRss(InputStream inputStream) {
        ArrayList<FeedItem> items = new ArrayList<>();

        if (inputStream == null) {
            Log.e(TAG, "Cannot parse: inputStream is null");
            return items;
        }

        try {
            String text = "";
            FeedItem currentItem = null;

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);

            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, "UTF-8");

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                String tagName = parser.getName();

                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        if ("item".equalsIgnoreCase(tagName)) {
                            currentItem = new FeedItem();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (currentItem != null) {
                            switch (tagName) {
                                case "title":
                                    currentItem.title = text;
                                    break;
                                case "description":
                                    currentItem.description = text;
                                    break;
                                case "link":
                                    currentItem.link = text;
                                    break;
                                case "pubDate":
                                    currentItem.pubDate = text;
                                    break;
                                case "item":
                                    items.add(currentItem);
                                    break;
                            }
                        }
                        break;
                }

                eventType = parser.next();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing RSS: ", e);
        }

        return items;
    }

    /**
     * Public method to fetch + parse in one call
     */
    public static ArrayList<FeedItem> getFeed(String url) {
        InputStream stream = fetchRss(url);
        return parseRss(stream);
    }
}
