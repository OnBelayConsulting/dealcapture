package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EvaluationContextRequest {

    private String queryText;
    private Integer dealId;
    private String currencyCodeValue;
    private String unitOfMeasureCodeValue;
    private LocalDateTime createdDateTime;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String positionGenerationIdentifier;

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public Integer getDealId() {
        return dealId;
    }

    public void setDealId(Integer dealId) {
        this.dealId = dealId;
    }

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
        return UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue);
    }

    public String getUnitOfMeasureCodeValue() {
        return unitOfMeasureCodeValue;
    }

    public void setUnitOfMeasureCodeValue(String unitOfMeasureCodeValue) {
        this.unitOfMeasureCodeValue = unitOfMeasureCodeValue;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
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

    public String getPositionGenerationIdentifier() {
        return positionGenerationIdentifier;
    }

    public void setPositionGenerationIdentifier(String positionGenerationIdentifier) {
        this.positionGenerationIdentifier = positionGenerationIdentifier;
    }
}
