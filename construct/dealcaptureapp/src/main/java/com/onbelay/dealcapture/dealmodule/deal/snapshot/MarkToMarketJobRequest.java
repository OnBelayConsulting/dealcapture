package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MarkToMarketJobRequest {
    private LocalDateTime createdDateTime;
    private String currencyCodeValue;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String powerProfileQueryText;
    private String dealQueryText;
    private String priceIndexQueryText;

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    @JsonIgnore
    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.lookUp(currencyCodeValue);
    }

    public void setCurrencyCode(CurrencyCode currencyCode) {
        this.currencyCodeValue = currencyCode.getCode();
    }


    public String getCurrencyCodeValue() {
        return currencyCodeValue;
    }

    public void setCurrencyCodeValue(String currencyCodeValue) {
        this.currencyCodeValue = currencyCodeValue;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getPowerProfileQueryText() {
        return powerProfileQueryText;
    }

    public void setPowerProfileQueryText(String powerProfileQueryText) {
        this.powerProfileQueryText = powerProfileQueryText;
    }

    public String getDealQueryText() {
        return dealQueryText;
    }

    public void setDealQueryText(String dealQueryText) {
        this.dealQueryText = dealQueryText;
    }

    public String getPriceIndexQueryText() {
        return priceIndexQueryText;
    }

    public void setPriceIndexQueryText(String priceIndexQueryText) {
        this.priceIndexQueryText = priceIndexQueryText;
    }
}
