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

import com.onbelay.dealcapture.busmath.exceptions.OBBusinessMathException;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

public class Price extends CalculatedEntity {
    public static final int PRICE_SCALE = 4;
	private CurrencyCode currency;
	private UnitOfMeasureCode unitOfMeasure;

	public Price(final CalculatedErrorType error) {
		super(error);
	}


	public Price(
			final BigDecimal value,
			final CurrencyCode currency,
			final UnitOfMeasureCode unitOfMeasure) {
		super(value);
		this.currency = currency;
		this.unitOfMeasure = unitOfMeasure;
	}

	public Price apply(FxRate rate) {
		if (rate == null)
			return new Price(CalculatedErrorType.ERROR);

		if (this.isInError() || rate.isInError())
			return new Price(CalculatedErrorType.ERROR);

		if (currency == rate.getFromCurrencyCode()) {
			BigDecimal converted = getValue().multiply(rate.getValue(), MathContext.DECIMAL128);
			converted = converted.setScale(PRICE_SCALE, RoundingMode.HALF_EVEN);
			return new Price(
					converted,
					rate.getToCurrencyCode(),
					unitOfMeasure);
		} else if (currency == rate.getToCurrencyCode()) {
			FxRate invertedRate = rate.getInversion();
			BigDecimal converted = getValue().multiply(invertedRate.getValue(), MathContext.DECIMAL128);
			converted = converted.setScale(PRICE_SCALE, RoundingMode.HALF_EVEN);
			return new Price(
					converted,
					invertedRate.getToCurrencyCode(),
					unitOfMeasure);
		} else {
			throw new OBBusinessMathException("Incompatible Currency");
		}
	}

	public Price roundPrice() {
		if (this.isInError())
			return this;

		return new Price(
				value.setScale(PRICE_SCALE, RoundingMode.HALF_EVEN),
				currency,
				unitOfMeasure);
	}

	@Override
	public CalculatedEntity add(CalculatedEntity entity) {
		if (entity instanceof Price price == false) {
			throw new OBBusinessMathException("Invalid calculated entity. Must be price.");
		} else {
			return add((Price) entity);
		}
	}

	@Override
	public CalculatedEntity subtract(CalculatedEntity entity) {
		if (entity instanceof Price price == false) {
			throw new OBBusinessMathException("Invalid calculated entity. Must be price.");
		} else {
			return subtract((Price) entity);
		}
	}

	@Override
	public CalculatedEntity multiply(CalculatedEntity entity) {
		if (entity instanceof Quantity quantity)
			return multiply(quantity);
		else if (entity instanceof  FxRate rate)
			return apply(rate);
		else if (entity instanceof  Conversion conversion)
			return apply(conversion);
		else
			throw new OBBusinessMathException("Invalid calculated entity. Must be quantity, fxRate or conversion.");
	}

	@Override
	public CalculatedEntity divide(CalculatedEntity entity) {
		throw new OBBusinessMathException("Invalid operation.");
	}

	public Price multiply(BigDecimal valueIn) {
		if (valueIn == null || isInError())
			return new Price(CalculatedErrorType.ERROR);
		return new Price(
				value.multiply(valueIn, mathContext),
				currency,
				unitOfMeasure);
	}


	public Price divide(BigDecimal valueIn) {
		if (valueIn == null || isInError())
			return new Price(CalculatedErrorType.ERROR);
		return new Price(
				value.divide(valueIn, divisorMathContext),
				currency,
				unitOfMeasure);
	}

	public CurrencyCode getCurrency() {
		return currency;
	}


	public UnitOfMeasureCode getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public Price add (Price priceIn) {
		if (this.isInError() || priceIn.isInError())
			return new Price(CalculatedErrorType.ERROR);

		if (!( this.currency == priceIn.currency && this.unitOfMeasure == priceIn.unitOfMeasure) )
			throw new OBBusinessMathException("Incompatible currency and/or UnitOfMeasure");

		if (hasValue() && priceIn.hasValue()) {

			return new Price(
					value.add(
							priceIn.getValue(),
							mathContext),
					this.currency,
					this.unitOfMeasure);
		}
		else {
			return new Price(CalculatedErrorType.ERROR);
		}
	}

	public Price subtract (Price priceIn) {
		if (this.isInError() || priceIn.isInError())
			return new Price(CalculatedErrorType.ERROR);

		if (!( this.currency == priceIn.currency && this.unitOfMeasure == priceIn.unitOfMeasure) )
			throw new OBBusinessMathException("Incompatible currency and/or UnitOfMeasure");

        if (hasValue() && priceIn.hasValue()) {
        	
            return new Price(
					value.subtract(
							priceIn.getValue(),
							mathContext),
            		this.currency,
            		this.unitOfMeasure);
        }
        else {
            return new Price(CalculatedErrorType.ERROR);
        }
	}

	public Price subtract (BigDecimal priceIn) {
        if (hasValue() && priceIn != null) {
        	
            return new Price(
					value.subtract(
							priceIn,
							mathContext),
            		this.currency,
            		this.unitOfMeasure);
        }
        else {
            return new Price(CalculatedErrorType.ERROR);
        }
	}

	public Price apply(Conversion conversion) {
		if (isInError() || conversion.isInError())
			return new Price(CalculatedErrorType.ERROR);

		if (this.unitOfMeasure == conversion.getFromUnitOfMeasure()) {
			return new Price(
					value.multiply(conversion.getValue(), mathContext),
					currency,
					conversion.getToUnitOfMeasure());
		}
		else {
			throw new OBBusinessMathException("Invalid UoM conversion.");
		}
	}

    public Amount multiply(Quantity quantity) {
		if (this.isInError() || quantity.isInError())
			return new Amount(CalculatedErrorType.ERROR);

		if (this.unitOfMeasure != quantity.getUnitOfMeasureCode())
			throw new OBBusinessMathException("Incompatible UnitOfMeasure");

        if (hasValue() && quantity.hasValue()) {
        	
            return new Amount(
            		value.multiply(
            				quantity.getValue(), 
            				mathContext),
					currency);
        }
        else {
            return new Amount(CalculatedErrorType.ERROR);
        }
    }

	@Override
	public String toFormula() {
		if (calculationErrorType == CalculatedErrorType.NO_ERROR) {
			return getValue().toPlainString() + " " + currency.getCode() + "/" + unitOfMeasure.getCode();
		}
		return "error";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Price price = (Price) o;
		if ( price.isInError() && isInError() == false)
			return false;
		if ( price.isInError() == false && isInError())
			return false;

		if (getValue().compareTo(price.getValue()) != 0)
			return false;
		return currency == price.currency && unitOfMeasure == price.unitOfMeasure;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getValue(), currency, unitOfMeasure);
	}
}
