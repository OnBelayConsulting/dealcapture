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
package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDetail;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.dealcapture.pricing.model.PricingLocation;
import com.onbelay.dealcapture.pricing.model.PricingLocationFixture;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DealTest extends DealCaptureSpringTestCase {
	
	@Autowired
	private DealRepositoryBean dealRepository;
	
	private CompanyRole companyRole;
	private CounterpartyRole counterpartyRole;

	private PricingLocation location;
	private PriceIndex firstIndex;
	private PriceIndex secondIndex;

	@Override
	public void setUp() {
		super.setUp();
		companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
		counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);

		location = PricingLocationFixture.createPricingLocation("west");
		firstIndex = PriceIndexFixture.createPriceIndex(
				"first",
				FrequencyCode.MONTHLY,
				CurrencyCode.USD,
				UnitOfMeasureCode.GJ,
				location);
		secondIndex = PriceIndexFixture.createPriceIndex(
				"second",
				FrequencyCode.MONTHLY,
				CurrencyCode.USD,
				UnitOfMeasureCode.GJ,
				location);

		flush();
	}

	@Test
	public void testCreateDeal() {

		PhysicalDealSnapshot snapshot = new PhysicalDealSnapshot();
		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.setCompanyRoleId(companyRole.generateEntityId());
		snapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());
		snapshot.getDealDetail().setCommodityCode(CommodityCode.CRUDE);
		setDealAttributes(
				snapshot.getDealDetail(),
				DealStatusCode.PENDING,
				BuySellCode.BUY,
				"deal-12n",
				LocalDate.of(2019, 1, 1),
				LocalDate.of(2019, 1, 31),
				BigDecimal.valueOf(34.78),
				UnitOfMeasureCode.GJ,
				CurrencyCode.USD);

		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.USD);
		snapshot.getDealDetail().setReportingCurrencyCode(CurrencyCode.CAD);
		snapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);

		snapshot.getDealDetail().setFixedPriceValue(BigDecimal.valueOf(1.55));
		snapshot.getDealDetail().setFixedPriceCurrencyCode(CurrencyCode.CAD);
		snapshot.getDealDetail().setFixedPriceUnitOfMeasureCode(UnitOfMeasureCode.MMBTU);

		PhysicalDeal deal = PhysicalDeal.create(snapshot);

		flush();
		clearCache();
		
		List<BaseDeal> deals = dealRepository.fetchAllDeals();
		assertEquals(1, deals.size());
		
		PhysicalDeal created = (PhysicalDeal) deals.get(0);
		
		assertTrue(created.getId().longValue() > 0);

		assertEquals(DealStatusCode.PENDING, created.getDealDetail().getDealStatus());

		assertEquals(companyRole.getId(), created.getCompanyRole().getId());
		assertEquals(counterpartyRole.getId(), created.getCounterpartyRole().getId());
		assertEquals(LocalDate.of(2019, 1, 1), created.getDealDetail().getStartDate());
		assertEquals(LocalDate.of(2019, 1, 31), created.getDealDetail().getEndDate());

		assertEquals(0, BigDecimal.valueOf(34.78).compareTo(created.getDealDetail().getVolumeQuantity()));

		assertEquals(CurrencyCode.USD, created.getDealDetail().getSettlementCurrencyCode());
		assertEquals(CurrencyCode.CAD, created.getDealDetail().getReportingCurrencyCode());

		assertEquals(UnitOfMeasureCode.GJ, created.getDealDetail().getVolumeUnitOfMeasureCode());

		assertEquals(0, BigDecimal.valueOf(1.55).compareTo(created.getDealDetail().getFixedPriceValue()));
		assertEquals(UnitOfMeasureCode.MMBTU, created.getDealDetail().getFixedPriceUnitOfMeasureCode());
		assertEquals(CurrencyCode.CAD, created.getDealDetail().getFixedPriceCurrencyCode());
	}

	@Test
	public void createFixed4Float() {
		FinancialSwapDeal swapDeal = FinancialSwapDealFixture.createFixed4FloatSwapDeal(
				myBusinessContact,
				CommodityCode.NATGAS,
				"ddd",
				companyRole,
				counterpartyRole,
				firstIndex,
				LocalDate.of(2019, 1, 1),
				LocalDate.of(2019, 1, 31),
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				CurrencyCode.CAD,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));

		flush();
		swapDeal = (FinancialSwapDeal) dealRepository.load(swapDeal.generateEntityId());
		assertEquals(CommodityCode.NATGAS, swapDeal.getDealDetail().getCommodityCode());
		assertEquals(companyRole.getId(), swapDeal.getCompanyRole().getId());
		assertEquals(counterpartyRole.getId(), swapDeal.getCounterpartyRole().getId());
		assertEquals(firstIndex.getId(), swapDeal.getReceivesPriceIndex().getId());
		assertEquals(new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ), swapDeal.getDealDetail().getFixedPrice());
	}

	private void setDealAttributes(
			DealDetail detail,
			DealStatusCode status,
			BuySellCode buySellCode,
			String ticketNo,
			LocalDate startDate,
			LocalDate endDate,
			BigDecimal quantity,
			UnitOfMeasureCode unitOfMeasureCode,
			CurrencyCode currencyCode) {

		detail.setDealStatus(status);
		detail.setBuySell(buySellCode);
		detail.setTicketNo(ticketNo);
		detail.setStartDate(startDate);
		detail.setEndDate(endDate);
		detail.setVolumeQuantity(quantity);
		detail.setReportingCurrencyCode(currencyCode);
		detail.setVolumeUnitOfMeasureCode(unitOfMeasureCode);

	}

}
