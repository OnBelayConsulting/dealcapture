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

import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.common.enums.UnitOfMeasureCode;

public class Quantity extends CalculatedEntity{
	
	private UnitOfMeasureCode unitOfMeasureCode;
	
	
	public Quantity(UnitOfMeasureCode unitOfMeasureCode, BigDecimal value) {
		super();
		this.unitOfMeasureCode = unitOfMeasureCode;
		this.value = value;
	}
	
	


	protected Quantity(CalculatedErrorType error) {
		super(error);
	}




	public BigDecimal getValue() {
		return value;
	}


	public UnitOfMeasureCode getUnitOfMeasureCode() {
		return unitOfMeasureCode;
	}


	
	
}
