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
import com.onbelay.dealcapture.dealmodule.deal.snapshot.FinancialSwapDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.shared.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinancialSwapDealFixture {
	
	private FinancialSwapDealFixture() { }

	public static FinancialSwapDealSnapshot createFixedForFloatSwapDealSnapshot(
			CommodityCode commodityCode,
			BuySellCode buySellCode,
			LocalDate startDate,
			LocalDate endDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex receivesIndex,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			Price paysPrice)  {
		
		FinancialSwapDealSnapshot dealSnapshot = new FinancialSwapDealSnapshot();
		
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
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasure(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setPaysValuationCode(ValuationCode.FIXED);

		dealSnapshot.getDetail().setFixedPrice(paysPrice);

		dealSnapshot.getDetail().setReceivesValuationCode(ValuationCode.INDEX);
		dealSnapshot.setReceivesPriceIndexId(receivesIndex.generateEntityId());

		return dealSnapshot;
	}


	public static FinancialSwapDealSnapshot createWithPowerProfileFinancialSwapDealSnapshot(
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

		FinancialSwapDealSnapshot dealSnapshot = new FinancialSwapDealSnapshot();

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
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasure(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setPaysValuationCode(ValuationCode.FIXED);

		dealSnapshot.getDetail().setFixedPrice(dealPrice);

		dealSnapshot.getDetail().setReceivesValuationCode(ValuationCode.POWER_PROFILE);
		dealSnapshot.setPowerProfileId(powerProfile.generateEntityId());

		return dealSnapshot;
	}


	public static FinancialSwapDealSnapshot createFloat4FloatSwapDealSnapshot(
			CommodityCode commodityCode,
			BuySellCode buySellCode,
			LocalDate startDate,
			LocalDate endDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex paysIndex,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			PriceIndex receivesIndex)  {

		FinancialSwapDealSnapshot dealSnapshot = new FinancialSwapDealSnapshot();

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
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasure(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setPaysValuationCode(ValuationCode.INDEX);
		dealSnapshot.setPaysPriceIndexId(paysIndex.generateEntityId());

		dealSnapshot.getDetail().setReceivesValuationCode(ValuationCode.INDEX);
		dealSnapshot.setReceivesPriceIndexId(receivesIndex.generateEntityId());

		return dealSnapshot;
	}


	public static FinancialSwapDealSnapshot createFloat4FloatPlusSwapDealSnapshot(
			CommodityCode commodityCode,
			BuySellCode buySellCode,
			LocalDate startDate,
			LocalDate endDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex paysIndex,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			PriceIndex receivesIndex,
			Price receivesFixedPrice)  {

		FinancialSwapDealSnapshot dealSnapshot = new FinancialSwapDealSnapshot();

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
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasure(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setPaysValuationCode(ValuationCode.INDEX);
		dealSnapshot.setPaysPriceIndexId(paysIndex.generateEntityId());

		dealSnapshot.getDetail().setReceivesValuationCode(ValuationCode.INDEX_PLUS);
		dealSnapshot.setReceivesPriceIndexId(receivesIndex.generateEntityId());
		dealSnapshot.getDetail().setFixedPrice(receivesFixedPrice);

		return dealSnapshot;
	}


	public static FinancialSwapDealSnapshot createPaysFloatWithPowerProfileSwapDealSnapshot(
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
			PriceIndex paysIndex)  {

		FinancialSwapDealSnapshot dealSnapshot = new FinancialSwapDealSnapshot();

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
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasure(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setPaysValuationCode(ValuationCode.INDEX);
		dealSnapshot.setPaysPriceIndexId(paysIndex.generateEntityId());

		dealSnapshot.setPowerProfileId(powerProfile.generateEntityId());
		dealSnapshot.getDetail().setReceivesValuationCode(ValuationCode.POWER_PROFILE);

		return dealSnapshot;
	}


	public static FinancialSwapDeal createFixed4FloatSwapDeal(
			CommodityCode commodityCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex receivesIndex,
			LocalDate startDate,
			LocalDate endDate,
			BigDecimal volume,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			CurrencyCode reportingCurrencyCode,
			Price paysPrice)  {

		FinancialSwapDealSnapshot snapshot =	createFixedForFloatSwapDealSnapshot(
				commodityCode,
				BuySellCode.SELL,
				startDate,
				endDate,
				DealStatusCode.VERIFIED,
				reportingCurrencyCode,
				ticketNo,
				companyRole,
				counterpartyRole,
				receivesIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				paysPrice);

		snapshot.getDealDetail().setVolumeUnitOfMeasure(volumeUnitOfMeasureCode);
		snapshot.getDealDetail().setSettlementCurrencyCode(reportingCurrencyCode);
		snapshot.getDealDetail().setVolumeQuantity(volume);
		FinancialSwapDeal swapDeal = new FinancialSwapDeal();
		swapDeal.createWith(snapshot);
		return swapDeal;
	}


	public static FinancialSwapDeal createFloat4FloatSwapDeal(
			CommodityCode commodityCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex paysIndex,
			LocalDate startDate,
			LocalDate endDate,
			CurrencyCode reportingCurrencyCode,
			PriceIndex receivesIndex)  {

			FinancialSwapDealSnapshot snapshot = createFloat4FloatSwapDealSnapshot(
						commodityCode,
						BuySellCode.SELL,
						startDate,
						endDate,
						DealStatusCode.VERIFIED,
						reportingCurrencyCode,
						ticketNo,
						companyRole,
						counterpartyRole,
						paysIndex,
						BigDecimal.TEN,
						UnitOfMeasureCode.GJ,
						receivesIndex);
		FinancialSwapDeal swapDeal = new FinancialSwapDeal();
		swapDeal.createWith(snapshot);
		return swapDeal;

	}

}
