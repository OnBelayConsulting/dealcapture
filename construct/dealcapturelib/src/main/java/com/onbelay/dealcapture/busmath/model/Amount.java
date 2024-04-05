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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

public class Amount extends CalculatedEntity {
	private static final int amountScale = 3;
	private CurrencyCode currency;


	public Amount(final CalculatedErrorType error) {
		super(error);
	}

	public Amount(
			final BigDecimal value,
			final CurrencyCode currency) {
		super(value);
		this.currency = currency;
	}

	public Amount round() {
		if (isInError())
			return this;

		return new Amount(
				value.setScale(3, RoundingMode.HALF_UP),
				this.currency);
	}

	@Override
	public CalculatedEntity add(CalculatedEntity entity) {
		if (entity instanceof  Amount == false)
			throw new OBBusinessMathException("not an amount");
		Amount amount = (Amount) entity;
		return add(amount);
	}

	public Amount add(Amount amount) {
		if (this.isInError() || amount.isInError())
			return new Amount(CalculatedErrorType.ERROR);

		if (this.currency != amount.currency)
			throw new OBBusinessMathException("Incompatible Currency");
		return new Amount(
				value.add(amount.value, mathContext),
				currency);
	}

	@Override
	public CalculatedEntity subtract(CalculatedEntity entity) {
		if (entity instanceof  Amount == false)
			throw new OBBusinessMathException("not an amount");
		Amount amount = (Amount) entity;
		return subtract(amount);
	}

	public Amount subtract(Amount amount) {
		if (this.isInError() || amount.isInError())
			return new Amount(CalculatedErrorType.ERROR);

		if (this.currency != amount.currency)
			throw new OBBusinessMathException("Incompatible Currency");
		return new Amount(
				value.subtract(amount.value, mathContext),
				currency);

	}

	@Override
	public CalculatedEntity multiply(CalculatedEntity entity) {
		if (entity instanceof FxRate == false)
			throw new OBBusinessMathException("Invalid calculated entity");
		FxRate rate = (FxRate) entity;
		return apply(rate);
	}

	public Amount apply(FxRate rate) {
		if (rate == null)
			return new Amount(CalculatedErrorType.ERROR);

		if (isInError() || rate.isInError())
			return new Amount(CalculatedErrorType.ERROR);

		if (this.currency == rate.getFromCurrencyCode()) {
			return new Amount(
					value.multiply(rate.getValue(), mathContext),
					rate.getToCurrencyCode());
		} else if (this.currency == rate.getToCurrencyCode()) {
			return new Amount(
					value.multiply(rate.getInvertedValue(), mathContext),
					rate.getFromCurrencyCode());

		} else {
			throw new OBBusinessMathException("Invalid Fx Rate");
		}
	}

	@Override
	public CalculatedEntity divide(CalculatedEntity entity) {
		if (entity instanceof Quantity quantity) {
			return divide(quantity);
		} else if (entity instanceof Price price) {
			return divide(price);
		} else {
			throw new OBBusinessMathException("Invalid calculated entity");
		}
	}

	public Price divide(Quantity quantity) {
		if (isInError() || quantity.isInError())
			return new Price(CalculatedErrorType.ERROR);
		return new Price(
				value.divide(quantity.value, divisorMathContext),
				currency,
				quantity.getUnitOfMeasureCode());
	}

	public Amount divide(BigDecimal valueIn) {
		if (isInError() || valueIn == null)
			return new Amount(CalculatedErrorType.ERROR);
		return new Amount(
				value.divide(valueIn, divisorMathContext),
				currency);
	}

	public Amount negate() {
		if (isInError())
			return this;

		return new Amount(
				this.value.negate(MathContext.DECIMAL128),
				this.currency);
	}

	@Override
	public String toFormula() {
		if (calculationErrorType == CalculatedErrorType.NO_ERROR) {
			return getValue().toPlainString() + " " + currency.getCode();
		}
		return "error";
	}


	public CurrencyCode getCurrency() {
		return currency;
	}

	public Quantity divide(Price price) {
		if (isInError() || price.isInError())
			return new Quantity(CalculatedErrorType.ERROR);
		if (this.currency != price.getCurrency())
			throw new OBBusinessMathException("Incompatible currency");

		return new Quantity(
				value.divide(price.getValue(), divisorMathContext),
				price.getUnitOfMeasure());

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Amount amount = (Amount) o;
		if (isInError() && amount.isInError() == false)
			return false;
		if (isInError() == false && amount.isInError())
			return false;
		if (this.value.compareTo(amount.value) != 0)
			return false;
		return currency == amount.currency;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, currency);
	}
}
