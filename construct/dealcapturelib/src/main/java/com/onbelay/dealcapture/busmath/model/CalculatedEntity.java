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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import com.onbelay.dealcapture.common.enums.CalculatedErrorType;

public abstract class CalculatedEntity {
	protected static final MathContext mathContext = MathContext.UNLIMITED;
    protected static final MathContext divisorMathContext = MathContext.DECIMAL128;
    protected static final BigDecimal ZERO;
    protected BigDecimal value = null;
    protected int roundingScale;
    protected List<CalculatedEntity> parts;
    protected CalculatedErrorType calculationErrorType = CalculatedErrorType.NO_ERROR;

    static {
        ZERO = new BigDecimal("0");
    }
    
    protected CalculatedEntity() {
    	
    }


    public abstract CalculatedEntity add(CalculatedEntity entity);
    public abstract CalculatedEntity subtract(CalculatedEntity entity);
    public abstract CalculatedEntity multiply(CalculatedEntity entity);
    public abstract CalculatedEntity divide(CalculatedEntity entity);


    protected CalculatedEntity(CalculatedErrorType error) {
    	this.calculationErrorType = error;
    }
    
    public CalculatedErrorType getError() {
    	return calculationErrorType;
    }
    
    public boolean isInError() {
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



}
