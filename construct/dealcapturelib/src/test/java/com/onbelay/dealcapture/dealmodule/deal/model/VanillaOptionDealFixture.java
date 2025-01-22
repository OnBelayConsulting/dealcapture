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
import com.onbelay.dealcapture.common.enums.OptionExpiryDateRuleToken;
import com.onbelay.dealcapture.dealmodule.deal.enums.*;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.VanillaOptionDealSnapshot;
import com.onbelay.dealcapture.organization.model.CompanyRole;
import com.onbelay.dealcapture.organization.model.CounterpartyRole;
import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.shared.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VanillaOptionDealFixture {
	
	private VanillaOptionDealFixture() { }

	public static VanillaOptionDealSnapshot createVanillaOptionDealWithPowerProfileSnapshot(
			PowerProfile powerProfile,
			CommodityCode commodityCode,
			TradeTypeCode tradeTypeCode,
			OptionTypeCode optionTypeCode,
			BuySellCode buySellCode,
			LocalDate startDate,
			LocalDate expiryDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			Price strikePrice)  {


		VanillaOptionDealSnapshot dealSnapshot = new VanillaOptionDealSnapshot();

		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);

		dealSnapshot.getDealDetail().setCommodityCode(commodityCode);
		dealSnapshot.getDealDetail().setDealStatus(dealStatusCode);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setSettlementCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setBuySell(buySellCode);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(expiryDate);
		dealSnapshot.getDealDetail().setTicketNo(ticketNo);

		dealSnapshot.getDealDetail().setVolumeQuantity(volumeQuantity);
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasureCode(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setTradeTypeCode(tradeTypeCode);
		dealSnapshot.getDetail().setOptionTypeCode(optionTypeCode);
		dealSnapshot.getDetail().setOptionStyleCode(OptionStyleCode.AMERICAN);

		dealSnapshot.getDetail().setStrikePrice(strikePrice);
		dealSnapshot.getDetail().setPremiumPrice(new Price(
				BigDecimal.ONE,
				reportingCurrencyCode,
				volumeUnitOfMeasureCode));

		dealSnapshot.setPowerProfileId(powerProfile.generateEntityId());

		return dealSnapshot;


	}

	public static VanillaOptionDealSnapshot createVanillaOptionDealSnapshot(
			CommodityCode commodityCode,
			TradeTypeCode tradeTypeCode,
			OptionTypeCode optionTypeCode,
			BuySellCode buySellCode,
			LocalDate startDate,
			LocalDate expiryDate,
			DealStatusCode dealStatusCode,
			CurrencyCode reportingCurrencyCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex underlyingIndex,
			BigDecimal volumeQuantity,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			Price strikePrice)  {
		
		VanillaOptionDealSnapshot dealSnapshot = new VanillaOptionDealSnapshot();
		
		dealSnapshot.setCompanyRoleId(companyRole.generateEntityId());
		dealSnapshot.setCounterpartyRoleId(counterpartyRole.generateEntityId());

		dealSnapshot.getDealDetail().setVolumeFrequencyCode(FrequencyCode.DAILY);

		dealSnapshot.getDealDetail().setCommodityCode(commodityCode);
		dealSnapshot.getDealDetail().setDealStatus(dealStatusCode);
		dealSnapshot.getDealDetail().setReportingCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setSettlementCurrencyCode(reportingCurrencyCode);
		dealSnapshot.getDealDetail().setBuySell(buySellCode);
		dealSnapshot.getDealDetail().setStartDate(startDate);
		dealSnapshot.getDealDetail().setEndDate(expiryDate);
		dealSnapshot.getDealDetail().setTicketNo(ticketNo);

		dealSnapshot.getDealDetail().setVolumeQuantity(volumeQuantity);
		dealSnapshot.getDealDetail().setVolumeUnitOfMeasureCode(volumeUnitOfMeasureCode);

		dealSnapshot.getDetail().setTradeTypeCode(tradeTypeCode);
		dealSnapshot.getDetail().setOptionExpiryDateRuleToken(OptionExpiryDateRuleToken.POSITION_END_DATE);
		dealSnapshot.getDetail().setOptionTypeCode(optionTypeCode);
		dealSnapshot.getDetail().setOptionStyleCode(OptionStyleCode.AMERICAN);

		dealSnapshot.getDetail().setStrikePrice(strikePrice);
		dealSnapshot.getDetail().setPremiumPrice(new Price(
				BigDecimal.ONE,
				reportingCurrencyCode,
				volumeUnitOfMeasureCode));

		dealSnapshot.setUnderlyingPriceIndexId(underlyingIndex.generateEntityId());

		return dealSnapshot;
	}


	public static VanillaOptionDeal createOTCBuyPutVanillaOptionDeal(
			CommodityCode commodityCode,
			TradeTypeCode tradeTypeCode,
			OptionTypeCode optionTypeCode,
			BuySellCode buySellCode,
			String ticketNo,
			CompanyRole companyRole,
			CounterpartyRole counterpartyRole,
			PriceIndex underlyingIndex,
			LocalDate startDate,
			LocalDate expiryDate,
			BigDecimal volume,
			UnitOfMeasureCode volumeUnitOfMeasureCode,
			CurrencyCode reportingCurrencyCode,
			Price strikePrice)  {

		VanillaOptionDealSnapshot snapshot =	createVanillaOptionDealSnapshot(
				commodityCode,
				tradeTypeCode,
				optionTypeCode,
				buySellCode,
				startDate,
				expiryDate,
				DealStatusCode.VERIFIED,
				reportingCurrencyCode,
				ticketNo,
				companyRole,
				counterpartyRole,
				underlyingIndex,
				BigDecimal.TEN,
				UnitOfMeasureCode.GJ,
				strikePrice);

		snapshot.getDealDetail().setVolumeUnitOfMeasureCode(volumeUnitOfMeasureCode);
		snapshot.getDealDetail().setSettlementCurrencyCode(reportingCurrencyCode);
		snapshot.getDealDetail().setVolumeQuantity(volume);
		VanillaOptionDeal swapDeal = new VanillaOptionDeal();
		swapDeal.createWith(snapshot);
		return swapDeal;
	}

}
