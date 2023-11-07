package com.onbelay.dealcapture.formulas.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.shared.enums.CurrencyCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EvaluationContext {

    private LocalDate startPositionDate;
    private LocalDate endPositionDate;
    private LocalDateTime observedDateTime = LocalDateTime.now();
    private CurrencyCode currencyCode;
    private UnitOfMeasureCode unitOfMeasureCode = UnitOfMeasureCode.GJ;

    protected EvaluationContext() {

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


    public EvaluationContext withObservedDateTime(LocalDateTime observedDateTime) {
        this.observedDateTime = observedDateTime;
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

    public LocalDateTime getObservedDateTime() {
        return observedDateTime;
    }

    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }

    public UnitOfMeasureCode getUnitOfMeasureCode() {
        return unitOfMeasureCode;
    }
}
