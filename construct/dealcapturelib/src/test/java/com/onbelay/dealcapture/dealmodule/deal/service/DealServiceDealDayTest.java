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
package com.onbelay.dealcapture.dealmodule.deal.service;

import com.onbelay.dealcapture.dealmodule.deal.model.*;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealDayRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDaySnapshot;
import com.onbelay.shared.enums.CommodityCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DealServiceDealDayTest extends DealServiceTestCase {

	private PhysicalDeal physicalDeal;

	@Autowired
	private DealDayRepository dealDayRepository;

	@Override
	public void setUp() {
		super.setUp();
		physicalDeal = DealFixture.createFixedPricePhysicalDeal(
				CommodityCode.CRUDE,
				"myDeal",
				companyRole,
				counterpartyRole,
				priceIndex);
		flush();
		DealDayFixture.createDayQuantity(
				physicalDeal,
				physicalDeal.getDealDetail().getStartDate(),
				2,
				BigDecimal.valueOf(30));
		flush();
		clearCache();
	}


	@Test
	public void fetchDealDays() {

		List<DealDaySnapshot> dealDays = dealService.fetchDealDays(physicalDeal.generateEntityId());
		assertEquals(1, dealDays.size());
	}

	@Test
	public void fetchDealDayView() {
		dealDayRepository.fetchDealDayViews(physicalDeal.generateEntityId());
		List<DealDayView> dealDays = dealService.fetchDealDayViews(physicalDeal.generateEntityId());
		assertEquals(1, dealDays.size());
	}


	@Test
	public void saveDealDays() {

	}

}
