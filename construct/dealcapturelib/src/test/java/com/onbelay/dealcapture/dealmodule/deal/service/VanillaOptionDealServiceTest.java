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
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.*;
import com.onbelay.dealcapture.dealmodule.deal.model.*;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.VanillaOptionDealSnapshot;
import com.onbelay.shared.enums.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VanillaOptionDealServiceTest extends VanillaOptionDealServiceTestCase {


	@Override
	public void setUp() {
		super.setUp();

	}


	@Test
	public void findByContainsTicketNo() {
		DefinedQuery definedQuery = new DefinedQuery("BaseDeal");
		definedQuery.getWhereClause().addExpression(
				new DefinedWhereExpression(
						"ticketNo",
						ExpressionOperator.LIKE,
						"%Call%"));
		QuerySelectedPage selectedPage = dealService.findDealIds(definedQuery);
		assertEquals(3, selectedPage.getIds().size());
		List<BaseDealSnapshot> snapshots = dealService.findByIds(selectedPage);
		assertEquals(3, snapshots.size());
	}



	@Test
	public void findByOptionType() {
		DefinedQuery definedQuery = new DefinedQuery("VanillaOptionDeal");
		definedQuery.getWhereClause().addExpression(
				new DefinedWhereExpression(
						"optionType",
						ExpressionOperator.EQUALS,
						"Call"));
		QuerySelectedPage selectedPage = dealService.findDealIds(definedQuery);
		assertEquals(3, selectedPage.getIds().size());
		List<BaseDealSnapshot> snapshots = dealService.findByIds(selectedPage);
		assertEquals(3, snapshots.size());
	}


	@Test
	public void testUpdateVanillaOptionDeal() {
		flush();
		clearCache();
		
		VanillaOptionDealSnapshot snapshot = new VanillaOptionDealSnapshot();
		snapshot.getDealDetail().setBuySell(BuySellCode.BUY);
		snapshot.setEntityId(buyPutOptionDeal.generateEntityId());
		snapshot.setEntityState(EntityState.MODIFIED);
		
		dealService.save(snapshot);
		flush();
		clearCache();
		
		VanillaOptionDeal optionDeal = (VanillaOptionDeal) dealRepository.load(buyPutOptionDeal.generateEntityId());
		
		assertNotNull(optionDeal.getCompanyRole());
		assertNotNull(optionDeal.getCounterpartyRole());

		assertEquals(BuySellCode.BUY, optionDeal.getDealDetail().getBuySell());
		
		flush();
		clearCache();
		
		
	}

	@Test
	public void fetchDealSummariesForOption() {

		List<DealSummary> summaries = dealService.fetchDealSummariesByIds(List.of(buyPutOptionDeal.getId()));
		assertEquals(1, summaries.size());
		VanillaOptionDealSummary summary = (VanillaOptionDealSummary) summaries.get(0);

		assertNotNull(summary.getId());
		assertEquals(buyPutOptionDeal.getDealDetail().getBuySell(), summary.getDealDetail().getBuySell());
		assertEquals(buyPutOptionDeal.getDealDetail().getTicketNo(), summary.getDealDetail().getTicketNo());
		assertEquals(buyPutOptionDeal.getDealDetail().getStartDate(), summary.getDealDetail().getStartDate());
		assertEquals(buyPutOptionDeal.getDealDetail().getEndDate(), summary.getDealDetail().getEndDate());
		assertEquals(0, buyPutOptionDeal.getDealDetail().getVolumeQuantity().compareTo(summary.getDealDetail().getVolumeQuantity()));

		assertEquals(DealTypeCode.VANILLA_OPTION, summary.getDealTypeCode());
		assertEquals(CurrencyCode.CAD, summary.getDealDetail().getSettlementCurrencyCode());
		assertEquals(CurrencyCode.CAD, summary.getDealDetail().getReportingCurrencyCode());
		assertEquals(UnitOfMeasureCode.GJ, summary.getDealDetail().getVolumeUnitOfMeasureCode());

		assertEquals(TradeTypeCode.OTC, summary.getDetail().getTradeTypeCode());
		assertEquals(OptionTypeCode.PUT, summary.getDetail().getOptionTypeCode());
		assertEquals(OptionStyleCode.AMERICAN, summary.getDetail().getOptionStyleCode());
		assertEquals(0, BigDecimal.TEN.compareTo(summary.getDetail().getStrikePriceValue()));
		assertEquals(CurrencyCode.CAD, summary.getDetail().getStrikePriceCurrencyCode());
		assertEquals(UnitOfMeasureCode.GJ, summary.getDetail().getStrikePriceUnitOfMeasureCode());


		assertNotNull(summary.getUnderlyingPriceIndexId());

	}

	@Test
	public void testCreateSellCallVanillaOptionDeal() {

		VanillaOptionDealSnapshot dealSnapshot = VanillaOptionDealFixture.createVanillaOptionDealSnapshot(
				CommodityCode.CRUDE,
				TradeTypeCode.OTC,
				OptionTypeCode.CALL,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"optSellCall",
				companyRole, 
				counterpartyRole,
				marketIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.TEN,
						CurrencyCode.USD,
						UnitOfMeasureCode.GJ));
		
		TransactionResult result  = dealService.save(dealSnapshot);
		flush();
		clearCache();
		VanillaOptionDeal deal = (VanillaOptionDeal) dealRepository.load(result.getEntityId());
		assertEquals(companyRole.getId(), deal.getCompanyRole().getId());
		assertEquals(counterpartyRole.getId(), deal.getCounterpartyRole().getId());
		assertEquals(startDate, deal.getDealDetail().getStartDate());
		assertEquals(endDate, deal.getDealDetail().getEndDate());
		assertEquals(BuySellCode.SELL, deal.getDealDetail().getBuySell());
		assertEquals(CurrencyCode.CAD, deal.getDealDetail().getReportingCurrencyCode());
		assertEquals(DealStatusCode.VERIFIED, deal.getDealDetail().getDealStatus());

		assertEquals(TradeTypeCode.OTC, deal.getDetail().getTradeTypeCode());
		assertEquals(OptionTypeCode.CALL, deal.getDetail().getOptionTypeCode());
		assertEquals(OptionStyleCode.AMERICAN, deal.getDetail().getOptionStyleCode());
		assertEquals(0, BigDecimal.TEN.compareTo(deal.getDetail().getStrikePriceValue()));
		assertEquals(CurrencyCode.USD, deal.getDetail().getStrikePriceCurrencyCode());
		assertEquals(UnitOfMeasureCode.GJ, deal.getDetail().getStrikePriceUnitOfMeasureCode());
		assertEquals(marketIndex.getId(), deal.getUnderlyingPriceIndex().getId());
	}


	@Test
	public void testCreateBuyPutVanillaOptionDeal() {

		VanillaOptionDealSnapshot dealSnapshot = VanillaOptionDealFixture.createVanillaOptionDealSnapshot(
				CommodityCode.CRUDE,
				TradeTypeCode.OTC,
				OptionTypeCode.PUT,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"optBuyCall",
				companyRole,
				counterpartyRole,
				marketIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));

		TransactionResult result  = dealService.save(dealSnapshot);
		flush();
		clearCache();
		VanillaOptionDeal deal = (VanillaOptionDeal) dealRepository.load(result.getEntityId());
		assertEquals(companyRole.getId(), deal.getCompanyRole().getId());
		assertEquals(counterpartyRole.getId(), deal.getCounterpartyRole().getId());
		assertEquals(startDate, deal.getDealDetail().getStartDate());
		assertEquals(endDate, deal.getDealDetail().getEndDate());
		assertEquals(BuySellCode.BUY, deal.getDealDetail().getBuySell());
		assertEquals(CurrencyCode.CAD, deal.getDealDetail().getReportingCurrencyCode());
		assertEquals(DealStatusCode.VERIFIED, deal.getDealDetail().getDealStatus());

		assertEquals(TradeTypeCode.OTC, deal.getDetail().getTradeTypeCode());
		assertEquals(OptionTypeCode.PUT, deal.getDetail().getOptionTypeCode());
		assertEquals(OptionStyleCode.AMERICAN, deal.getDetail().getOptionStyleCode());
		assertEquals(0, BigDecimal.ONE.compareTo(deal.getDetail().getStrikePriceValue()));
		assertEquals(CurrencyCode.CAD, deal.getDetail().getStrikePriceCurrencyCode());
		assertEquals(UnitOfMeasureCode.GJ, deal.getDetail().getStrikePriceUnitOfMeasureCode());
		assertEquals(marketIndex.getId(), deal.getUnderlyingPriceIndex().getId());
	}


}
