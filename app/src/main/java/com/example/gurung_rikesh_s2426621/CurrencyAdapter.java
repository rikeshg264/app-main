package com.example.gurung_rikesh_s2426621;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gurung_rikesh_s2426621.R;
import com.example.gurung_rikesh_s2426621.CurrencyRate;
import com.example.gurung_rikesh_s2426621.CurrencyUtils;

import java.util.List;

/**
 * RecyclerView Adapter for displaying currency exchange rates
 * Implements color coding based on exchange rate strength
 * Uses color resources from colors.xml following lab pattern
 */
public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {

    private List<CurrencyRate> currencyRates;
    private OnItemClickListener clickListener;
    private Context context;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(CurrencyRate rate);
    }

    // Constructor
    public CurrencyAdapter(Context context, List<CurrencyRate> currencyRates, OnItemClickListener clickListener) {
        this.context = context;
        this.currencyRates = currencyRates;
        this.clickListener = clickListener;
    }

    /**
     * ViewHolder class following RecyclerView pattern
     * Holds references to views for each item
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView flagImageView;
        public TextView currencyPairTextView;
        public TextView rateTextView;
        public View itemContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            flagImageView = itemView.findViewById(R.id.flagImageView);
            currencyPairTextView = itemView.findViewById(R.id.currencyPairTextView);
            rateTextView = itemView.findViewById(R.id.rateTextView);
            itemContainer = itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the currency_item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.currency_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CurrencyRate rate = currencyRates.get(position);

        // Set currency pair text (e.g., "GBP -> USD")
        String currencyPair = rate.getBaseCode() + " -> " + rate.getTargetCode();
        holder.currencyPairTextView.setText(currencyPair);

        // Format and display exchange rate using UK/US format (period for decimal)
        String formattedRate = CurrencyUtils.formatRate(rate.getRate());
        holder.rateTextView.setText(formattedRate);

        // Apply color coding based on exchange rate strength
        int backgroundColor = CurrencyUtils.getColorForRate(context, rate.getRate());
        holder.itemContainer.setBackgroundColor(backgroundColor);

        // Set flag icon based on target currency code
        CurrencyUtils.setFlagIcon(context, holder.flagImageView, rate.getTargetCode());

        // Handle click events
        holder.itemContainer.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(rate);
            }
        });
    }

    @Override
    public int getItemCount() {
        return currencyRates.size();
    }

    /**
     * Update adapter data and refresh RecyclerView
     */
    public void updateData(List<CurrencyRate> newRates) {
        this.currencyRates = newRates;
        notifyDataSetChanged();
    }
}
