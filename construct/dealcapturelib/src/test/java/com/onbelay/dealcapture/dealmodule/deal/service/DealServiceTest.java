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

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary;
import com.onbelay.shared.enums.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DealServiceTest extends PhysicalDealServiceTestCase {


	@Override
	public void setUp() {
		super.setUp();

	}

	@Test
	// TicketNo is defined as an alternative unique business key for deal
	public void findByTicketNo() {
		EntityId entityId = new EntityId(fixedPriceBuyDeal.getDealDetail().getTicketNo());
		BaseDealSnapshot deal = dealService.load(entityId);
		assertNotNull(deal);
	}

	@Test
	public void findByContainsTicketNo() {
		DefinedQuery definedQuery = new DefinedQuery("BaseDeal");
		definedQuery.getWhereClause().addExpression(
				new DefinedWhereExpression(
						"ticketNo",
						ExpressionOperator.LIKE,
						"indexSell%"));
		QuerySelectedPage selectedPage = dealService.findDealIds(definedQuery);
		assertEquals(1, selectedPage.getIds().size());
		List<BaseDealSnapshot> snapshots = dealService.findByIds(selectedPage);
		assertEquals(1, snapshots.size());
	}

	@Test
	public void updateMultipleDeals() {
		PhysicalDeal physicalDeal = DealFixture.createSamplePhysicalDeal(
				CommodityCode.CRUDE,
				"myDeal", 
				companyRole, 
				counterpartyRole,
				marketIndex);
		flush();
		clearCache();
		
		PhysicalDealSnapshot snapshot = new PhysicalDealSnapshot();
		snapshot.getDealDetail().setBuySell(BuySellCode.BUY);
		snapshot.setEntityId(physicalDeal.generateEntityId());
		snapshot.setEntityState(EntityState.MODIFIED);
		
		dealService.save(snapshot);
		flush();
		clearCache();
		
		physicalDeal = (PhysicalDeal) dealRepository.load(physicalDeal.generateEntityId());
		
		assertNotNull(physicalDeal.getCompanyRole());
		assertNotNull(physicalDeal.getCounterpartyRole());
		assertNotNull(physicalDeal.getMarketPriceIndex());
		
		assertEquals(BuySellCode.BUY, physicalDeal.getDealDetail().getBuySell());
		
		flush();
		clearCache();
		
		
	}

	@Test
	public void createMultipleDeals() {
		List<BaseDealSnapshot> snapshots = new ArrayList<>();


		Price paysPrice = new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ);

		FinancialSwapDealSnapshot swapDealSnapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"mine_1",
				companyRole,
				counterpartyRole,
				dealPriceIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				paysPrice);

		snapshots.add(swapDealSnapshot);

		PhysicalDealSnapshot dealSnapshot = DealFixture.createPhysicalDealSnapshot(
				CommodityCode.CRUDE,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"mine_2",
				companyRole, 
				counterpartyRole,
				marketIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.USD,
						UnitOfMeasureCode.GJ));
		snapshots.add(dealSnapshot);

		TransactionResult result  = dealService.save(snapshots);
		flush();
		clearCache();
		DefinedQuery definedQuery = new DefinedQuery("BaseDeal");
		definedQuery.getWhereClause().addExpression(
				new DefinedWhereExpression(
						"ticketNo",
						ExpressionOperator.LIKE,
						"mine_%"));

		QuerySelectedPage selectedPage = dealService.findDealIds(definedQuery);
		assertEquals(2, selectedPage.getIds().size());
		List<BaseDealSnapshot> snapshots2 = dealService.findByIds(selectedPage);
		assertEquals(2, snapshots2.size());
	}

}
