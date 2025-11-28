package com.example.gurung_rikesh_s2426621;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gurung_rikesh_s2426621.R;
import com.example.gurung_rikesh_s2426621.CurrencyRate;
import com.example.gurung_rikesh_s2426621.CurrencyAdapter;
import com.example.gurung_rikesh_s2426621.CurrencyViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment displaying list of currency exchange rates from GBP to other currencies
 * Uses RecyclerView with custom adapter following MPD_03a1_RecyclerView pattern
 * Implements Fragment communication pattern from Task2_sol
 */
public class CurrencyListFragment extends Fragment {

    private static final String TAG = "CurrencyListFragment";

    // Interface for communication with MainActivity (Task2_sol pattern)
    public interface CurrencyListListener {
        void onCurrencySelected(CurrencyRate selectedRate);
    }

    private CurrencyListListener listener;
    private CurrencyViewModel viewModel;

    // UI Components
    private RecyclerView recyclerView;
    private CurrencyAdapter adapter;
    private EditText searchEditText;
    private ProgressBar loadingSpinner;
    private TextView statusTextView;
    private TextView currencyCountTextView;

    // Data - allRates is now managed by ViewModel

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Set up listener following Task2_sol pattern
        if (context instanceof CurrencyListListener) {
            listener = (CurrencyListListener) context;
            Log.d(TAG, "CurrencyListListener attached");
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement CurrencyListFragment.CurrencyListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        Log.d(TAG, "CurrencyListListener detached");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_currency, container, false);

        // Initialize UI components
        recyclerView = view.findViewById(R.id.currencyRecyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        statusTextView = view.findViewById(R.id.statusTextView);
        currencyCountTextView = view.findViewById(R.id.currencyCountTextView);


        // Set up RecyclerView with LinearLayoutManager (MPD_03a1 pattern)
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize adapter with context and click listener
        adapter = new CurrencyAdapter(getContext(), new ArrayList<>(), new CurrencyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CurrencyRate rate) {
                // Notify MainActivity of selection via interface
                if (listener != null) {
                    Log.d(TAG, "Currency selected: " + rate.getTargetCode());
                    listener.onCurrencySelected(rate);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        // Set up search functionality
        setupSearchListener();

        Log.d(TAG, "CurrencyListFragment view created");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get shared ViewModel from Activity scope
        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);

        // Setup observers for LiveData
        setupObservers();

        // Data fetching is handled by MainActivity - don't duplicate
        Log.d(TAG, "CurrencyListFragment observers set up, waiting for data...");
    }

    /**
     * Setup LiveData observers following MVVM pattern
     * Added lifecycle safety checks to prevent crashes
     */
    private void setupObservers() {
        // Observe currency rates data
        viewModel.getCurrencyRates().observe(getViewLifecycleOwner(), currencyRates -> {
            // Safety check: ensure Fragment is still attached
            if (!isAdded() || getView() == null) {
                Log.w(TAG, "Fragment not attached, skipping UI update");
                return;
            }

            if (currencyRates != null && !currencyRates.isEmpty()) {
                Log.d(TAG, "Received " + currencyRates.size() + " currency rates");

                // Safely update adapter and UI
                if (adapter != null) {
                    adapter.updateData(currencyRates);
                }
                // Update currency count display
                updateCurrencyCount(currencyRates.size());

                if (statusTextView != null) {
                    statusTextView.setVisibility(View.GONE);
                }
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Safety check: ensure Fragment is still attached
            if (!isAdded() || getView() == null) {
                Log.w(TAG, "Fragment not attached, skipping loading state update");
                return;
            }

            if (isLoading != null && loadingSpinner != null) {
                loadingSpinner.setVisibility(isLoading ? View.VISIBLE : View.GONE);

                if (isLoading && statusTextView != null && recyclerView != null) {
                    statusTextView.setText("Loading currency data...");
                    statusTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });

        // Observe errors
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            // Safety check: ensure Fragment is still attached
            if (!isAdded() || getView() == null) {
                Log.w(TAG, "Fragment not attached, skipping error display");
                return;
            }

            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e(TAG, "Error: " + errorMessage);

                if (statusTextView != null && recyclerView != null) {
                    statusTextView.setText("Error: " + errorMessage);
                    statusTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Setup search functionality with real-time filtering
     * Searches by currency name, code, or country
     */
    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCurrencies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    /**
     * Filter currencies based on search query
     * Delegates to ViewModel for filtering logic
     */
    private void filterCurrencies(String query) {
        if (adapter == null) {
            Log.w(TAG, "Cannot filter: adapter is null");
            return;
        }

        // Use ViewModel's searchCurrencies method to filter
        List<CurrencyRate> filtered = viewModel.searchCurrencies(query);
        if (filtered != null) {
            adapter.updateData(filtered);
            updateCurrencyCount(filtered.size());
        }
    }

    /**
     * Update the currency count display
     * Handles singular/plural properly
     */
    private void updateCurrencyCount(int count) {
        if (currencyCountTextView != null) {
            String countText = count == 1 ?
                count + " currency listed" :
                count + " currencies listed";
            currencyCountTextView.setText(countText);
        }
    }
}
