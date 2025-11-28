package com.example.gurung_rikesh_s2426621;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

/**
 * Main Activity implementing Fragment-based architecture
 */
public class MainActivity extends AppCompatActivity
        implements  CurrencyListFragment.CurrencyListListener {

    private static final String TAG = "FXMate";
    private CurrencyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize shared ViewModel
        viewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);

        // AUTO-UPDATE REQUIREMENT: Fetch currency data on EVERY startup
        // This ensures fresh exchange rates even after configuration changes or app restart
        // This triggers the Handler+Thread pattern in CurrencyRepository
        Log.d(TAG, "Initiating currency data fetch on startup...");
        viewModel.fetchCurrencyData();

        // Load SummaryFragment on first launch (shows main currencies only)
        if (savedInstanceState == null) {
            loadCurrencyListFragment();

            // AUTO-UPDATE REQUIREMENT: Start periodic updates at regular intervals
            // This enables automatic background updates every AUTO_UPDATE_INTERVAL_MS
            Log.d(TAG, "Starting automatic periodic updates...");
            viewModel.startAutoUpdate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume auto-updates when app comes to foreground
        if (!viewModel.isAutoUpdateEnabled()) {
            Log.d(TAG, "Resuming auto-update on activity resume");
            viewModel.startAutoUpdate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause auto-updates when app goes to background to save battery/data
        if (viewModel.isAutoUpdateEnabled()) {
            Log.d(TAG, "Pausing auto-update on activity pause");
            viewModel.stopAutoUpdate();
        }
    }

    /**
     * Load SummaryFragment into the container (default view)
     */
    /*private void loadSummaryFragment() {
        Log.d(TAG, "Loading SummaryFragment...");

        SummaryFragment summaryFragment = new SummaryFragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, summaryFragment);
        transaction.commit();
    }*/

    /**
     * Load CurrencyListFragment into the container
     */
    private void loadCurrencyListFragment() {
        Log.d(TAG, "Loading CurrencyListFragment...");

        CurrencyListFragment listFragment = new CurrencyListFragment();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, listFragment);
        transaction.addToBackStack(null); // Enable back navigation to Summary
        transaction.commit();
    }

    /**
     * SummaryFragment.SummaryListener implementation
     * Called when user clicks "Show All Currencies" from summary
     */
    public void onShowAllCurrencies() {
        Log.d(TAG, "Show all currencies requested");
        loadCurrencyListFragment();
    }

    /**
     * CurrencyListFragment.CurrencyListListener implementation
     * Called when user selects a currency from the list
     * Navigates to CurrencyDetailFragment
     */
    @Override
    public void onCurrencySelected(CurrencyRate selectedRate) {
        Log.d(TAG, "Currency selected: " + selectedRate.getTargetCode());

        // Create detail fragment with selected currency
        CurrencyDetailFragment detailFragment = CurrencyDetailFragment.newInstance(selectedRate);

        // Navigate to detail fragment with back stack
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, detailFragment);
        transaction.addToBackStack(null); // Enable back navigation
        transaction.commit();
    }
}
