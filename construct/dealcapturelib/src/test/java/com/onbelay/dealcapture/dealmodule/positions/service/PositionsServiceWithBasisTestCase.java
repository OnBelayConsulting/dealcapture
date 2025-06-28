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
package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.organization.model.OrganizationRoleFixture;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class PositionsServiceWithBasisTestCase extends DealCaptureSpringTestCase {
	
	protected CompanyRole companyRole;
	protected CounterpartyRole counterpartyRole;
	protected PriceIndex marketIndex;

	protected PriceIndex dealPriceIndex;

	protected PriceIndex basisPriceIndex;
	protected PriceIndex basisToBasisPriceIndex;


	protected FxIndex fxIndex;

	protected PricingLocation pricingLocation;

	protected PhysicalDeal fixedPriceSellDeal;
	protected PhysicalDeal indexSellDeal;
	protected PhysicalDeal indexBuyDeal;

	protected LocalDateTime createdDateTime = LocalDateTime.of(2024, 1, 1, 10, 1);

	protected LocalDate startDate = LocalDate.of(2024, 1, 1);
	protected LocalDate endDate = LocalDate.of(2024, 1, 31);


	@Autowired
	protected DealRepository dealRepository;
	
	@Autowired
	protected DealService dealService;
	@Autowired
	protected DealPositionService dealPositionService;

	@Autowired
	protected GeneratePositionsService generatePositionsService;


	protected LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
	protected LocalDate toMarketDate = LocalDate.of(2024, 1, 31);

	@Override
	public void setUp() {
		super.setUp();
		companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
		counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);

		pricingLocation = PricingLocationFixture.createPricingLocation("west");

		marketIndex = PriceIndexFixture.createPriceIndex(
				"AECO",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);


		basisPriceIndex = PriceIndexFixture.createBasisPriceIndex(
				marketIndex,
				"B_ADFS",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		basisToBasisPriceIndex = PriceIndexFixture.createBasisPriceIndex(
				basisPriceIndex,
				"B_VVDD",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);



		dealPriceIndex = PriceIndexFixture.createPriceIndex(
				"DDFF",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				PricingLocationFixture.createPricingLocation(
						"east"));

		fxIndex = FxIndexFixture.createFxIndex(
				FrequencyCode.MONTHLY,
				CurrencyCode.CAD,
				CurrencyCode.USD);

		flush();

		PhysicalDealSnapshot snapshot = DealFixture.createIndexPhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"indexSellDeal",
				companyRole,
				counterpartyRole,
				marketIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				basisToBasisPriceIndex);
		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		indexSellDeal = PhysicalDeal.create(snapshot);

		snapshot = DealFixture.createIndexPhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"indexBuyDeal",
				companyRole,
				counterpartyRole,
				marketIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				basisPriceIndex);
		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		indexBuyDeal = PhysicalDeal.create(snapshot);


		snapshot = DealFixture.createPhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"fixedSellDeal",
				companyRole,
				counterpartyRole,
				basisToBasisPriceIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));

		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		fixedPriceSellDeal = PhysicalDeal.create(snapshot);

		flush();
		clearCache();


	}


}
