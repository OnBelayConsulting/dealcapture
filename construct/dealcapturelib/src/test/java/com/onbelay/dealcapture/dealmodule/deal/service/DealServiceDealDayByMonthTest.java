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
import com.onbelay.dealcapture.dealmodule.deal.model.*;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealDayByMonthRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayByMonthSnapshot;
import com.onbelay.shared.enums.CommodityCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DealServiceDealDayByMonthTest extends PhysicalDealServiceTestCase {

	private PhysicalDeal physicalDeal;

	@Autowired
	private DealDayByMonthRepository dealDayByMonthRepository;

	@Override
	public void setUp() {
		super.setUp();
		physicalDeal = DealFixture.createSamplePhysicalDeal(
				myBusinessContact,
				CommodityCode.CRUDE,
				"myDeal",
				companyRole,
				counterpartyRole,
                marketIndex);
		flush();
		DealDayByMonthFixture.createDayByMonthQuantity(
				physicalDeal,
				physicalDeal.getDealDetail().getStartDate(),
				2,
				BigDecimal.valueOf(30));
		flush();
		clearCache();
	}


	@Test
	public void fetchDealDayByMonths() {

		List<DealDayByMonthSnapshot> dealDays = dealService.fetchDealDayByMonths(physicalDeal.generateEntityId());
		assertEquals(1, dealDays.size());
	}

	@Test
	public void fetchDealDayView() {
		dealDayByMonthRepository.fetchDealDayViews(physicalDeal.generateEntityId());
		List<DealDayByMonthView> dealDays = dealService.fetchDealDayByMonthViews(physicalDeal.generateEntityId());
		assertEquals(1, dealDays.size());
	}


	@Test
	public void saveDealDayByMonths() {
		DealDayByMonthSnapshot dealDayByMonth = new DealDayByMonthSnapshot();
		dealDayByMonth.setDealId(physicalDeal.generateEntityId());
		dealDayByMonth.getDetail().setDealMonthDate(LocalDate.of(2024, 1, 1));
		for (int i = 1; i < 32; i++)
			dealDayByMonth.getDetail().setDayValue(i, BigDecimal.valueOf(i));
		TransactionResult result = dealService.saveDealDayByMonths(
				physicalDeal.generateEntityId(),
				List.of(dealDayByMonth));

		assertNotNull(result.getEntityId());
	}

}
