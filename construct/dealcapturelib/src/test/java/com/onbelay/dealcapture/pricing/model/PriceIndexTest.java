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
package com.onbelay.dealcapture.pricing.model;

import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceIndexTest extends DealCaptureSpringTestCase {
	
	private PricingLocation pricingLocation;

	@Autowired
	private PriceIndexRepositoryBean pricingIndexRepository;

	@Override
	public void setUp() {
		super.setUp();
		pricingLocation = PricingLocationFixture.createPricingLocation("west");
		flush();
	}
	
	
	@Test
	public void createIndex() {
		
		PriceIndex created = PriceIndexFixture.createPriceIndex(
				"westidx", 
				pricingLocation);
		flush();
		
		PriceIndex priceIndex = pricingIndexRepository.load(created.generateEntityId());
		
		assertEquals("westidx", priceIndex.getDetail().getName());
		
	}
	
}
