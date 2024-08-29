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
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.shared.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DealFixture {
	
	private DealFixture() { }

	public static PhysicalDealSnapshot createPhysicalDealSnapshot(
			CommodityCode commodityCode,
			BuySellCode buySellCode,
			LocalDate startDate,
			LocalDate endDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex marketIndex,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			Price dealPrice)  {
		
		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();
		
		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);

		dealSnapshot.getDealDetail().setCommodityCode(commodityCode);
		dealSnapshot.getDealDetail().setDealStatus(dealStatusCode);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setSettlementCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setBuySell(buySellCode);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo(ticketNo);

		dealSnapshot.getDealDetail().setVolumeQuantity(volumeQuantity);
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasureCode(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.FIXED);

		dealSnapshot.getDealDetail().setFixedPrice(dealPrice);

		dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);
		dealSnapshot.setMarketPriceIndexId(marketIndex.generateEntityId());

		return dealSnapshot;
	}


	public static PhysicalDealSnapshot createWithPowerProfilePhysicalDealSnapshot(
			CommodityCode commodityCode,
			BuySellCode buySellCode,
			LocalDate startDate,
			LocalDate endDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PowerProfile powerProfile,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			Price dealPrice)  {

		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);

		dealSnapshot.getDealDetail().setCommodityCode(commodityCode);
		dealSnapshot.getDealDetail().setDealStatus(dealStatusCode);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setSettlementCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setBuySell(buySellCode);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo(ticketNo);

		dealSnapshot.getDealDetail().setVolumeQuantity(volumeQuantity);
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasureCode(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.FIXED);

		dealSnapshot.getDealDetail().setFixedPrice(dealPrice);

		dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.POWER_PROFILE);
		dealSnapshot.setPowerProfileId(powerProfile.generateEntityId());

		return dealSnapshot;
	}


	public static PhysicalDealSnapshot createIndexPhysicalDealSnapshot(
			CommodityCode commodityCode,
			BuySellCode buySellCode,
			LocalDate startDate,
			LocalDate endDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex marketIndex,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			PriceIndex dealPriceIndex)  {

		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);

		dealSnapshot.getDealDetail().setCommodityCode(commodityCode);
		dealSnapshot.getDealDetail().setDealStatus(dealStatusCode);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setSettlementCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setBuySell(buySellCode);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo(ticketNo);

		dealSnapshot.getDealDetail().setVolumeQuantity(volumeQuantity);
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasureCode(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX);
		dealSnapshot.setDealPriceIndexId(dealPriceIndex.generateEntityId());

		dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);
		dealSnapshot.setMarketPriceIndexId(marketIndex.generateEntityId());

		return dealSnapshot;
	}


	public static PhysicalDealSnapshot createIndexWithPowerProfilePhysicalDealSnapshot(
			CommodityCode commodityCode,
			BuySellCode buySellCode,
			LocalDate startDate,
			LocalDate endDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PowerProfile powerProfile,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			PriceIndex dealPriceIndex)  {

		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);

		dealSnapshot.getDealDetail().setCommodityCode(commodityCode);
		dealSnapshot.getDealDetail().setDealStatus(dealStatusCode);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setSettlementCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setBuySell(buySellCode);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo(ticketNo);

		dealSnapshot.getDealDetail().setVolumeQuantity(volumeQuantity);
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasureCode(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX);
		dealSnapshot.setDealPriceIndexId(dealPriceIndex.generateEntityId());

		dealSnapshot.setPowerProfileId(powerProfile.generateEntityId());
		dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.POWER_PROFILE);

		return dealSnapshot;
	}


	public static PhysicalDealSnapshot createIndexPlusPhysicalDealSnapshot(
			CommodityCode commodityCode,
			BuySellCode buySellCode,
			LocalDate startDate,
			LocalDate endDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex marketIndex,
			Price fixedPrice,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			PriceIndex dealPriceIndex)  {

		PhysicalDealSnapshot dealSnapshot = new PhysicalDealSnapshot();

		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);

		dealSnapshot.getDealDetail().setCommodityCode(commodityCode);
		dealSnapshot.getDealDetail().setDealStatus(dealStatusCode);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setSettlementCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setBuySell(buySellCode);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(endDate);
		dealSnapshot.getDealDetail().setTicketNo(ticketNo);

		dealSnapshot.getDealDetail().setVolumeQuantity(volumeQuantity);
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasureCode(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setDealPriceValuationCode(ValuationCode.INDEX_PLUS);
		dealSnapshot.setDealPriceIndexId(dealPriceIndex.generateEntityId());

		dealSnapshot.getDealDetail().setFixedPrice(fixedPrice);

		dealSnapshot.getDetail().setMarketValuationCode(ValuationCode.INDEX);
		dealSnapshot.setMarketPriceIndexId(marketIndex.generateEntityId());

		return dealSnapshot;
	}


	public static PhysicalDeal createSamplePhysicalDeal(
			CommodityCode commodityCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex priceIndex)  {
		
		return PhysicalDeal.create(
				createPhysicalDealSnapshot(
						commodityCode,
						BuySellCode.SELL,
						LocalDate.of(2023, 1, 1),
						LocalDate.of(2023, 12, 31),
						DealStatusCode.PENDING,
						CurrencyCode.CAD,
						ticketNo, 
						companyRole, 
						counterpartyRole,
                        priceIndex,
						BigDecimal.TEN,
						UnitOfMeasureCode.GJ,
						new Price(
								BigDecimal.ONE,
								CurrencyCode.CAD,
								UnitOfMeasureCode.GJ)));
		
	}


	public static PhysicalDeal createPricePhysicalDeal(
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

		PhysicalDealSnapshot snapshot =	createPhysicalDealSnapshot(
				commodityCode,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				reportingCurrencyCode,
				ticketNo,
				companyRole,
				counterpartyRole,
				priceIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				dealPrice);

		snapshot.getDealDetail().setVolumeUnitOfMeasureCode(volumeUnitOfMeasureCode);
		snapshot.getDealDetail().setSettlementCurrencyCode(reportingCurrencyCode);
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
				createIndexPhysicalDealSnapshot(
						commodityCode,
						BuySellCode.SELL,
						startDate,
						endDate,
						DealStatusCode.VERIFIED,
						reportingCurrencyCode,
						ticketNo,
						companyRole,
						counterpartyRole,
						priceIndex,
						BigDecimal.TEN,
						UnitOfMeasureCode.GJ,
						dealPriceIndex));

	}



	public static PhysicalDeal createIndexedPricePlusPhysicalDeal(
			CommodityCode commodityCode,
			BuySellCode buySellCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex marketIndex,
			Price fixedPrice,
			LocalDate startDate,
			LocalDate endDate,
			CurrencyCode reportingCurrencyCode,
			PriceIndex dealPriceIndex
			)  {

		return PhysicalDeal.create(
				createIndexPhysicalDealSnapshot(
						commodityCode,
						buySellCode,
						startDate,
						endDate,
						DealStatusCode.VERIFIED,
						reportingCurrencyCode,
						ticketNo,
						companyRole,
						counterpartyRole,
						marketIndex,
						BigDecimal.TEN,
						UnitOfMeasureCode.GJ,
						dealPriceIndex));

	}


}
