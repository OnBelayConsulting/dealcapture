package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EvaluationContext {

    private LocalDate startPositionDate;
    private LocalDate endPositionDate;
    private LocalDateTime createdDateTime;
    private CurrencyCode currencyCode;
    private UnitOfMeasureCode unitOfMeasureCode = UnitOfMeasureCode.GJ;

    public EvaluationContext() {

    }

    public boolean validate() {
        return startPositionDate != null && createdDateTime != null && currencyCode != null;
    }

    public static EvaluationContext build() {
        return new EvaluationContext();
    }

    public EvaluationContext withStartPositionDate(LocalDate positionDate) {
        this.startPositionDate = positionDate;
        return this;
    }

    public EvaluationContext withEndPositionDate(LocalDate positionDate) {
        this.endPositionDate = positionDate;
        return this;
    }


    public EvaluationContext withCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return this;
    }

    public EvaluationContext withCurrency(CurrencyCode currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public EvaluationContext withUnitOfMeasure(UnitOfMeasureCode unitOfMeasure) {
        this.unitOfMeasureCode = unitOfMeasure;
        return this;
    }

    public LocalDate getStartPositionDate() {
        return startPositionDate;
    }

    public LocalDate getEndPositionDate() {
        return endPositionDate;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }

    public UnitOfMeasureCode getUnitOfMeasureCode() {
        return unitOfMeasureCode;
    }

    public void setStartPositionDate(LocalDate startPositionDate) {
        this.startPositionDate = startPositionDate;
    }

    public void setEndPositionDate(LocalDate endPositionDate) {
        this.endPositionDate = endPositionDate;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public void setCurrencyCode(CurrencyCode currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setUnitOfMeasureCode(UnitOfMeasureCode unitOfMeasureCode) {
        this.unitOfMeasureCode = unitOfMeasureCode;
    }
}
