/*
 Copyright 2019, OnBelay Consulting Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.  
 */
package com.onbelay.dealcapture.busmath.model;

import com.onbelay.dealcapture.common.enums.CalculatedErrorType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public abstract class CalculatedEntity {
	protected static final MathContext mathContext = MathContext.DECIMAL128;
    protected static final MathContext divisorMathContext = MathContext.DECIMAL128;
    protected static int roundingScale = 6;
    protected static final BigDecimal ZERO = BigDecimal.ZERO;
    protected BigDecimal value;
    protected CalculatedErrorType calculationErrorType = CalculatedErrorType.NO_ERROR;


    protected CalculatedEntity(final CalculatedErrorType error) {
        this.calculationErrorType = error;
    }


    protected CalculatedEntity(final BigDecimal value) {
        if (value == null)
            calculationErrorType = CalculatedErrorType.ERROR;
        else
    	    this.value = value.setScale(roundingScale, RoundingMode.HALF_UP);
    }

    public BigDecimal getInvertedValue() {
        BigDecimal inverted = BigDecimal.ONE.divide(value, MathContext.DECIMAL128);
        return inverted.setScale(6, RoundingMode.HALF_UP);
    }

    public abstract CalculatedEntity add(CalculatedEntity entity);
    public abstract CalculatedEntity subtract(CalculatedEntity entity);
    public abstract CalculatedEntity multiply(CalculatedEntity entity);
    public abstract CalculatedEntity divide(CalculatedEntity entity);

    public abstract String toFormula();

    public CalculatedErrorType getError() {
    	return calculationErrorType;
    }
    
    public boolean isInError() {
        if (value == null)
            return true;

    	return (calculationErrorType != CalculatedErrorType.NO_ERROR);
    }
    
    public boolean hasValue() {
    	if (isInError())
    		return false;
    	return (value != null);
    }

	public BigDecimal getValue() {
		return value;
	}


    public Double toDouble() {
        if (isInError() || value == null)
            throw new ArithmeticException("Cannot convert value to double");
        return value.doubleValue();
    }
}
