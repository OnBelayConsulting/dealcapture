package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EvaluationContextRequest {

    private String currencyCodeValue;
    private String unitofMeasureCodeValue;
    private LocalDateTime observedDateTime;
    private LocalDate fromDate;
    private LocalDate toDate;


    @JsonIgnore
    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.lookUp(currencyCodeValue);
    }

    public String getCurrencyCodeValue() {
        return currencyCodeValue;
    }

    public void setCurrencyCodeValue(String currencyCodeValue) {
        this.currencyCodeValue = currencyCodeValue;
    }

    @JsonIgnore
    public UnitOfMeasureCode getUnitOfMeasureCode() {
        return UnitOfMeasureCode.lookUp(unitofMeasureCodeValue);
    }

    public String getUnitofMeasureCodeValue() {
        return unitofMeasureCodeValue;
    }

    public void setUnitofMeasureCodeValue(String unitofMeasureCodeValue) {
        this.unitofMeasureCodeValue = unitofMeasureCodeValue;
    }

    public LocalDateTime getObservedDateTime() {
        return observedDateTime;
    }

    public void setObservedDateTime(LocalDateTime observedDateTime) {
        this.observedDateTime = observedDateTime;
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
}
