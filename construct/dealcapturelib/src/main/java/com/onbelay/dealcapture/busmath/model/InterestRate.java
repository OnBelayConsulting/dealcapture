package com.onbelay.dealcapture.busmath.model;

import com.onbelay.dealcapture.busmath.exceptions.OBBusinessMathException;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;

import java.math.BigDecimal;

public class InterestRate extends CalculatedEntity{

    public InterestRate(CalculatedErrorType error) {
        super(error);
    }

    public InterestRate(BigDecimal value) {
        super(value);
    }

    @Override
    public CalculatedEntity add(CalculatedEntity entity) {
        if (entity == null)
            return new InterestRate(CalculatedErrorType.ERROR);

        if (this.isInError() || entity.isInError())
            return new InterestRate(CalculatedErrorType.ERROR);

        if (entity instanceof InterestRate == false)
            throw new OBBusinessMathException("Invalid operation on interest rate.");

        return new InterestRate(value.add(entity.getValue()));
    }

    @Override
    public CalculatedEntity subtract(CalculatedEntity entity) {
        if (entity == null)
            return new InterestRate(CalculatedErrorType.ERROR);

        if (this.isInError() || entity.isInError())
            return new InterestRate(CalculatedErrorType.ERROR);

        if (entity instanceof InterestRate == false)
            throw new OBBusinessMathException("Invalid operation on interest rate.");

        return new InterestRate(value.subtract(entity.getValue()));
    }

    @Override
    public CalculatedEntity multiply(CalculatedEntity entity) {
        throw new OBBusinessMathException("Invalid operation on interest rate.");
    }

    @Override
    public CalculatedEntity divide(CalculatedEntity entity) {
        throw new OBBusinessMathException("Invalid operation on interest rate.");
    }

    @Override
    public String toFormula() {
        if (calculationErrorType == CalculatedErrorType.NO_ERROR) {
            return getValue().toPlainString();
        }
        return "error";
    }
}
