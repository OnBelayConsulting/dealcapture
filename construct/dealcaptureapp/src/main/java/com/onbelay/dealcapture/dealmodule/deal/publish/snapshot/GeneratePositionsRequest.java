package com.onbelay.dealcapture.dealmodule.deal.publish.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GeneratePositionsRequest {

    private List<Integer> dealIds = new ArrayList<>();
    private LocalDateTime requestDateTime = LocalDateTime.now();
    private LocalDateTime observedDateTime;
    private String currencyCodeValue;

    public GeneratePositionsRequest() {
    }

    public GeneratePositionsRequest(
            LocalDateTime observedDateTime,
            CurrencyCode currencyCode,
            List<Integer> dealIds) {

        this.dealIds = dealIds;
        this.currencyCodeValue = currencyCode.getCode();
        this.observedDateTime = observedDateTime;
    }

    public GeneratePositionsRequest(
            LocalDateTime observedDateTime,
            CurrencyCode currencyCode,
            Integer dealId) {

        this.dealIds.add(dealId);
        this.currencyCodeValue = currencyCode.getCode();
        this.observedDateTime = observedDateTime;
    }

    public List<Integer> getDealIds() {
        return dealIds;
    }

    public void setDealIds(List<Integer> dealIds) {
        this.dealIds = dealIds;
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(LocalDateTime requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public LocalDateTime getObservedDateTime() {
        return observedDateTime;
    }

    public void setObservedDateTime(LocalDateTime observedDateTime) {
        this.observedDateTime = observedDateTime;
    }

    @JsonIgnore
    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.lookUp(currencyCodeValue);
    }

    public void setCurrencyCode(CurrencyCode code) {
        this.currencyCodeValue = code.getCode();
    }

    public String getCurrencyCodeValue() {
        return currencyCodeValue;
    }

    public void setCurrencyCodeValue(String currencyCodeValue) {
        this.currencyCodeValue = currencyCodeValue;
    }
}
