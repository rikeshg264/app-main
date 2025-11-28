package com.example.gurung_rikesh_s2426621;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class CurrencyRate implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String baseCurrency;
    private String baseCode;
    private String targetCurrency;
    private String targetCode;
    private String link;
    private String pubDate;
    private String description;
    private double rate;

    public CurrencyRate(){}

    public CurrencyRate(String title, String baseCurrency, String baseCode,
                        String targetCurrency, String targetCode, String link,
                        String pubDate, String description, double rate) {
        this.title = title;
        this.baseCurrency = baseCurrency;
        this.baseCode = baseCode;
        this.targetCurrency = targetCurrency;
        this.targetCode = targetCode;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.rate = rate;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public String getBaseCode() { return baseCode; }
    public void setBaseCode(String baseCode) { this.baseCode = baseCode; }

    public String getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }

    public String getTargetCode() { return targetCode; }
    public void setTargetCode(String targetCode) { this.targetCode = targetCode; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getPubDate() { return pubDate; }
    public void setPubDate(String pubDate) { this.pubDate = pubDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    @NonNull
    @Override
    public String toString() {
        return "CurrencyRate{" +
                "title='" + title + '\'' +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", baseCode='" + baseCode + '\'' +
                ", targetCurrency='" + targetCurrency + '\'' +
                ", targetCode='" + targetCode + '\'' +
                ", rate=" + rate +
                ", link='" + link + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
