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
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.shared.enums.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class DealServiceWithPowerProfileTest extends DealServiceWithPowerProfileTestCase {


	@Override
	public void setUp() {
		super.setUp();

	}

	@Test
	public void createIndexDeal() {

		PhysicalDealSnapshot snapshot = DealFixture.createIndexWithPowerProfilePhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"indexSellDeal",
				companyRole,
				counterpartyRole,
				powerProfile,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				dealPriceIndex);
		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());

		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		
		TransactionResult result = dealService.save(snapshot);
		flush();
		clearCache();
		
		PhysicalDeal physicalDeal = (PhysicalDeal) dealRepository.load(result.getEntityId());
		
		assertNotNull(physicalDeal.getPowerProfile());
	}

	@Test
	public void createFixedDeal() {
		PhysicalDealSnapshot snapshot = DealFixture.createWithPowerProfilePhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"fixedSellDeal",
				companyRole,
				counterpartyRole,
				powerProfile,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));

		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		TransactionResult result = dealService.save(snapshot);
		flush();
		clearCache();

		PhysicalDeal physicalDeal = (PhysicalDeal) dealRepository.load(result.getEntityId());
		assertNotNull(physicalDeal.getPowerProfile());
	}

}
