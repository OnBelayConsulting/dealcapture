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
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DealServiceTest extends DealCaptureSpringTestCase {
	
	private CompanyRole companyRole;
	private CounterpartyRole counterpartyRole;
	private PriceIndex priceIndex;

	private PriceIndex dealPriceIndex;

	private LocalDate startDate = LocalDate.of(2023, 1, 1);
	private LocalDate endDate = LocalDate.of(2023, 12, 31);

	@Autowired
	private DealRepository dealRepository;
	
	@Autowired
	private DealService dealService;

	@Override
	public void setUp() {
		super.setUp();
		companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
		counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);
		
		priceIndex = PriceIndexFixture.createPriceIndex(
				"AECO", 
				PricingLocationFixture.createPricingLocation(
						"west"));
		dealPriceIndex = PriceIndexFixture.createPriceIndex(
				"DDFF",
				PricingLocationFixture.createPricingLocation(
						"east"));

		flush();
	}

	@Test
	public void testUpdatePhysicalDeal() {
		PhysicalDeal physicalDeal = DealFixture.createFixedPricePhysicalDeal(
				CommodityCode.CRUDE,
				"myDeal", 
				companyRole, 
				counterpartyRole,
                priceIndex);
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
	public void testCreateFixedPriceMarketIndexPhysicalDeal() {

		PhysicalDealSnapshot dealSnapshot = DealFixture.createFixedPriceMarketIndexPhysicalDealSnapshot(
				CommodityCode.CRUDE,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"mydeal",
				companyRole, 
				counterpartyRole,
                priceIndex,
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
		assertEquals(priceIndex.getId(), physicalDeal.getMarketPriceIndex().getId());
	}

	@Test
	public void testCreateIndexPriceMarketIndexPhysicalDeal() {

		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setDealStatus(DealStatusCode.VERIFIED);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(CurrencyCode.USD);
		dealSnapshot.getDealDetail().setBuySell(BuySellCode.SELL);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo("GHT");

		dealSnapshot.setMarketPriceIndexId(priceIndex.generateEntityId());
		dealSnapshot.getDealDetail().setCommodityCode(CommodityCode.CRUDE);

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
	}

	@Test
	public void testCreateIndexPlusPriceMarketIndexPhysicalDeal() {

		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());
		dealSnapshot.getDealDetail().setCommodityCode(CommodityCode.CRUDE);
		dealSnapshot.getDealDetail().setDealStatus(DealStatusCode.VERIFIED);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(CurrencyCode.USD);
		dealSnapshot.getDealDetail().setBuySell(BuySellCode.SELL);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo("GHT");

		dealSnapshot.setMarketPriceIndexId(priceIndex.generateEntityId());

		dealSnapshot.getDealDetail().setVolume(
				new Quantity(
						BigDecimal.valueOf(34.78),
						UnitOfMeasureCode.GJ));

		dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX_PLUS);
		dealSnapshot.setDealPriceIndexId(dealPriceIndex.generateEntityId());
		dealSnapshot.getDetail().setDealPrice(
				new Price(
					BigDecimal.ONE,
					CurrencyCode.USD,
					UnitOfMeasureCode.GJ));

		dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);

		TransactionResult result  = dealService.save(dealSnapshot);
		flush();
		clearCache();
	}

}
