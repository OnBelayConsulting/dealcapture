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
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary;
import com.onbelay.shared.enums.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DealServiceTest extends DealServiceTestCase {


	@Override
	public void setUp() {
		super.setUp();

	}

	@Test
	public void testUpdatePhysicalDeal() {
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
	public void fetchDealSummariesFixedPriceDeal() {

		List<PhysicalDealSummary> summaries = dealService.findPhysicalDealSummariesByIds(List.of(fixedPriceSellDeal.getId()));
		assertEquals(1, summaries.size());
		PhysicalDealSummary summary = summaries.get(0);

		assertNotNull(summary.getDealId());
		assertEquals(fixedPriceSellDeal.getDealDetail().getBuySell(), summary.getBuySellCode());
		assertEquals(fixedPriceSellDeal.getDealDetail().getTicketNo(), summary.getTicketNo());
		assertEquals(fixedPriceSellDeal.getDealDetail().getStartDate(), summary.getStartDate());
		assertEquals(fixedPriceSellDeal.getDealDetail().getEndDate(), summary.getEndDate());
		assertEquals(0, fixedPriceSellDeal.getDealDetail().getVolumeQuantity().compareTo(summary.getVolumeQuantity()));

		assertEquals(DealTypeCode.PHYSICAL_DEAL, summary.getDealTypeCode());
		assertEquals(CurrencyCode.CAD, summary.getSettlementCurrencyCode());
		assertEquals(CurrencyCode.CAD, summary.getReportingCurrencyCode());
		assertEquals(UnitOfMeasureCode.GJ, summary.getVolumeUnitOfMeasureCode());

		assertEquals(ValuationCode.FIXED, summary.getDealPriceValuationCode());
		assertEquals(0, BigDecimal.ONE.compareTo(summary.getFixedPriceValue()));
		assertEquals(UnitOfMeasureCode.GJ, summary.getFixedPriceUnitOfMeasureCode());
		assertEquals(CurrencyCode.CAD, summary.getFixedPriceCurrencyCode());
		assertNull(summary.getDealPriceIndexId());

		assertEquals(ValuationCode.INDEX, summary.getMarketValuationCode());
		assertEquals(marketIndex.getId(), summary.getMarketIndexId());
	}

	@Test
	public void testCreateFixedPriceMarketIndexPhysicalDeal() {

		PhysicalDealSnapshot dealSnapshot = DealFixture.createPhysicalDealSnapshot(
				CommodityCode.CRUDE,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"mydeal",
				companyRole, 
				counterpartyRole,
				marketIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.USD,
						UnitOfMeasureCode.GJ));
		
		TransactionResult result  = dealService.save(dealSnapshot);
		flush();
		clearCache();
		PhysicalDeal physicalDeal = (PhysicalDeal) dealRepository.load(result.getEntityId());
		assertEquals(companyRole.getId(), physicalDeal.getCompanyRole().getId());
		assertEquals(counterpartyRole.getId(), physicalDeal.getCounterpartyRole().getId());
		assertEquals(startDate, physicalDeal.getDealDetail().getStartDate());
		assertEquals(endDate, physicalDeal.getDealDetail().getEndDate());
		assertEquals(BuySellCode.SELL, physicalDeal.getDealDetail().getBuySell());
		assertEquals(CurrencyCode.CAD, physicalDeal.getDealDetail().getReportingCurrencyCode());
		assertEquals(DealStatusCode.VERIFIED, physicalDeal.getDealDetail().getDealStatus());
		assertEquals(marketIndex.getId(), physicalDeal.getMarketPriceIndex().getId());
	}

	@Test
	public void testCreateIndexPriceMarketIndexPhysicalDeal() {

		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setDealStatus(DealStatusCode.VERIFIED);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(CurrencyCode.USD);
		dealSnapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		dealSnapshot.getDealDetail().setBuySell(BuySellCode.SELL);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo("GHT");

		dealSnapshot.setMarketPriceIndexId(marketIndex.generateEntityId());
		dealSnapshot.getDealDetail().setCommodityCode(CommodityCode.CRUDE);
		dealSnapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);

		dealSnapshot.getDealDetail().setVolume(
				new Quantity(
						BigDecimal.valueOf(34.78),
						UnitOfMeasureCode.GJ));

		dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX);
		dealSnapshot.setDealPriceIndexId(dealPriceIndex.generateEntityId());

		dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);

		TransactionResult result  = dealService.save(dealSnapshot);
		flush();
		clearCache();
		PhysicalDeal physicalDeal = (PhysicalDeal) dealRepository.load(result.getEntityId());
		assertEquals(dealPriceIndex.getId(), physicalDeal.getDealPriceIndex().getId());
	}

	@Test
	public void testCreateIndexPhysicalDealWithFixedPriceFail() {

		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());
		dealSnapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		dealSnapshot.getDealDetail().setCommodityCode(CommodityCode.CRUDE);
		dealSnapshot.getDealDetail().setDealStatus(DealStatusCode.VERIFIED);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(CurrencyCode.USD);
		dealSnapshot.getDealDetail().setBuySell(BuySellCode.SELL);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo("GHT");

		dealSnapshot.setMarketPriceIndexId(marketIndex.generateEntityId());

		dealSnapshot.getDealDetail().setVolume(
				new Quantity(
						BigDecimal.valueOf(34.78),
						UnitOfMeasureCode.GJ));
		dealSnapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);
		dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX);
		dealSnapshot.setDealPriceIndexId(dealPriceIndex.generateEntityId());
		dealSnapshot.getDetail().setFixedPrice(
				new Price(
					BigDecimal.ONE,
					CurrencyCode.USD,
					UnitOfMeasureCode.GJ));

		dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);

		try {
			dealService.save(dealSnapshot);
			fail("Should have thrown exception");
		} catch (OBValidationException e) {
			assertEquals(DealErrorCode.INVALID_FIXED_PRICE_VALUE.getCode(), e.getErrorCode());
			return;
		}
		fail("Should have thrown ob exception");
	}

}
