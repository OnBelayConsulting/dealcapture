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
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.util.Objects;

public class Quantity extends CalculatedEntity{
	
	private UnitOfMeasureCode unitOfMeasureCode;
	
	
	public Quantity(
			final BigDecimal value,
			final UnitOfMeasureCode unitOfMeasureCode) {
		super(value);
		this.unitOfMeasureCode = unitOfMeasureCode;
	}

	public Quantity(final CalculatedErrorType error) {
		super(error);
	}

	@Override
	public CalculatedEntity add(CalculatedEntity entity) {
		if (entity instanceof Quantity == false)
			throw new OBBusinessMathException("Invalid calculated entity. Must be Quantity");

		Quantity quantity = (Quantity) entity;
		return add(quantity);
	}

	public Quantity add(Quantity quantity) {
		if (isInError() || quantity.isInError())
			return new Quantity(CalculatedErrorType.ERROR);

		if (quantity.getUnitOfMeasureCode() != this.unitOfMeasureCode)
			throw new OBBusinessMathException("Incompatible UnitOfMeasure.");

		return new Quantity(
				value.add(quantity.value, mathContext),
				unitOfMeasureCode);
	}

	@Override
	public CalculatedEntity subtract(CalculatedEntity entity) {
		if (entity instanceof Quantity == false)
			throw new OBBusinessMathException("Invalid calculated entity.");
		Quantity quantity = (Quantity) entity;
		return subtract(quantity);
	}


	public Quantity subtract(Quantity quantity) {
		if (isInError() || quantity.isInError())
			return new Quantity(CalculatedErrorType.ERROR);

		if (quantity.getUnitOfMeasureCode() != this.unitOfMeasureCode)
			throw new OBBusinessMathException("Incompatible UnitOfMeasure.");

		return new Quantity(
				value.subtract(quantity.value, mathContext),
				unitOfMeasureCode);
	}


	@Override
	public CalculatedEntity multiply(CalculatedEntity entity) {
		if (entity instanceof Conversion conversion)
			return apply(conversion);
		if (entity instanceof Price price)
			return multiply(price);

		throw new OBBusinessMathException("Invalid Calculated Entity.");
	}

	public Quantity apply(Conversion conversion) {
		if (isInError() || conversion.isInError())
			return new Quantity(CalculatedErrorType.ERROR);

		if (unitOfMeasureCode == conversion.getFromUnitOfMeasure()) {
			return new Quantity(
					value.multiply(conversion.getValue()),
					conversion.getToUnitOfMeasure());
		} else {
			throw new OBBusinessMathException("Invalid conversion");
		}
	}

	@Override
	public CalculatedEntity divide(CalculatedEntity entity) {
		throw new OBBusinessMathException("Invalid Operation.");
	}

	@Override
	public String toFormula() {
		if (calculationErrorType == CalculatedErrorType.NO_ERROR) {
			return getValue().toPlainString() + " " + unitOfMeasureCode.getCode();
		}
		return "error";
	}

	public Amount multiply(Price price) {
		if (this.isInError() || price.isInError())
			return new Amount(CalculatedErrorType.ERROR);

		if (this.unitOfMeasureCode != price.getUnitOfMeasure())
			throw new OBBusinessMathException("Incompatible UnitOfMeasure.");

		return new Amount(
					value.multiply(
							price.getValue(),
							mathContext),
					price.getCurrency());
	}


	public UnitOfMeasureCode getUnitOfMeasureCode() {
		return unitOfMeasureCode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Quantity quantity = (Quantity) o;
		if (isInError() && quantity.isInError() == false)
			return false;
		if (isInError() == false && quantity.isInError())
			return false;
		if (value.compareTo(quantity.value) != 0)
			return false;
		return unitOfMeasureCode == quantity.unitOfMeasureCode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, unitOfMeasureCode);
	}
}
