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
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CommodityCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DealFixture {
	
	private DealFixture() { }

	public static PhysicalDealSnapshot createFixedPriceMarketIndexPhysicalDealSnapshot(
			CommodityCode commodityCode,
			LocalDate startDate,
			LocalDate endDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex priceIndex,
			Price dealPrice)  {
		
		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();
		
		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setCommodityCode(commodityCode);
		dealSnapshot.getDealDetail().setDealStatus(dealStatusCode);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setBuySell(BuySellCode.SELL);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo(ticketNo);
		
		dealSnapshot.setMarketPriceIndexId(priceIndex.generateEntityId());
		
		dealSnapshot.getDealDetail().setVolume(
				new Quantity(
						BigDecimal.valueOf(34.78),
						UnitOfMeasureCode.GJ));

		dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.FIXED);

		dealSnapshot.getDetail().setFixedPrice(dealPrice);

		dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);

		return dealSnapshot;
	}


	public static PhysicalDealSnapshot createIndexPriceMarketIndexPhysicalDealSnapshot(
			CommodityCode commodityCode,
			LocalDate startDate,
			LocalDate endDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex priceIndex,
			PriceIndex dealPriceIndex)  {

		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setCommodityCode(commodityCode);
		dealSnapshot.getDealDetail().setDealStatus(dealStatusCode);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setBuySell(BuySellCode.SELL);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo(ticketNo);

		dealSnapshot.getDealDetail().setVolume(
				new Quantity(
						BigDecimal.valueOf(34.78),
						UnitOfMeasureCode.GJ));

		dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX);
		dealSnapshot.setDealPriceIndexId(dealPriceIndex.generateEntityId());

		dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);
		dealSnapshot.setMarketPriceIndexId(priceIndex.generateEntityId());

		return dealSnapshot;
	}


	public static PhysicalDeal createFixedPricePhysicalDeal(
			CommodityCode commodityCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex priceIndex)  {
		
		return PhysicalDeal.create(
				createFixedPriceMarketIndexPhysicalDealSnapshot(
						commodityCode,
						LocalDate.of(2023, 1, 1),
						LocalDate.of(2023, 12, 31),
						DealStatusCode.PENDING,
						CurrencyCode.CAD,
						ticketNo, 
						companyRole, 
						counterpartyRole,
                        priceIndex,
						new Price(
								BigDecimal.ONE,
								CurrencyCode.CAD,
								UnitOfMeasureCode.GJ)));
		
	}


	public static PhysicalDeal createFixedPricePhysicalDeal(
			CommodityCode commodityCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex priceIndex,
			LocalDate startDate,
			LocalDate endDate,
			BigDecimal volume,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			CurrencyCode reportingCurrencyCode,
			Price dealPrice)  {

		PhysicalDealSnapshot snapshot =	createFixedPriceMarketIndexPhysicalDealSnapshot(
				commodityCode,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				reportingCurrencyCode,
				ticketNo,
				companyRole,
				counterpartyRole,
				priceIndex,
				dealPrice);

		snapshot.getDealDetail().setVolumeUnitOfMeasure(volumeUnitOfMeasureCode);
		snapshot.getDealDetail().setVolumeQuantity(volume);

		return PhysicalDeal.create(snapshot);

	}


	public static PhysicalDeal createIndexedPricePhysicalDeal(
			CommodityCode commodityCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex priceIndex,
			LocalDate startDate,
			LocalDate endDate,
			CurrencyCode reportingCurrencyCode,
			PriceIndex dealPriceIndex)  {

		return PhysicalDeal.create(
				createIndexPriceMarketIndexPhysicalDealSnapshot(
						commodityCode,
						startDate,
						endDate,
						DealStatusCode.VERIFIED,
						reportingCurrencyCode,
						ticketNo,
						companyRole,
						counterpartyRole,
						priceIndex,
						dealPriceIndex));

	}

}
