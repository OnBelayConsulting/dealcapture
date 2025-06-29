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

import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfileFixture;
import com.onbelay.dealcapture.pricing.model.*;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.test.DealCaptureSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class PowerProfilePositionsWithFxTestCase extends DealCaptureSpringTestCase {

	protected PricingLocation pricingLocation;

	protected PriceIndex settledHourlyIndex;
	protected PriceIndex onPeakDailyIndex;
	protected PriceIndex offPeakDailyIndex;

	protected PowerProfile powerProfile;
	protected PowerProfile mixedPowerProfile;
	protected PowerProfile powerProfileWithBasis;


	protected PriceIndex hubIndex;
	protected PriceIndex basisOnPeakPriceIndex;
	protected PriceIndex basisOffPeakPriceIndex;
	protected PriceIndex forwardHourlyIndex;


	protected FxIndex fxIndex;

	protected InterestIndex interestIndex;

	protected LocalDateTime createdDateTime = LocalDateTime.of(2024, 1, 1, 10, 1);

	protected LocalDate startDate = LocalDate.of(2024, 1, 1);
	protected LocalDate endDate = LocalDate.of(2024, 1, 31);



	@Autowired
	protected PriceRiskFactorService priceRiskFactorService;

	protected LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
	protected LocalDate toMarketDate = LocalDate.of(2024, 3, 31);

	@Override
	public void setUp() {
		super.setUp();

		pricingLocation = PricingLocationFixture.createPricingLocation("west");


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


		hubIndex = PriceIndexFixture.createPriceIndex(
				"HubIndex",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);


		settledHourlyIndex = PriceIndexFixture.createPriceIndex(
				"SETTLE",
				FrequencyCode.HOURLY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);

		forwardHourlyIndex = PriceIndexFixture.createBasisPriceIndex(
				hubIndex,
				"fwd_hourly",
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


		basisOnPeakPriceIndex = PriceIndexFixture.createBasisPriceIndex(
				hubIndex,
				"B_ONP",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);


		basisOffPeakPriceIndex = PriceIndexFixture.createBasisPriceIndex(
				hubIndex,
				"B_OffP",
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);



		powerProfile = PowerProfileFixture.createPowerProfileAllDaysAllHours(
				"24By7",
				settledHourlyIndex,
				offPeakDailyIndex,
				onPeakDailyIndex);

		mixedPowerProfile = PowerProfileFixture.createPowerProfileWeekDaysSomeHours(
				"5By12",
				settledHourlyIndex,
				offPeakDailyIndex,
				onPeakDailyIndex);


		powerProfileWithBasis = PowerProfileFixture.createPowerProfileWeekDaysSomeHours(
				"5By12Fwd",
				settledHourlyIndex,
				forwardHourlyIndex,
				offPeakDailyIndex,
				onPeakDailyIndex);


		fxIndex = FxIndexFixture.createFxIndex(
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				CurrencyCode.USD);

		flush();


	}


}
