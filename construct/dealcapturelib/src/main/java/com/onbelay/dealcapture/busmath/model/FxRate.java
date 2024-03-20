package com.onbelay.dealcapture.busmath.model;

import com.onbelay.dealcapture.busmath.exceptions.OBBusinessMathException;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.shared.enums.CurrencyCode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

public class FxRate extends CalculatedEntity {
    private static final int SCALE = 4;
    private CurrencyCode fromCurrencyCode;
    private CurrencyCode toCurrencyCode;

    public FxRate(
            final BigDecimal fxRate,
            final CurrencyCode toCurrencyCode,
            final CurrencyCode fromCurrencyCode) {
        super(fxRate);
        this.fromCurrencyCode = fromCurrencyCode;
        this.toCurrencyCode = toCurrencyCode;
    }

    public FxRate getInversion() {
        if (isInError())
            return this;

        return new FxRate(
                BigDecimal.ONE.divide(value, divisorMathContext),
                fromCurrencyCode,
                toCurrencyCode);
    }

    public FxRate(final CalculatedErrorType error) {
        super(error);
    }

    @Override
    public CalculatedEntity add(CalculatedEntity entity) {
        throw new OBBusinessMathException("Invalid calculated entity");
    }

    @Override
    public CalculatedEntity subtract(CalculatedEntity entity) {
        throw new OBBusinessMathException("Invalid calculated entity");
    }

    public FxRate apply(FxRate rate) {
        if (this.isInError() || rate.isInError())
            return this;
        if (this.fromCurrencyCode != rate.toCurrencyCode)
            throw new OBBusinessMathException("Incompatible rate currencies.");
        BigDecimal converted = this.value.multiply(rate.getValue(), MathContext.DECIMAL128);
        return new FxRate(
                converted,
                this.toCurrencyCode,
                rate.fromCurrencyCode);
    }

    @Override
    public CalculatedEntity multiply(CalculatedEntity entity) {
        if (entity instanceof FxRate == false)
            throw new OBBusinessMathException("Invalid calculated entity");
        return apply((FxRate) entity);
    }

    @Override
    public CalculatedEntity divide(CalculatedEntity entity) {
        return new FxRate(CalculatedErrorType.ERROR);
    }

    public CurrencyCode getFromCurrencyCode() {
        return this.fromCurrencyCode;
    }

    public CurrencyCode getToCurrencyCode() {
        return this.toCurrencyCode;
    }

    @Override
    public String toFormula() {
        if (calculationErrorType == CalculatedErrorType.NO_ERROR) {
            return getValue().toPlainString() + " " + toCurrencyCode.getCode() + "/" + fromCurrencyCode.getCode();
        }
        return "error";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FxRate fxRate = (FxRate) o;
        if (isInError() && fxRate.isInError() == false)
            return false;
        if (isInError() == false && fxRate.isInError())
            return false;
        if (value.compareTo(fxRate.value) != 0)
            return false;

        return fromCurrencyCode == fxRate.fromCurrencyCode && toCurrencyCode == fxRate.toCurrencyCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, fromCurrencyCode, toCurrencyCode);
    }
}
