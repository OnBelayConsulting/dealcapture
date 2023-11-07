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
package com.onbelay.dealcapture.common.model;


import java.math.BigDecimal;

import com.onbelay.shared.enums.CurrencyCode;
import org.junit.jupiter.api.Test;

import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValueObjectTest {

	@Test
	public void testValues() {
		
		Price price = new Price(
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ, 
				BigDecimal.valueOf(3));
		
		Quantity quantity = new Quantity(
				UnitOfMeasureCode.GJ, 
				BigDecimal.valueOf(200));
		
		Amount amount = price.multipliedBy(quantity);
		
		BigDecimal expected = BigDecimal.valueOf(600);
		assertEquals(0, expected.compareTo(amount.getValue()));
		
	}
	
}
