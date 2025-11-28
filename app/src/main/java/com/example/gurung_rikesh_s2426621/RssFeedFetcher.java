package com.example.gurung_rikesh_s2426621;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handles network operations to fetch RSS feed data
 * This class performs blocking I/O operations and should only be called from worker threads
 */
public class RssFeedFetcher {

    private static final String TAG = "RssFeedFetcher";
    private static final int TIMEOUT_MS = 10000; // 10 second timeout

    /**
     * Downloads RSS feed data from the given URL
     * This is a blocking operation - must be called from a worker thread!
     *
     * @param urlString The URL of the RSS feed to fetch
     * @return XML string data from the RSS feed, or null if error occurs
     */
    public String fetchRssFeed(String urlString) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder result = new StringBuilder();

        try {
            Log.d(TAG, "Starting RSS feed download from: " + urlString);

            // Create URL and open connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(TIMEOUT_MS);
            connection.setReadTimeout(TIMEOUT_MS);
            connection.setRequestProperty("User-Agent", "FXMate/1.0");

            // Connect and check response code
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }

                Log.d(TAG, "Successfully downloaded RSS feed (" + result.length() + " characters)");
                return result.toString();

            } else {
                Log.e(TAG, "HTTP error: " + responseCode + " " + connection.getResponseMessage());
                return null;
            }

        } catch (IOException e) {
            Log.e(TAG, "Network error fetching RSS feed: " + e.getMessage(), e);
            return null;

        } finally {
            // Clean up resources
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing reader", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
