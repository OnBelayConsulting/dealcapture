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

import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealCost;
import com.onbelay.dealcapture.dealmodule.deal.model.DealCostFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSnapshot;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DealServiceDealCostTest extends PhysicalDealServiceTestCase {

	private PhysicalDeal physicalDeal;

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
		DealCostFixture.createPerUnitCost(
				physicalDeal,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				CostNameCode.FACILITY_PER_UNIT_FEE,
				BigDecimal.TEN);
		flush();
		clearCache();
	}


	@Test
	public void fetchCosts() {

		List<DealCostSnapshot> costs = dealService.fetchDealCosts(physicalDeal.generateEntityId());
		assertEquals(1, costs.size());
	}

	@Test
	public void saveCosts() {

		DealCostSnapshot costSnapshot = new DealCostSnapshot();
		costSnapshot.getDetail().setCostName(CostNameCode.BROKERAGE_DAILY_FEE);
		costSnapshot.getDetail().setCostValue(BigDecimal.valueOf(17.45));
		costSnapshot.getDetail().setCurrencyCode(CurrencyCode.CAD);


		dealService.saveDealCosts(
				physicalDeal.generateEntityId(),
				List.of(costSnapshot));
		flush();

		assertEquals(2, physicalDeal.fetchDealCosts().size());

		DealCost cost = physicalDeal.fetchDealCosts().stream().filter(c-> c.getDetail().getCostType() == CostTypeCode.FIXED).findFirst().get();
		assertEquals(CostNameCode.BROKERAGE_DAILY_FEE, cost.getDetail().getCostName());
		assertEquals(CostTypeCode.FIXED, cost.getDetail().getCostType());
		assertEquals(0, cost.getDetail().getCostValue().compareTo(BigDecimal.valueOf(17.45)));
		flush();
		clearCache();
		
		
	}

}
