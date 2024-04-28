package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DealPositionsEvaluationContext extends EvaluationContext {

    private final CurrencyCode currencyCode;
    private UnitOfMeasureCode unitOfMeasureCode = UnitOfMeasureCode.GJ;

    public DealPositionsEvaluationContext(
        CurrencyCode currencyCode,
        LocalDateTime createdDateTime,
        LocalDate startPositionDate,
        LocalDate endPositionDate) {

        super(createdDateTime, startPositionDate, endPositionDate);
        this.currencyCode = currencyCode;
    }

    public boolean validate() {
        super.validate();
        return currencyCode != null;
    }

    public DealPositionsEvaluationContext withUnitOfMeasureCode(UnitOfMeasureCode unitOfMeasureCode) {
        this.unitOfMeasureCode = unitOfMeasureCode;
        return this;
    }

    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }

    public UnitOfMeasureCode getUnitOfMeasureCode() {
        return unitOfMeasureCode;
    }
}
