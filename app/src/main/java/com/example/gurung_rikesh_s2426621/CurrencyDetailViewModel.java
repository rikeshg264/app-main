package com.example.gurung_rikesh_s2426621;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gurung_rikesh_s2426621.CurrencyRate;
import com.example.gurung_rikesh_s2426621.CurrencyUtils;

/**
 * ViewModel for CurrencyDetailFragment
 * Handles currency conversion business logic
 */
public class CurrencyDetailViewModel extends ViewModel {

    private CurrencyRate currencyRate;
    private boolean isSwapped = false;

    private final MutableLiveData<String> topAmount = new MutableLiveData<>("");
    private final MutableLiveData<String> bottomAmount = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> swapState = new MutableLiveData<>(false);

    /**
     * Set the currency rate to use for conversions
     */
    public void setCurrencyRate(CurrencyRate rate) {
        this.currencyRate = rate;
    }

    /**
     * Get the currency rate
     */
    public CurrencyRate getCurrencyRate() {
        return currencyRate;
    }

    /**
     * Get swap state as LiveData
     */
    public LiveData<Boolean> getSwapState() {
        return swapState;
    }

    /**
     * Get top amount as LiveData
     */
    public LiveData<String> getTopAmount() {
        return topAmount;
    }

    /**
     * Get bottom amount as LiveData
     */
    public LiveData<String> getBottomAmount() {
        return bottomAmount;
    }

    /**
     * Check if currencies are swapped
     */
    public boolean isSwapped() {
        return isSwapped;
    }

    /**
     * Get the top currency code based on swap state
     */
    public String getTopCurrencyCode() {
        if (currencyRate == null) return "";
        return isSwapped ? currencyRate.getTargetCode() : currencyRate.getBaseCode();
    }

    /**
     * Get the bottom currency code based on swap state
     */
    public String getBottomCurrencyCode() {
        if (currencyRate == null) return "";
        return isSwapped ? currencyRate.getBaseCode() : currencyRate.getTargetCode();
    }

    /**
     * Swap the conversion direction
     */
    public void swapCurrencies() {
        isSwapped = !isSwapped;
        swapState.setValue(isSwapped);

        // Swap the amounts
        String top = topAmount.getValue();
        String bottom = bottomAmount.getValue();
        topAmount.setValue(bottom != null ? bottom : "");
        bottomAmount.setValue(top != null ? top : "");
    }

    /**
     * Convert from top currency to bottom currency
     * @param topText The amount in top currency
     * @return The converted amount formatted as string, or empty string if invalid
     */
    public String convertTopToBottom(String topText) {
        if (currencyRate == null || topText == null || topText.isEmpty()) {
            return "";
        }

        try {
            String cleanText = topText.replace(',', '.');
            double amount = Double.parseDouble(cleanText);
            double result;

            if (isSwapped) {
                // Converting from target currency to base (divide by rate)
                result = CurrencyUtils.convertToBase(amount, currencyRate.getRate());
            } else {
                // Converting from base to target currency (multiply by rate)
                result = CurrencyUtils.convertToTarget(amount, currencyRate.getRate());
            }

            return CurrencyUtils.formatAmount(result);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /**
     * Convert from bottom currency to top currency
     * @param bottomText The amount in bottom currency
     * @return The converted amount formatted as string, or empty string if invalid
     */
    public String convertBottomToTop(String bottomText) {
        if (currencyRate == null || bottomText == null || bottomText.isEmpty()) {
            return "";
        }

        try {
            String cleanText = bottomText.replace(',', '.');
            double amount = Double.parseDouble(cleanText);
            double result;

            if (isSwapped) {
                // Converting from base to target currency (multiply by rate)
                result = CurrencyUtils.convertToTarget(amount, currencyRate.getRate());
            } else {
                // Converting from target to base currency (divide by rate)
                result = CurrencyUtils.convertToBase(amount, currencyRate.getRate());
            }

            return CurrencyUtils.formatAmount(result);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /**
     * Set initial amount for conversion
     */
    public void setInitialAmount(String amount) {
        topAmount.setValue(amount);
        String converted = convertTopToBottom(amount);
        bottomAmount.setValue(converted);
    }
}
