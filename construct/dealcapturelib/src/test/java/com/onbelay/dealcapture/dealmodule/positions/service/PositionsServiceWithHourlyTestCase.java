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
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class PositionsServiceWithHourlyTestCase extends DealCaptureSpringTestCase {
	
	protected CompanyRole companyRole;
	protected CounterpartyRole counterpartyRole;

	protected PricingLocation pricingLocation;

	protected PriceIndex marketIndex;
	protected PriceIndex marketHourlyIndex;

	protected PriceIndex dealPriceIndex;
	protected PriceIndex dealPriceHourlyIndex;


	protected FxIndex fxIndex;

	protected InterestIndex interestIndex;


	protected PhysicalDeal fixedPriceSellDeal;
	protected PhysicalDeal fixedPriceHourlySellDeal;

	protected PhysicalDeal indexSellDeal;

	protected LocalDateTime createdDateTime = LocalDateTime.of(2024, 1, 1, 10, 1);

	protected LocalDate startDate = LocalDate.of(2024, 1, 1);
	protected LocalDate endDate = LocalDate.of(2024, 1, 2);


	@Autowired
	protected DealRepository dealRepository;
	
	@Autowired
	protected DealService dealService;
	@Autowired
	protected DealPositionService dealPositionService;

	@Autowired
	protected GeneratePositionsService generatePositionsService;

	@Autowired
	protected PriceRiskFactorService priceRiskFactorService;

	protected LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
	protected LocalDate toMarketDate = LocalDate.of(2024, 1, 31);

	@Override
	public void setUp() {
		super.setUp();
		companyRole = OrganizationRoleFixture.createCompanyRole(myOrganization);
		counterpartyRole = OrganizationRoleFixture.createCounterpartyRole(myOrganization);

		interestIndex = InterestIndexFixture.createInterestIndex("RATE", true, FrequencyCode.DAILY);
		flush();
		LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
		LocalDate toMarketDate = LocalDate.of(2024, 1, 31);
		LocalDateTime observedDateTime = LocalDateTime.of(2024, 1, 1, 1, 43);

		InterestIndexFixture.generateDailyInterestCurves(
				interestIndex,
				fromMarketDate,
				toMarketDate,
				BigDecimal.valueOf(0.12),
				observedDateTime);


		pricingLocation = PricingLocationFixture.createPricingLocation("west");

		marketIndex = PriceIndexFixture.createPriceIndex(
				"AECO",
				FrequencyCode.DAILY,
				CurrencyCode.USD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		marketHourlyIndex = PriceIndexFixture.createPriceIndex(
				"MYHOUR",
				FrequencyCode.HOURLY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);


		dealPriceIndex = PriceIndexFixture.createPriceIndex(
				"DDFF",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);


		dealPriceHourlyIndex = PriceIndexFixture.createPriceIndex(
				"DDFF_HH",
				FrequencyCode.HOURLY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);


		fxIndex = FxIndexFixture.createFxIndex(
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				CurrencyCode.USD);

		flush();

		PhysicalDealSnapshot snapshot = DealFixture.createIndexPhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.USD,
				"indexSellDeal",
				companyRole,
				counterpartyRole,
				marketHourlyIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				dealPriceHourlyIndex);
		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.USD);
		indexSellDeal = PhysicalDeal.create(snapshot);

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
				marketHourlyIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));

		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		fixedPriceSellDeal = PhysicalDeal.create(snapshot);


		snapshot = DealFixture.createPhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"fixedSellDeal_H",
				companyRole,
				counterpartyRole,
				marketHourlyIndex,
				BigDecimal.valueOf(2),
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.valueOf(4),
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));

		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.HOURLY);
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		fixedPriceHourlySellDeal = PhysicalDeal.create(snapshot);

		flush();
		clearCache();


	}


}
