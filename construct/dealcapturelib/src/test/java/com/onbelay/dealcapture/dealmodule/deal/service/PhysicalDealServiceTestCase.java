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
import com.onbelay.dealcapture.dealmodule.deal.model.DealFixture;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;

public abstract class PhysicalDealServiceTestCase extends DealServiceTestCase {

	protected PhysicalDeal fixedPriceSellDeal;
	protected PhysicalDeal indexSellDeal;
	protected PhysicalDeal indexPlusSellDeal;

	protected PhysicalDeal fixedPriceBuyDeal;
	protected PhysicalDeal indexBuyDeal;
	protected PhysicalDeal indexPlusBuyDeal;

	@Override
	public void setUp() {
		super.setUp();

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
				dealPriceIndex);
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
				dealPriceIndex);
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
				marketIndex,
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
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"fixedBuyDeal",
				companyRole,
				counterpartyRole,
				marketIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ));

		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		fixedPriceBuyDeal = PhysicalDeal.create(snapshot);


		snapshot = DealFixture.createIndexPlusPhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"indexPlusSellDeal",
				companyRole,
				counterpartyRole,
				marketIndex,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ),
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				dealPriceIndex);

		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		indexPlusSellDeal = PhysicalDeal.create(snapshot);

		snapshot = DealFixture.createIndexPlusPhysicalDealSnapshot(
				CommodityCode.NATGAS,
				BuySellCode.BUY,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				CurrencyCode.CAD,
				"indexPlusBuyDeal",
				companyRole,
				counterpartyRole,
				marketIndex,
				new Price(
						BigDecimal.ONE,
						CurrencyCode.CAD,
						UnitOfMeasureCode.GJ),
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				dealPriceIndex);

		snapshot.setCompanyTraderId(myBusinessContact.generateEntityId());
		snapshot.getDealDetail().setSettlementCurrencyCode(CurrencyCode.CAD);
		indexPlusBuyDeal = PhysicalDeal.create(snapshot);
		flush();
		clearCache();


	}


}
