package com.example.gurung_rikesh_s2426621;

import android.os.Handler;
import android.util.Log;

import com.example.gurung_rikesh_s2426621.RssFeedParser;
import com.example.gurung_rikesh_s2426621.CurrencyRate;
import com.example.gurung_rikesh_s2426621.RssFeedFetcher;

import java.util.List;

public class CurrencyRepository {

    private static final String TAG = "CurrencyRepository";
    private static final String RSS_FEED_URL = "https://www.fx-exchange.com/gbp/rss.xml";

    /**
     * Callback interface for asynchronous data fetching
     * This allows the Repository to communicate back to the ViewModel on the main thread
     */
    public interface DataCallback {
        void onDataLoaded(List<CurrencyRate> rates);
        void onError(String errorMessage);
    }

    // Thread-safe singleton using volatile and double-checked locking
    private static volatile CurrencyRepository instance;

    private final RssFeedParser parser;

    private CurrencyRepository() {
        parser = new RssFeedParser();
    }

    /**
     * Thread-safe singleton accessor using double-checked locking
     */
    public static CurrencyRepository getInstance() {
        if (instance == null) {
            synchronized (CurrencyRepository.class) {
                if (instance == null) {
                    instance = new CurrencyRepository();
                }
            }
        }
        return instance;
    }

    /**
     * Fetches and parses currency data from RSS feed using background thread
     *
     * Threading approach:
     * 1. Creates Handler on main thread (for UI updates)
     * 2. Spawns worker thread to fetch RSS feed and parse data
     * 3. Uses Handler.post() to send results back to main thread
     *
     * @param callback Callback to receive parsed data on main thread
     */
    public void fetchAndParseRates(final DataCallback callback) {
        // Create Handler bound to the main thread's message queue
        final Handler mHandler = new Handler();

        Log.d(TAG, "Starting background thread to fetch RSS feed...");

        // Create Thread to handle the long-running network operation
        new Thread(() -> {
            Log.d(TAG, "Worker thread started - fetching RSS feed from: " + RSS_FEED_URL);

            try {
                // Step 1: Fetch RSS feed from network (blocking I/O operation)
                RssFeedFetcher fetcher = new RssFeedFetcher();
                final String xmlData = fetcher.fetchRssFeed(RSS_FEED_URL);

                if (xmlData == null || xmlData.isEmpty()) {
                    // Network error - post error to main thread
                    mHandler.post(() -> callback.onError("Failed to download RSS feed"));
                    return;
                }

                Log.d(TAG, "RSS feed downloaded successfully, parsing XML...");

                // Step 2: Parse the XML data (still on worker thread)
                final List<CurrencyRate> rates = parser.parse(xmlData);

                if (rates == null || rates.isEmpty()) {
                    // Parsing error - post error to main thread
                    mHandler.post(() -> callback.onError("Failed to parse currency data"));
                    return;
                }

                Log.d(TAG, "Parsing complete. Posting " + rates.size() + " rates to main thread...");

                // Step 3: Post results to main thread using Handler
                // This ensures UI updates happen on the main thread
                mHandler.post(() -> callback.onDataLoaded(rates));

            } catch (Exception e) {
                Log.e(TAG, "Error in worker thread: " + e.getMessage(), e);

                // Post error to main thread
                final String errorMsg = e.getMessage();
                mHandler.post(() -> callback.onError("Error fetching data: " + errorMsg));
            }
        }).start(); // Start the worker thread
    }

    /**
     * Parses XML data containing currency exchange rates
     * Delegates to RssFeedParser for actual parsing
     * @param dataToParse XML string containing RSS feed data
     * @return List of parsed CurrencyRate objects
     */
    public List<CurrencyRate> parseRates(String dataToParse) {
        return parser.parse(dataToParse);
    }
}
