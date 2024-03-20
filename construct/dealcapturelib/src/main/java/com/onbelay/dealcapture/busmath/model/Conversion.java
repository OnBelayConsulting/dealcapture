package com.onbelay.dealcapture.busmath.model;

import com.onbelay.dealcapture.busmath.exceptions.OBBusinessMathException;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.util.Objects;

public class Conversion extends CalculatedEntity {
    
    private UnitOfMeasureCode fromUnitOfMeasure;
    private UnitOfMeasureCode toUnitOfMeasure;

    public Conversion(
            final BigDecimal conversionValue,
            final UnitOfMeasureCode toUnitOfMeasure,
            final UnitOfMeasureCode fromUnitOfMeasure) {
        super(conversionValue);
        this.fromUnitOfMeasure = fromUnitOfMeasure;
        this.toUnitOfMeasure = toUnitOfMeasure;
    }

    public Conversion getInversion() {
        return new Conversion(
                BigDecimal.ONE.divide(value, divisorMathContext),
                fromUnitOfMeasure,
                toUnitOfMeasure);
    }

    public Conversion(final CalculatedErrorType error) {
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

    @Override
    public CalculatedEntity multiply(CalculatedEntity entity) {
        throw new OBBusinessMathException("Invalid calculated entity");
    }

    @Override
    public CalculatedEntity divide(CalculatedEntity entity) {
        throw new OBBusinessMathException("Invalid calculated entity");
    }

    public UnitOfMeasureCode getFromUnitOfMeasure() {
        return this.fromUnitOfMeasure;
    }

    public UnitOfMeasureCode getToUnitOfMeasure() {
        return this.toUnitOfMeasure;
    }

    @Override
    public String toFormula() {
        if (isInError())
            return "error";
        return getValue().toPlainString() + " " + toUnitOfMeasure.getCode() + "/" + fromUnitOfMeasure.getCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversion that = (Conversion) o;
        if (isInError() && that.isInError() == false)
            return false;
        if (isInError() == false && that.isInError())
            return false;
        if (value.compareTo(that.value) != 0)
            return false;
        return fromUnitOfMeasure == that.fromUnitOfMeasure && toUnitOfMeasure == that.toUnitOfMeasure;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, fromUnitOfMeasure, toUnitOfMeasure);
    }
}
