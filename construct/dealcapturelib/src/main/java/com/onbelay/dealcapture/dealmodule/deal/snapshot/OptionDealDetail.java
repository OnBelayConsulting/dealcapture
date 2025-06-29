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
package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.exception.OBValidationException;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.OptionExpiryDateRuleToken;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealErrorCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionStyleCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.OptionTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.TradeTypeCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionDealDetail {

	private String optionExpiryDateRuleValue;

	private String tradeTypeCodeValue;

	private String optionTypeCodeValue;
	private String optionStyleCodeValue;

	private BigDecimal strikePriceValue;
	private String strikePriceCurrencyCodeValue;
	private String strikePriceUnitOfMeasureCodeValue;


	private BigDecimal premiumPriceValue;
	private String premiumPriceCurrencyCodeValue;
	private String premiumPriceUnitOfMeasureCodeValue;


	public OptionDealDetail() {
	}

	public void setDefaults() {
		tradeTypeCodeValue = TradeTypeCode.OTC.getCode();
		optionExpiryDateRuleValue = OptionExpiryDateRuleToken.POSITION_END_DATE.getCode();
	}

	public void validate() throws OBValidationException {


		if (optionExpiryDateRuleValue == null || optionExpiryDateRuleValue.isEmpty()) {
			throw new OBValidationException(DealErrorCode.MISSING_OPTION_EXPIRY_DATE_RULE.getCode());
		}

		if (tradeTypeCodeValue == null || tradeTypeCodeValue.isEmpty()) {
			throw new OBValidationException(DealErrorCode.MISSING_TRADE_TYPE_CODE.getCode());
		}
		if (optionTypeCodeValue == null || optionTypeCodeValue.isEmpty()) {
			throw new OBValidationException(DealErrorCode.MISSING_OPTION_TYPE_CODE.getCode());
		}
		if (optionStyleCodeValue == null || optionStyleCodeValue.isEmpty()) {
			throw new OBValidationException(DealErrorCode.MISSING_OPTION_STYLE_CODE.getCode());
		}
		if (strikePriceValue == null) {
			throw new OBValidationException(DealErrorCode.MISSING_STRIKE_PRICE_VALUE.getCode());
		}
		if (strikePriceValue.compareTo(BigDecimal.ZERO) < 0 ) {
			throw new OBValidationException(DealErrorCode.INVALID_STRIKE_PRICE_VALUE.getCode());
		}

		if (strikePriceCurrencyCodeValue == null || strikePriceCurrencyCodeValue.isEmpty()) {
			throw new OBValidationException(DealErrorCode.MISSING_STRIKE_PRICE_CURRENCY.getCode());
		}
		if (strikePriceUnitOfMeasureCodeValue == null || strikePriceUnitOfMeasureCodeValue.isEmpty()) {
			throw new OBValidationException(DealErrorCode.MISSING_STRIKE_PRICE_UOM.getCode());
		}

		if (premiumPriceValue == null) {
			throw new OBValidationException(DealErrorCode.MISSING_PREMIUM_PRICE_VALUE.getCode());
		}
		if (premiumPriceValue.compareTo(BigDecimal.ZERO) < 0 ) {
			throw new OBValidationException(DealErrorCode.INVALID_PREMIUM_PRICE_VALUE.getCode());
		}

		if (premiumPriceCurrencyCodeValue == null || premiumPriceCurrencyCodeValue.isEmpty()) {
			throw new OBValidationException(DealErrorCode.MISSING_PREMIUM_PRICE_CURRENCY.getCode());
		}

		if (premiumPriceUnitOfMeasureCodeValue == null || premiumPriceUnitOfMeasureCodeValue.isEmpty()) {
			throw new OBValidationException(DealErrorCode.MISSING_PREMIUM_PRICE_UOM.getCode());
		}

	}

	@Column(name="OPTION_EXPIRY_DATE_RULE")
	public String getOptionExpiryDateRuleValue() {
		return optionExpiryDateRuleValue;
	}

	public void setOptionExpiryDateRuleValue(String optionExpiryDateRule) {
		this.optionExpiryDateRuleValue = optionExpiryDateRule;
	}

	@Transient
	@JsonIgnore
	public OptionExpiryDateRuleToken getOptionExpiryDateRuleToken() {
		return OptionExpiryDateRuleToken.lookUp(optionExpiryDateRuleValue);
	}

	public void setOptionExpiryDateRuleToken(OptionExpiryDateRuleToken optionExpiryDateRuleToken) {
		this.optionExpiryDateRuleValue = optionExpiryDateRuleToken.getCode();
	}

	@Transient
	@JsonIgnore
	public TradeTypeCode getTradeTypeCode() {
		return TradeTypeCode.lookUp(tradeTypeCodeValue);
	}

	public void setTradeTypeCode(TradeTypeCode code) {
		this.tradeTypeCodeValue = code.getCode();
	}

	@Column(name="TRADE_TYPE_CODE")
	public String getTradeTypeCodeValue() {
		return tradeTypeCodeValue;
	}

	public void setTradeTypeCodeValue(String tradeTypeCodeValue) {
		this.tradeTypeCodeValue = tradeTypeCodeValue;
	}

	@Transient
	@JsonIgnore
	public OptionTypeCode getOptionTypeCode() {
		return OptionTypeCode.lookUp(optionTypeCodeValue);
	}

	public void setOptionTypeCode(OptionTypeCode code) {
		this.optionTypeCodeValue = code.getCode();
	}

	@Column(name="OPTION_TYPE_CODE")
	public String getOptionTypeCodeValue() {
		return optionTypeCodeValue;
	}

	public void setOptionTypeCodeValue(String paysPriceValuationCodeValue) {
		this.optionTypeCodeValue = paysPriceValuationCodeValue;
	}

	@Transient
	@JsonIgnore
	public OptionStyleCode getOptionStyleCode() {
		return OptionStyleCode.lookUp(optionStyleCodeValue);
	}

	public void setOptionStyleCode(OptionStyleCode code) {
		this.optionStyleCodeValue = code.getCode();
	}

	@Column(name="OPTION_STYLE_CODE")
	public String getOptionStyleCodeValue() {
		return optionStyleCodeValue;
	}

	public void setOptionStyleCodeValue(String optionStyleCodeValue) {
		this.optionStyleCodeValue = optionStyleCodeValue;
	}

	@Column(name="STRIKE_PRICE")
	public BigDecimal getStrikePriceValue() {
		return strikePriceValue;
	}

	public void setStrikePriceValue(BigDecimal strikePriceValue) {
		this.strikePriceValue = strikePriceValue;
	}

	@JsonIgnore
	@Transient
	public CurrencyCode getStrikePriceCurrencyCode() {
		return CurrencyCode.lookUp(strikePriceCurrencyCodeValue);
	}

	public void setStrikePriceCurrencyCode(CurrencyCode strikePriceCurrencyCode) {
		this.strikePriceCurrencyCodeValue = strikePriceCurrencyCode.getCode();
	}

	@Column(name="STRIKE_PRICE_CURRENCY_CODE")
	public String getStrikePriceCurrencyCodeValue() {
		return strikePriceCurrencyCodeValue;
	}

	public void setStrikePriceCurrencyCodeValue(String strikePriceCurrencyCodeValue) {
		this.strikePriceCurrencyCodeValue = strikePriceCurrencyCodeValue;
	}

	@JsonIgnore
	@Transient
	public UnitOfMeasureCode getStrikePriceUnitOfMeasureCode() {
		return UnitOfMeasureCode.lookUp(strikePriceUnitOfMeasureCodeValue);
	}

	public void setStrikePriceUnitOfMeasureCode(UnitOfMeasureCode strikePriceUnitOfMeasureCode) {
		this.strikePriceUnitOfMeasureCodeValue = strikePriceUnitOfMeasureCode.getCode();
	}

	@Column(name="STRIKE_PRICE_UOM_CODE")
	public String getStrikePriceUnitOfMeasureCodeValue() {
		return strikePriceUnitOfMeasureCodeValue;
	}

	public void setStrikePriceUnitOfMeasureCodeValue(String strikePriceUnitOfMeasureCodeValue) {
		this.strikePriceUnitOfMeasureCodeValue = strikePriceUnitOfMeasureCodeValue;
	}

	@Column(name="PREMIUM_PRICE")
	public BigDecimal getPremiumPriceValue() {
		return premiumPriceValue;
	}

	public void setPremiumPriceValue(BigDecimal premiumPriceValue) {
		this.premiumPriceValue = premiumPriceValue;
	}

	@Column(name="PREMIUM_PRICE_CURRENCY_CODE")
	public String getPremiumPriceCurrencyCodeValue() {
		return premiumPriceCurrencyCodeValue;
	}

	public void setPremiumPriceCurrencyCodeValue(String premiumPriceCurrencyCodeValue) {
		this.premiumPriceCurrencyCodeValue = premiumPriceCurrencyCodeValue;
	}

	@JsonIgnore
	@Transient
	public CurrencyCode getPremiumPriceCurrencyCode() {
		return CurrencyCode.lookUp(premiumPriceCurrencyCodeValue);
	}

	public void setPremiumPriceCurrencyCode(CurrencyCode currencyCode) {
		this.premiumPriceCurrencyCodeValue = currencyCode.getCode();
	}


	@Column(name="PREMIUM_PRICE_UOM_CODE")
	public String getPremiumPriceUnitOfMeasureCodeValue() {
		return premiumPriceUnitOfMeasureCodeValue;
	}

	public void setPremiumPriceUnitOfMeasureCodeValue(String premiumPriceUnitOfMeasureCodeValue) {
		this.premiumPriceUnitOfMeasureCodeValue = premiumPriceUnitOfMeasureCodeValue;
	}


	@JsonIgnore
	@Transient
	public UnitOfMeasureCode getPremuimPriceUnitOfMeasureCode() {
		return UnitOfMeasureCode.lookUp(premiumPriceUnitOfMeasureCodeValue);
	}

	public void setPremiumPriceUnitOfMeasureCode(UnitOfMeasureCode unitOfMeasureCode) {
		this.premiumPriceUnitOfMeasureCodeValue = unitOfMeasureCode.getCode();
	}

	public void copyFrom(OptionDealDetail copy) {

		if (copy.optionExpiryDateRuleValue != null)
			this.optionExpiryDateRuleValue = copy.optionExpiryDateRuleValue;

		if (copy.tradeTypeCodeValue != null)
			this.tradeTypeCodeValue = copy.tradeTypeCodeValue;

		if (copy.optionTypeCodeValue != null)
			this.optionTypeCodeValue = copy.optionTypeCodeValue;

		if (copy.optionStyleCodeValue != null)
			this.optionStyleCodeValue = copy.optionStyleCodeValue;

		if (copy.strikePriceValue != null)
			this.strikePriceValue = copy.strikePriceValue;

		if (copy.strikePriceCurrencyCodeValue != null)
			this.strikePriceCurrencyCodeValue = copy.strikePriceCurrencyCodeValue;

		if (copy.strikePriceUnitOfMeasureCodeValue != null)
			this.strikePriceUnitOfMeasureCodeValue = copy.strikePriceUnitOfMeasureCodeValue;

		if (copy.premiumPriceValue != null)
			this.premiumPriceValue = copy.premiumPriceValue;

		if (copy.premiumPriceCurrencyCodeValue != null)
			this.premiumPriceCurrencyCodeValue = copy.premiumPriceCurrencyCodeValue;

		if (copy.premiumPriceUnitOfMeasureCodeValue != null)
			this.premiumPriceUnitOfMeasureCodeValue = copy.premiumPriceUnitOfMeasureCodeValue;
    }

	@Transient
	@JsonIgnore
	public void setStrikePrice(Price strikePrice) {
		this.strikePriceValue = strikePrice.getValue();
		this.strikePriceCurrencyCodeValue = strikePrice.getCurrency().getCode();
		this.strikePriceUnitOfMeasureCodeValue = strikePrice.getUnitOfMeasure().getCode();
	}

	@Transient
	@JsonIgnore
	public Price getStrikePrice() {
		return new Price(
				strikePriceValue,
				CurrencyCode.lookUp(strikePriceCurrencyCodeValue),
				UnitOfMeasureCode.lookUp(strikePriceUnitOfMeasureCodeValue));
	}

	@Transient
	@JsonIgnore
	public void setPremiumPrice(Price premiumPrice) {
		this.premiumPriceValue = premiumPrice.getValue();
		this.premiumPriceCurrencyCodeValue = premiumPrice.getCurrency().getCode();
		this.premiumPriceUnitOfMeasureCodeValue = premiumPrice.getUnitOfMeasure().getCode();
	}

	@Transient
	@JsonIgnore
	public Price getPremiumPrice() {
		return new Price(
				premiumPriceValue,
				CurrencyCode.lookUp(premiumPriceCurrencyCodeValue),
				UnitOfMeasureCode.lookUp(premiumPriceUnitOfMeasureCodeValue));
	}
}
