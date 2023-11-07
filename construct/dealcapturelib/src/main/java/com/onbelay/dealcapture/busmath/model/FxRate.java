package com.onbelay.dealcapture.busmath.model;

import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.shared.enums.CurrencyCode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class FxRate extends CalculatedEntity {
    private static final int SCALE = 4;
    private CurrencyCode fromCurrencyCode;
    private CurrencyCode toCurrencyCode;
    private BigDecimal fxRate;

    public FxRate(
            final CurrencyCode fromCurrencyCode,
            final CurrencyCode toCurrencyCode,
            final BigDecimal fxRate) {
        this.fromCurrencyCode = fromCurrencyCode;
        this.toCurrencyCode = toCurrencyCode;
        this.fxRate = fxRate;
    }

    public FxRate(final CalculatedErrorType error) {
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

    public CurrencyCode getFromCurrencyCode() {
        return this.fromCurrencyCode;
    }

    public CurrencyCode getToCurrencyCode() {
        return this.toCurrencyCode;
    }

    public BigDecimal getFxRate() {
        return this.fxRate;
    }

    public BigDecimal getInvertedFxRate() {
        BigDecimal inverted = BigDecimal.ONE.divide(value, MathContext.DECIMAL128);
        return inverted.setScale(4, RoundingMode.HALF_EVEN);
    }
}
