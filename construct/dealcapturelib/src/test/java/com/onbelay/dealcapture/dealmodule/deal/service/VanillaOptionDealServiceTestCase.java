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
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.TradeTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.VanillaOptionDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.VanillaOptionDealFixture;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.VanillaOptionDealSnapshot;
import com.onbelay.dealcapture.pricing.model.InterestIndex;
import com.onbelay.dealcapture.pricing.model.InterestIndexFixture;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.pricing.model.PriceIndexFixture;
import com.onbelay.shared.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class VanillaOptionDealServiceTestCase extends DealServiceTestCase {

	protected VanillaOptionDeal buyPutOptionDeal;
	protected VanillaOptionDeal buyCallOptionDeal;

	protected VanillaOptionDeal buyCallOptionOutOfMoneyDeal;


	protected VanillaOptionDeal sellCallOptionDeal;


	protected PriceIndex monthlyOptionIndex;

	protected PriceIndex monthlySecondOptionIndex;


	protected InterestIndex interestIndex;


	protected LocalDate fromMarketDate = LocalDate.of(2024, 1, 1);
	protected LocalDate toMarketDate = LocalDate.of(2024, 1, 31);

	protected LocalDateTime observedDateTime = LocalDateTime.of(2024, 1, 1, 1, 43);


	@Override
	public void setUp() {
		super.setUp();

		interestIndex = InterestIndexFixture.createInterestIndex("RATE", true, FrequencyCode.DAILY);
		flush();

		InterestIndexFixture.generateDailyInterestCurves(
				interestIndex,
				fromMarketDate,
				toMarketDate,
				BigDecimal.valueOf(0.12),
				observedDateTime);

		monthlyOptionIndex = PriceIndexFixture.createPriceIndex(
				"MOPtIDX",
				FrequencyCode.MONTHLY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);


		monthlySecondOptionIndex = PriceIndexFixture.createPriceIndex(
				"MOPt2nd",
				FrequencyCode.MONTHLY,
				CurrencyCode.CAD,
				UnitOfMeasureCode.GJ,
				pricingLocation);



		VanillaOptionDealSnapshot snapshot = VanillaOptionDealFixture.createVanillaOptionDealSnapshot(
				CommodityCode.NATGAS,
				TradeTypeCode.OTC,
				OptionTypeCode.PUT,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"buyPutOptDeal",
				companyRole,
				counterpartyRole,
				monthlyOptionIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.TEN,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		buyPutOptionDeal = VanillaOptionDeal.create(snapshot);

		snapshot = VanillaOptionDealFixture.createVanillaOptionDealSnapshot(
				CommodityCode.NATGAS,
				TradeTypeCode.OTC,
				OptionTypeCode.CALL,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"buyCallOptDeal",
				companyRole,
				counterpartyRole,
				monthlyOptionIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.TEN,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		buyCallOptionDeal = VanillaOptionDeal.create(snapshot);


		snapshot = VanillaOptionDealFixture.createVanillaOptionDealSnapshot(
				CommodityCode.NATGAS,
				TradeTypeCode.OTC,
				OptionTypeCode.CALL,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"buyCallOptDealOut",
				companyRole,
				counterpartyRole,
				monthlySecondOptionIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.TEN,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		buyCallOptionOutOfMoneyDeal = VanillaOptionDeal.create(snapshot);


		snapshot = VanillaOptionDealFixture.createVanillaOptionDealSnapshot(
				CommodityCode.NATGAS,
				TradeTypeCode.OTC,
				OptionTypeCode.CALL,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"sellCallOptDeal",
				companyRole,
				counterpartyRole,
				monthlyOptionIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.TEN,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		sellCallOptionDeal = VanillaOptionDeal.create(snapshot);

		flush();
		clearCache();


	}


}
