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

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDayFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDayView;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealHourByDayRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDaySnapshot;
import com.onbelay.shared.enums.CommodityCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DealServiceDealHourByDayTest extends DealServiceTestCase {

	private PhysicalDeal physicalDeal;

	@Autowired
	private DealHourByDayRepository dealHourByDayRepository;

	@Override
	public void setUp() {
		super.setUp();
		physicalDeal = DealFixture.createSamplePhysicalDeal(
				CommodityCode.CRUDE,
				"myDeal",
				companyRole,
				counterpartyRole,
                marketIndex);
		flush();
		DealHourByDayFixture.createHourByDayQuantity(
				physicalDeal,
				physicalDeal.getDealDetail().getStartDate(),
				2,
				2,
				7,
				BigDecimal.valueOf(30));
		flush();
		clearCache();
	}


	@Test
	public void fetchDealDayByMonths() {

		List<DealHourByDaySnapshot> dealDays = dealService.fetchDealHourByDays(physicalDeal.generateEntityId());
		assertEquals(1, dealDays.size());
	}

	@Test
	public void fetchDealDayView() {
		List<DealHourByDayView> dealDays = dealService.fetchDealDealHourByDayViews(physicalDeal.generateEntityId());
		assertEquals(1, dealDays.size());
	}


	@Test
	public void saveDealHourByDays() {
		DealHourByDaySnapshot dealHourByDay = new DealHourByDaySnapshot();
		dealHourByDay.setDealId(physicalDeal.generateEntityId());
		dealHourByDay.getDetail().setDealDayDate(LocalDate.of(2024, 1, 1));
		for (int i = 1; i < 25; i++)
			dealHourByDay.getDetail().setHourValue(i, BigDecimal.valueOf(i));
		TransactionResult result = dealService.saveDealHourByDays(
				physicalDeal.generateEntityId(),
				List.of(dealHourByDay));
		
		assertNotNull(result.getEntityId());
	}

}
