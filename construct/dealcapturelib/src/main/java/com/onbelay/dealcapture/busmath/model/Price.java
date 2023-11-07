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
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.shared.enums.CurrencyCode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Price extends CalculatedEntity {
    public static final int PRICE_SCALE = 4;
	private CurrencyCode currency;
	private UnitOfMeasureCode unitOfMeasure;
	
	
	public Price(CurrencyCode currency, UnitOfMeasureCode unitOfMeasure, BigDecimal value) {
		super();
		this.currency = currency;
		this.unitOfMeasure = unitOfMeasure;
		this.value = value;
	}

	public Price convertWith(FxRate rate) {
		if (currency == rate.getFromCurrencyCode()) {
			BigDecimal converted = getValue().multiply(rate.getFxRate(), MathContext.DECIMAL128);
			converted = converted.setScale(PRICE_SCALE, RoundingMode.HALF_EVEN);
			return new Price(
					rate.getToCurrencyCode(),
					unitOfMeasure,
					converted);
		} else {
			BigDecimal converted = getValue().multiply(rate.getInvertedFxRate(), MathContext.DECIMAL128);
			converted = converted.setScale(PRICE_SCALE, RoundingMode.HALF_EVEN);
			return new Price(
					rate.getToCurrencyCode(),
					unitOfMeasure,
					converted);

		}
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

	public Price(CalculatedErrorType error) {
		super(error);
	}

	public CurrencyCode getCurrency() {
		return currency;
	}


	public UnitOfMeasureCode getUnitOfMeasure() {
		return unitOfMeasure;
	}
	
	public Price subtract (Price priceIn) {
        if (hasValue() && priceIn.hasValue()) {
        	
            return new Price(
            		this.currency,
            		this.unitOfMeasure,
            		value.subtract(
            				priceIn.getValue(), 
            				mathContext)); 
        }
        else {
            return new Price(CalculatedErrorType.ERROR);
        }
	}

	public Price subtract (BigDecimal priceIn) {
        if (hasValue() && priceIn != null) {
        	
            return new Price(
            		this.currency,
            		this.unitOfMeasure,
            		value.subtract(
            				priceIn, 
            				mathContext)); 
        }
        else {
            return new Price(CalculatedErrorType.ERROR);
        }
	}

    public Amount multipliedBy(Quantity quantity) {
        if (hasValue() && quantity.hasValue()) {
        	
            return new Amount(
            		currency, 
            		value.multiply(
            				quantity.getValue(), 
            				mathContext)); 
        }
        else {
            return new Amount(CalculatedErrorType.ERROR);
        }
    }

}
