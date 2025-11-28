package com.example.gurung_rikesh_s2426621;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gurung_rikesh_s2426621.CurrencyRate;
import com.example.gurung_rikesh_s2426621.CurrencyRepository;
import com.example.gurung_rikesh_s2426621.DateUtils;

import java.util.List;

public class CurrencyViewModel extends ViewModel {

    private static final String TAG = "CurrencyViewModel";

    // Auto-update configuration
    // Default: 1 hour (3600000 ms) - can be reduced for demo purposes
    // For demo: use 5 minutes (300000 ms) or even 1 minute (60000 ms)
    private static final long AUTO_UPDATE_INTERVAL_MS = 60000; // 1 minutes for demo

    private final CurrencyRepository repository;
    private final MutableLiveData<List<CurrencyRate>> currencyRates;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<String> lastUpdateTime;

    // Guard flag to prevent multiple simultaneous fetches
    private boolean isFetching = false;

    // Handler for periodic updates
    private final Handler autoUpdateHandler;
    private boolean autoUpdateEnabled = false;

    // Runnable for periodic updates
    private final Runnable autoUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (autoUpdateEnabled) {
                Log.d(TAG, "Auto-update triggered (interval: " + (AUTO_UPDATE_INTERVAL_MS / 1000) + "s)");
                refreshCurrencyData();
                // Schedule next update
                autoUpdateHandler.postDelayed(this, AUTO_UPDATE_INTERVAL_MS);
            }
        }
    };

    public CurrencyViewModel() {
        repository = CurrencyRepository.getInstance();
        currencyRates = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        lastUpdateTime = new MutableLiveData<>();
        autoUpdateHandler = new Handler(Looper.getMainLooper());
    }

    public LiveData<List<CurrencyRate>> getCurrencyRates() {
        return currencyRates;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Fetches currency data from RSS feed using background thread
     * This method uses the Repository's Handler pattern implementation
     * The callback will be invoked on the main thread, making it safe to update LiveData
     *
     * Note: Always fetches fresh data to ensure exchange rates are current.
     * The isFetching guard prevents duplicate simultaneous fetches.
     */
    public void fetchCurrencyData() {
        // Guard: prevent multiple simultaneous fetches
        if (isFetching) {
            Log.w(TAG, "Fetch already in progress, ignoring duplicate request");
            return;
        }

        performFetch();
    }

    /**
     * Refreshes currency data from RSS feed (forces update even if data exists)
     * This method is used for periodic auto-updates and manual refresh
     */
    public void refreshCurrencyData() {
        // Guard: prevent multiple simultaneous fetches
        if (isFetching) {
            Log.w(TAG, "Fetch already in progress, ignoring duplicate refresh request");
            return;
        }

        Log.d(TAG, "Refreshing currency data...");
        performFetch();
    }

    /**
     * Internal method to perform the actual fetch operation
     */
    private void performFetch() {
        isFetching = true;
        isLoading.setValue(true);
        errorMessage.setValue(null);

        Log.d(TAG, "Requesting currency data from repository...");

        // Call repository method which handles threading internally
        repository.fetchAndParseRates(new CurrencyRepository.DataCallback() {
            @Override
            public void onDataLoaded(List<CurrencyRate> rates) {
                // This runs on main thread thanks to Handler.post() in repository
                Log.d(TAG, "Successfully received " + rates.size() + " currency rates");
                currencyRates.setValue(rates);
                isLoading.setValue(false);
                isFetching = false;

                // Update last update time
                updateLastUpdateTime();
            }

            @Override
            public void onError(String error) {
                // This runs on main thread thanks to Handler.post() in repository
                Log.e(TAG, "Error loading currency data: " + error);
                errorMessage.setValue(error);
                isLoading.setValue(false);
                isFetching = false;
            }
        });
    }

    /**
     * Updates the last update timestamp using DateUtils for consistent formatting
     */
    private void updateLastUpdateTime() {
        String currentTime = DateUtils.formatLastUpdateTime();
        lastUpdateTime.setValue(currentTime);
        Log.d(TAG, "Data updated at: " + currentTime);
    }

    /**
     * Loads and parses currency data from XML string (for testing purposes)
     * @param xmlData XML string containing RSS feed data
     */
    public void loadCurrencyData(String xmlData) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        try {
            Log.d(TAG, "Starting to parse currency data...");
            List<CurrencyRate> rates = repository.parseRates(xmlData);

            if (rates != null && !rates.isEmpty()) {
                currencyRates.setValue(rates);
                Log.d(TAG, "Successfully loaded " + rates.size() + " currency rates");
            } else {
                errorMessage.setValue("No currency data found");
                Log.w(TAG, "No currency rates parsed from XML");
            }
        } catch (Exception e) {
            errorMessage.setValue("Error parsing currency data: " + e.getMessage());
            Log.e(TAG, "Error loading currency data", e);
        } finally {
            isLoading.setValue(false);
        }
    }

    /**
     * Filters currency rates by search query (currency name, code, or country)
     * @param query Search query
     * @return Filtered list of currency rates
     */
    public List<CurrencyRate> searchCurrencies(String query) {
        List<CurrencyRate> allRates = currencyRates.getValue();
        if (allRates == null || query == null || query.trim().isEmpty()) {
            return allRates;
        }

        String searchQuery = query.toLowerCase().trim();
        List<CurrencyRate> filtered = new java.util.ArrayList<>();

        for (CurrencyRate rate : allRates) {
            if (rate.getTargetCurrency().toLowerCase().contains(searchQuery) ||
                rate.getTargetCode().toLowerCase().contains(searchQuery) ||
                rate.getTitle().toLowerCase().contains(searchQuery)) {
                filtered.add(rate);
            }
        }

        Log.d(TAG, "Search for '" + query + "' returned " + filtered.size() + " results");
        return filtered;
    }

    /**
     * Gets main currencies (USD, EUR, JPY)
     * @return List of main currency rates
     */
    public List<CurrencyRate> getMainCurrencies() {
        List<CurrencyRate> allRates = currencyRates.getValue();
        if (allRates == null) {
            return new java.util.ArrayList<>();
        }

        List<CurrencyRate> mainCurrencies = new java.util.ArrayList<>();
        for (CurrencyRate rate : allRates) {
            String code = rate.getTargetCode();
            if ("USD".equals(code) || "EUR".equals(code) || "JPY".equals(code)) {
                mainCurrencies.add(rate);
            }
        }

        Log.d(TAG, "Retrieved " + mainCurrencies.size() + " main currencies");
        return mainCurrencies;
    }

    /**
     * Starts automatic periodic updates of currency data
     * Updates will occur at intervals defined by AUTO_UPDATE_INTERVAL_MS
     */
    public void startAutoUpdate() {
        if (autoUpdateEnabled) {
            Log.d(TAG, "Auto-update already enabled");
            return;
        }

        autoUpdateEnabled = true;
        Log.d(TAG, "Starting auto-update (interval: " + (AUTO_UPDATE_INTERVAL_MS / 1000) + " seconds)");

        // Schedule first update
        autoUpdateHandler.postDelayed(autoUpdateRunnable, AUTO_UPDATE_INTERVAL_MS);
    }

    /**
     * Stops automatic periodic updates of currency data
     */
    public void stopAutoUpdate() {
        if (!autoUpdateEnabled) {
            Log.d(TAG, "Auto-update already disabled");
            return;
        }

        autoUpdateEnabled = false;
        autoUpdateHandler.removeCallbacks(autoUpdateRunnable);
        Log.d(TAG, "Auto-update stopped");
    }

    /**
     * Returns whether auto-update is currently enabled
     */
    public boolean isAutoUpdateEnabled() {
        return autoUpdateEnabled;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Stop auto-updates when ViewModel is destroyed
        stopAutoUpdate();
        Log.d(TAG, "ViewModel cleared");
    }
}
