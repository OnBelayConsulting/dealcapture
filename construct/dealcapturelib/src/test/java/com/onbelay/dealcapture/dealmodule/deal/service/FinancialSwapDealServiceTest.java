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
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDealFixture;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSummary;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FinancialSwapDealServiceTest extends FinancialSwapDealServiceTestCase {


	@Test
	public void fetchSwapDealSummaries() {

		List<FinancialSwapDealSummary> summaries = dealService.findFinancialSwapDealSummariesByIds(List.of(fixed4FloatDeal.getId()));
		assertEquals(1, summaries.size());
		FinancialSwapDealSummary summary = summaries.get(0);
		assertNull(summary.getPaysIndexId());
	}

	@Test
	public void createFixed4FloatFinancialSwapDeal() {

		Price paysPrice = new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ);

		FinancialSwapDealSnapshot swapDealSnapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"ghkk",
				companyRole,
				counterpartyRole,
				receivesIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				paysPrice);

		TransactionResult result  = dealService.save(swapDealSnapshot);
		flush();
		clearCache();
		FinancialSwapDeal swapDeal = (FinancialSwapDeal) dealRepository.load(result.getEntityId());
		assertEquals(companyRole.getId(), swapDeal.getCompanyRole().getId());
		assertEquals(counterpartyRole.getId(), swapDeal.getCounterpartyRole().getId());
		assertEquals(startDate, swapDeal.getDealDetail().getStartDate());
		assertEquals(endDate, swapDeal.getDealDetail().getEndDate());
		assertEquals(BuySellCode.BUY, swapDeal.getDealDetail().getBuySell());
		assertEquals(CurrencyCode.CAD, swapDeal.getDealDetail().getReportingCurrencyCode());
		assertEquals(DealStatusCode.VERIFIED, swapDeal.getDealDetail().getDealStatus());
		assertEquals(DealStatusCode.VERIFIED, swapDeal.getDealDetail().getDealStatus());
		assertEquals(ValuationCode.INDEX, swapDeal.getDetail().getRecievesValuationCode());
		assertEquals(receivesIndex.getId(), swapDeal.getReceivesPriceIndex().getId());
		assertEquals(ValuationCode.FIXED, swapDeal.getDetail().getPaysValuationCode());
		assertEquals(paysPrice, swapDeal.getDetail().getFixedPrice());
	}


	@Test
	public void createFloatPlus4FloatFinancialSwapDeal() {

		Price paysPrice = new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ);

		FinancialSwapDealSnapshot swapDealSnapshot = FinancialSwapDealFixture.createFloat4FloatPlusSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"ghkk",
				companyRole,
				counterpartyRole,
				paysIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				receivesIndex,
				paysPrice);

		TransactionResult result  = dealService.save(swapDealSnapshot);
		flush();
		clearCache();
		FinancialSwapDeal swapDeal = (FinancialSwapDeal) dealRepository.load(result.getEntityId());
		assertEquals(companyRole.getId(), swapDeal.getCompanyRole().getId());
		assertEquals(counterpartyRole.getId(), swapDeal.getCounterpartyRole().getId());
		assertEquals(startDate, swapDeal.getDealDetail().getStartDate());
		assertEquals(endDate, swapDeal.getDealDetail().getEndDate());
		assertEquals(BuySellCode.BUY, swapDeal.getDealDetail().getBuySell());
		assertEquals(CurrencyCode.CAD, swapDeal.getDealDetail().getReportingCurrencyCode());
		assertEquals(DealStatusCode.VERIFIED, swapDeal.getDealDetail().getDealStatus());
		assertEquals(DealStatusCode.VERIFIED, swapDeal.getDealDetail().getDealStatus());
		assertEquals(ValuationCode.INDEX, swapDeal.getDetail().getRecievesValuationCode());
		assertEquals(receivesIndex.getId(), swapDeal.getReceivesPriceIndex().getId());
		assertEquals(ValuationCode.INDEX_PLUS, swapDeal.getDetail().getPaysValuationCode());
		assertEquals(paysPrice, swapDeal.getDetail().getFixedPrice());
	}


	@Test
	public void createFixed4PowerProfileFinancialSwapDeal() {

		Price paysPrice = new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ);

		FinancialSwapDealSnapshot swapDealSnapshot = FinancialSwapDealFixture.createWithPowerProfileFinancialSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"ghkk",
				companyRole,
				counterpartyRole,
				powerProfile,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				paysPrice);

		TransactionResult result  = dealService.save(swapDealSnapshot);
		flush();
		clearCache();
		FinancialSwapDeal swapDeal = (FinancialSwapDeal) dealRepository.load(result.getEntityId());
		assertEquals(companyRole.getId(), swapDeal.getCompanyRole().getId());
		assertEquals(counterpartyRole.getId(), swapDeal.getCounterpartyRole().getId());
		assertEquals(startDate, swapDeal.getDealDetail().getStartDate());
		assertEquals(endDate, swapDeal.getDealDetail().getEndDate());
		assertEquals(BuySellCode.BUY, swapDeal.getDealDetail().getBuySell());
		assertEquals(CurrencyCode.CAD, swapDeal.getDealDetail().getReportingCurrencyCode());
		assertEquals(DealStatusCode.VERIFIED, swapDeal.getDealDetail().getDealStatus());
		assertEquals(DealStatusCode.VERIFIED, swapDeal.getDealDetail().getDealStatus());
		assertEquals(ValuationCode.FIXED, swapDeal.getDetail().getPaysValuationCode());
		assertEquals(paysPrice, swapDeal.getDetail().getFixedPrice());
		assertEquals(ValuationCode.POWER_PROFILE, swapDeal.getDetail().getRecievesValuationCode());
		assertEquals(powerProfile.getId(), swapDeal.getPowerProfile().getId());

	}



	@Test
	public void createFloat4FloatFinancialSwapDeal() {

		FinancialSwapDealSnapshot swapDealSnapshot = FinancialSwapDealFixture.createFloat4FloatSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"ghkk",
				companyRole,
				counterpartyRole,
				paysIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				receivesIndex);

		TransactionResult result  = dealService.save(swapDealSnapshot);
		flush();
		clearCache();
		FinancialSwapDeal swapDeal = (FinancialSwapDeal) dealRepository.load(result.getEntityId());
		assertEquals(companyRole.getId(), swapDeal.getCompanyRole().getId());
		assertEquals(counterpartyRole.getId(), swapDeal.getCounterpartyRole().getId());
		assertEquals(startDate, swapDeal.getDealDetail().getStartDate());
		assertEquals(endDate, swapDeal.getDealDetail().getEndDate());
		assertEquals(BuySellCode.SELL, swapDeal.getDealDetail().getBuySell());
		assertEquals(CurrencyCode.CAD, swapDeal.getDealDetail().getReportingCurrencyCode());
		assertEquals(DealStatusCode.VERIFIED, swapDeal.getDealDetail().getDealStatus());
		assertEquals(ValuationCode.INDEX, swapDeal.getDetail().getRecievesValuationCode());
		assertEquals(receivesIndex.getId(), swapDeal.getReceivesPriceIndex().getId());
		assertEquals(ValuationCode.INDEX, swapDeal.getDetail().getRecievesValuationCode());
		assertEquals(paysIndex.getId(), swapDeal.getPaysPriceIndex().getId());
	}


	@Test
	public void updateFinancialSwapDeal() {

		FinancialSwapDealSnapshot snapshot = new FinancialSwapDealSnapshot();
		snapshot.getDealDetail().setBuySell(BuySellCode.BUY);
		snapshot.setEntityId(fixed4FloatDeal.generateEntityId());
		snapshot.setEntityState(EntityState.MODIFIED);

		dealService.save(snapshot);
		flush();
		clearCache();

		FinancialSwapDealSnapshot swapDeal = (FinancialSwapDealSnapshot) dealService.load(fixed4FloatDeal.generateEntityId());

		assertNull(swapDeal.getPaysPriceIndexId());
		assertNotNull(swapDeal.getReceivesPriceIndexId());

		assertEquals(BuySellCode.BUY, swapDeal.getDealDetail().getBuySell());
	}

}
