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

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.*;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
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

public abstract class FinancialSwapDealServiceTestCase extends DealServiceTestCase {
	

	protected PriceIndex receivesIndex;
	protected PriceIndex paysIndex;

	protected FinancialSwapDeal fixed4FloatDeal;
	protected FinancialSwapDeal float4FloatDeal;
	protected FinancialSwapDeal float4FloatPlusDeal;

	protected PriceIndex settledHourlyIndex;
	protected PriceIndex onPeakDailyIndex;
	protected PriceIndex offPeakDailyIndex;

	protected PowerProfile powerProfile;




	@Override
	public void setUp() {
		super.setUp();


		settledHourlyIndex = PriceIndexFixture.createPriceIndex(
				"SETTLE",
				FrequencyCode.HOURLY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		onPeakDailyIndex = PriceIndexFixture.createPriceIndex(
				"ON PEAK",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		offPeakDailyIndex = PriceIndexFixture.createPriceIndex(
				"OFF PEAK",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);


		powerProfile = PowerProfileFixture.createPowerProfileAllDaysAllHours(
				"24By7",
				settledHourlyIndex,
				offPeakDailyIndex,
				onPeakDailyIndex);



		receivesIndex = PriceIndexFixture.createPriceIndex(
				"Receives",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		paysIndex = PriceIndexFixture.createPriceIndex(
				"Pays",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		flush();

		FinancialSwapDealSnapshot snapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				LocalDate.of(2019, 1, 1),
				LocalDate.of(2019, 1, 31),
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"f4float",
				companyRole,
				counterpartyRole,
				receivesIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);

		fixed4FloatDeal = FinancialSwapDeal.create(snapshot);

		snapshot = FinancialSwapDealFixture.createFloat4FloatSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"float4float",
				companyRole,
				counterpartyRole,
				receivesIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				paysIndex);
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		float4FloatDeal = FinancialSwapDeal.create(snapshot);


		snapshot = FinancialSwapDealFixture.createFloat4FloatPlusSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"float4floatPlus",
				companyRole,
				counterpartyRole,
				receivesIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				paysIndex,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));

		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		float4FloatPlusDeal = FinancialSwapDeal.create(snapshot);

		flush();
		clearCache();

		fixed4FloatDeal = (FinancialSwapDeal) dealRepository.load(fixed4FloatDeal.generateEntityId());
		float4FloatDeal = (FinancialSwapDeal) dealRepository.load(float4FloatDeal.generateEntityId());
		float4FloatPlusDeal = (FinancialSwapDeal) dealRepository.load(float4FloatPlusDeal.generateEntityId());

	}


}
