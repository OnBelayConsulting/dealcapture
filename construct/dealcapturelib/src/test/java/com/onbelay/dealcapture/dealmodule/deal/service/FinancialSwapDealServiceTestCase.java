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
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.shared.enums.*;

import java.math.BigDecimal;

public abstract class FinancialSwapDealServiceTestCase extends DealServiceTestCase {
	

	protected PriceIndex receivesIndex;
	protected PriceIndex paysIndex;

	protected FinancialSwapDeal fixed4FloatSellDeal;
	protected FinancialSwapDeal fixed4FloatBuyDeal;
	protected FinancialSwapDeal float4FloatBuyDeal;
	protected FinancialSwapDeal float4FloatPlusBuyDeal;
	protected FinancialSwapDeal fixed4PowerProfileBuyDeal;

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
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"f4floatsell",
				companyRole,
				counterpartyRole,
				receivesIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);

		fixed4FloatSellDeal = FinancialSwapDeal.create(snapshot);


		snapshot = FinancialSwapDealFixture.createFixedForFloatSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"f4floatbuy",
				companyRole,
				counterpartyRole,
				receivesIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);

		fixed4FloatBuyDeal = FinancialSwapDeal.create(snapshot);


		snapshot = FinancialSwapDealFixture.createFloat4FloatSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"float4floatbuy",
				companyRole,
				counterpartyRole,
				paysIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				receivesIndex);
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		float4FloatBuyDeal = FinancialSwapDeal.create(snapshot);


		snapshot = FinancialSwapDealFixture.createFloat4FloatPlusSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"float4floatPlusbuy",
				companyRole,
				counterpartyRole,
				paysIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				receivesIndex,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));
		float4FloatPlusBuyDeal = FinancialSwapDeal.create(snapshot);


		snapshot = FinancialSwapDealFixture.createWithPowerProfileFinancialSwapDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"float4powerprofbuy",
				companyRole,
				counterpartyRole,
				powerProfile,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(BigDecimal.ONE, CurrencyCode.CAD, UnitOfMeasureCode.GJ));


		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		fixed4PowerProfileBuyDeal = FinancialSwapDeal.create(snapshot);

		flush();
		clearCache();

		fixed4FloatSellDeal = (FinancialSwapDeal) dealRepository.load(fixed4FloatSellDeal.generateEntityId());
		float4FloatBuyDeal = (FinancialSwapDeal) dealRepository.load(float4FloatBuyDeal.generateEntityId());
		float4FloatPlusBuyDeal = (FinancialSwapDeal) dealRepository.load(float4FloatPlusBuyDeal.generateEntityId());
		fixed4PowerProfileBuyDeal = (FinancialSwapDeal) dealRepository.load(fixed4PowerProfileBuyDeal.generateEntityId());

	}


}
