package com.onbelay.dealcapture.busmath.model;

import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;

import java.math.BigDecimal;

public class Conversion extends CalculatedEntity {
    
    private BigDecimal conversionValue;
    private UnitOfMeasureCode fromCurrency;
    private UnitOfMeasureCode toCurrency;

    public Conversion(
            final UnitOfMeasureCode fromCurrency, 
            final UnitOfMeasureCode toCurrency,
            final BigDecimal conversionValue) {
        
        this.conversionValue = conversionValue;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    public Conversion(final CalculatedErrorType error) {
        super(error);
    }

    @Override
    public CalculatedEntity add(CalculatedEntity entity) {
        return null;
    }

    @Override
    public CalculatedEntity subtract(CalculatedEntity entity) {
        return null;
    }

    @Override
    public CalculatedEntity multiply(CalculatedEntity entity) {
        return null;
    }

    @Override
    public CalculatedEntity divide(CalculatedEntity entity) {
        return null;
    }

    public BigDecimal getConversionValue() {
        return this.conversionValue;
    }

    public UnitOfMeasureCode getFromUnitOfMeasure() {
        return this.fromCurrency;
    }

    public UnitOfMeasureCode getToUnitOfMeasure() {
        return this.toCurrency;
    }
}
