package com.slyworks.cryptowatcher.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)

public class CryptoCoinEntity {
    //We are going to get a list of these entities from our api dial - this entity is immutable
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("cmc_rank")
    private String rank;
    @JsonProperty("circulating_supply")
    private String availableSupply;
    @JsonProperty("total_supply")
    private String totalSupply;
    @JsonProperty("last_updated")
    private String lastUpdated;

//    @JsonProperty("quote.USD.price")
    private String priceUsd;
//    @JsonProperty("quote.BTC.price")
    private String priceBtc;
//    @JsonProperty("quote.USD.volume_24h")
    private String _24hVolumeUsd;
//    @JsonProperty("quote.USD.market_cap")
    private String marketCapUsd;
//    @JsonProperty("quote.USD.percent_change_1h")
    private String percentChange1h;
//    @JsonProperty("quote.USD.percent_change_24h")
    private String percentChange24h;
//    @JsonProperty("quote.USD.percent_change_7d")
    private String percentChange7d;
    @JsonIgnore

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("cmc_rank")
    public String getRank() {
        return rank;
    }

    @JsonProperty("cmc_rank")
    public void setRank(String rank) {
        this.rank = rank;
    }

//    @JsonProperty("quote.USD.price")
    public String getPriceUsd() {
        return priceUsd;
    }

    @JsonProperty("circulating_supply")
    public String getAvailableSupply() {
        return availableSupply;
    }

    @JsonProperty("circulating_supply")
    public void setAvailableSupply(String availableSupply) {
        this.availableSupply = availableSupply;
    }

    @JsonProperty("total_supply")
    public String getTotalSupply() {
        return totalSupply;
    }

    @JsonProperty("total_supply")
    public void setTotalSupply(String totalSupply) {
        this.totalSupply = totalSupply;
    }

    @JsonProperty("last_updated")
    public String getLastUpdated() {
        return lastUpdated;
    }

    @JsonProperty("last_updated")
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    //    @JsonProperty("quote.USD.price")
    public void setPriceUsd(String priceUsd) {
        this.priceUsd = priceUsd;
    }

    //    @JsonProperty("quote.BTC.price")
    public String getPriceBtc() {
        return priceBtc;
    }

    //    @JsonProperty("quote.BTC.price")
    public void setPriceBtc(String priceBtc) {
        this.priceBtc = priceBtc;
    }

    //    @JsonProperty("quote.USD.volume_24h")
    public String get24hVolumeUsd() {
        return _24hVolumeUsd;
    }

    //    @JsonProperty("quote.USD.volume_24h")
    public void set24hVolumeUsd(String _24hVolumeUsd) {
        this._24hVolumeUsd = _24hVolumeUsd;
    }

    //    @JsonProperty("quote.USD.market_cap")
    public String getMarketCapUsd() {
        return marketCapUsd;
    }

    //    @JsonProperty("quote.USD.market_cap")
    public void setMarketCapUsd(String marketCapUsd) {
        this.marketCapUsd = marketCapUsd;
    }

    //    @JsonProperty("quote.USD.percent_change_1h")
    public String getPercentChange1h() {
        return percentChange1h;
    }

//    @JsonProperty("quote.USD.percent_change_1h")
    public void setPercentChange1h(String percentChange1h) {
        this.percentChange1h = percentChange1h;
    }

//    @JsonProperty("quote.USD.percent_change_24h")
    public String getPercentChange24h() {
        return percentChange24h;
    }

//    @JsonProperty("quote.USD.percent_change_24h")
    public void setPercentChange24h(String percentChange24h) {
        this.percentChange24h = percentChange24h;
    }

//    @JsonProperty("quote.USD.percent_change_7d")
    public String getPercentChange7d() {
        return percentChange7d;
    }

//    @JsonProperty("quote.USD.percent_change_7d")
    public void setPercentChange7d(String percentChange7d) {
        this.percentChange7d = percentChange7d;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("quote")
    private void unpackNestedQuote(Map<String, Object> quote) {
        Map<String, Object> USD = (Map<String, Object>)quote.get("USD");
        this.priceUsd = USD.get("price").toString();
        this._24hVolumeUsd = USD.get("volume_24h").toString();
        this.marketCapUsd = USD.get("market_cap").toString();
        this.percentChange1h = USD.get("percent_change_1h").toString();
        this.percentChange24h = USD.get("percent_change_24h").toString();
        this.percentChange7d = USD.get("percent_change_7d").toString();
    }

    @Override
    public String toString() {
        return "CryptoCoinEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", rank='" + rank + '\'' +
                ", priceUsd='" + priceUsd + '\'' +
                ", priceBtc='" + priceBtc + '\'' +
                ", _24hVolumeUsd='" + _24hVolumeUsd + '\'' +
                ", marketCapUsd='" + marketCapUsd + '\'' +
                ", availableSupply='" + availableSupply + '\'' +
                ", totalSupply='" + totalSupply + '\'' +
                ", percentChange1h='" + percentChange1h + '\'' +
                ", percentChange24h='" + percentChange24h + '\'' +
                ", percentChange7d='" + percentChange7d + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }
}
