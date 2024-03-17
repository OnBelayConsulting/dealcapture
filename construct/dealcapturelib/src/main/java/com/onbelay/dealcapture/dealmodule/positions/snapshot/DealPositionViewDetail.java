package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.onbelay.core.codes.annotations.CodeLabelSerializer;
import com.onbelay.core.codes.annotations.InjectCodeLabel;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DealPositionViewDetail {


    private LocalDate startDate;
    private LocalDate endDate;
    private String dealTypeCodeValue;
    private String currencyCodeValue;
    private String volumeUnitOfMeasureValue;
    private BigDecimal volumeQuantityValue;

    private String dealPriceValuationValue;

    private BigDecimal fixedPriceValue;
    private String fixedPriceCurrencyCodeValue;
    private String fixedPriceUnitOfMeasureCodeValue;

    private String marketPriceValuationValue;
    private String ticketNo;
    private String buySellCodeValue;

    private BigDecimal fixedFxRateValue;


    private BigDecimal costFxRateValue;
    private String settlementCurrencyCodeValue;
    private String dealUnitOfMeasureCodeValue;

    private BigDecimal dealPriceRfValue;
    private String dealPriceCurrencyCodeValue;
    private String dealPriceUnitOfMeasureCodeValue;
    private BigDecimal dealPriceFxRateValue;

    private BigDecimal marketPriceRfValue;
    private String marketPriceCurrencyCodeValue;
    private String marketPriceUnitOfMeasureCodeValue;
    private BigDecimal marketPriceFxValue;

    @Transient
    @JsonIgnore
    public DealTypeCode getDealTypeCode() {
        return DealTypeCode.lookUp(dealTypeCodeValue);
    }

    public void setDealTypeCode(DealTypeCode code) {
        this.dealTypeCodeValue = code.getCode();
    }

    @Column(name = "DEAL_TYPE_CODE", insertable = false, updatable = false)
    private String getDealTypeCodeValue() {
        return dealTypeCodeValue;
    }

    private void setDealTypeCodeValue(String dealTypeCodeValue) {
        this.dealTypeCodeValue = dealTypeCodeValue;
    }

    @Column(name = "START_DATE")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Column(name = "END_DATE")
    public LocalDate getEndDate() {
        return endDate;
    }


    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Transient
    public Quantity getQuantity() {
        return new Quantity(
                volumeQuantityValue,
                getVolumeUnitOfMeasure());
    }

    @Column(name = "VOLUME_QUANTITY")
    public BigDecimal getVolumeQuantityValue() {
        return volumeQuantityValue;
    }

    public void setVolumeQuantityValue(BigDecimal volumeQuantityValue) {
        this.volumeQuantityValue = volumeQuantityValue;
    }

    @Transient
    @JsonIgnore
    public CurrencyCode getCurrencyCode() {
        return CurrencyCode.lookUp(currencyCodeValue);
    }

    public void setCurrencyCode(CurrencyCode code) {
        this.currencyCodeValue = code.getCode();
    }

    @Column(name = "CURRENCY_CODE")
    public String getCurrencyCodeValue() {
        return currencyCodeValue;
    }

    public void setCurrencyCodeValue(String currencyCodeValue) {
        this.currencyCodeValue = currencyCodeValue;
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getVolumeUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(volumeUnitOfMeasureValue);
    }

    public void setVolumeUnitOfMeasure(UnitOfMeasureCode code) {
        this.volumeUnitOfMeasureValue = code.getCode();
    }

    @Column(name = "VOLUME_UOM_CODE")
    @InjectCodeLabel(codeFamily = "unitOfMeasureCode", injectedPropertyName = "volumeUnitOfMeasureCodeItem")
    @JsonSerialize(using = CodeLabelSerializer.class)
    public String getVolumeUnitOfMeasureValue() {
        return volumeUnitOfMeasureValue;
    }

    public void setVolumeUnitOfMeasureValue(String volumeUnitOfMeasureValue) {
        this.volumeUnitOfMeasureValue = volumeUnitOfMeasureValue;
    }

    @Transient
    public ValuationCode getDealPriceValuationCode() {
        return ValuationCode.lookUp(dealPriceValuationValue);
    }

    public void setDealPriceValuationCode(ValuationCode code) {
        this.dealPriceValuationValue = code.getCode();
    }

    @Column(name = "DEAL_PRICE_VALUATION_CODE")
    public String getDealPriceValuationValue() {
        return dealPriceValuationValue;
    }

    public void setDealPriceValuationValue(String dealPriceValuationValue) {
        this.dealPriceValuationValue = dealPriceValuationValue;
    }

    @Transient
    public ValuationCode getMarketPriceValuationCode() {
        return ValuationCode.lookUp(marketPriceValuationValue);
    }

    public void setDealMarketValuationCode(ValuationCode code) {
        this.marketPriceValuationValue = code.getCode();
    }

    @Column(name = "MARKET_VALUATION_CODE")
    public String getMarketPriceValuationValue() {
        return marketPriceValuationValue;
    }

    public void setMarketPriceValuationValue(String marketPriceValuationValue) {
        this.marketPriceValuationValue = marketPriceValuationValue;
    }


    @Column(name = "FIXED_PRICE")
    public BigDecimal getFixedPriceValue() {
        return fixedPriceValue;
    }

    public void setFixedPriceValue(BigDecimal fixedPriceValue) {
        this.fixedPriceValue = fixedPriceValue;
    }

    @Column(name = "FIXED_PRICE_CURRENCY_CODE")
    public String getFixedPriceCurrencyCodeValue() {
        return fixedPriceCurrencyCodeValue;
    }

    public void setFixedPriceCurrencyCodeValue(String fixedPriceCurrencyCodeValue) {
        this.fixedPriceCurrencyCodeValue = fixedPriceCurrencyCodeValue;
    }

    @Column(name = "FIXED_PRICE_UOM_CODE")
    public String getFixedPriceUnitOfMeasureCodeValue() {
        return fixedPriceUnitOfMeasureCodeValue;
    }

    public void setFixedPriceUnitOfMeasureCodeValue(String fixedPriceUnitOfMeasureCodeValue) {
        this.fixedPriceUnitOfMeasureCodeValue = fixedPriceUnitOfMeasureCodeValue;
    }

    @Transient
    @JsonIgnore
    public CurrencyCode getFixedPriceCurrencyCode() {
        return CurrencyCode.lookUp(fixedPriceCurrencyCodeValue);
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getFixedPriceUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(fixedPriceUnitOfMeasureCodeValue);
    }

    @Transient
    @JsonIgnore
    public Price getFixedPrice() {
        return new Price(
                fixedPriceValue,
                getFixedPriceCurrencyCode(),
                getFixedPriceUnitOfMeasure());
    }


    @Column(name = "TICKET_NO")
    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    @Transient
    @JsonIgnore
    public BuySellCode getBuySellCode() {
        return BuySellCode.lookUp(buySellCodeValue);
    }

    @Column(name = "BUY_SELL_CODE")
    public String getBuySellCodeValue() {
        return buySellCodeValue;
    }

    public void setBuySellCodeValue(String buySellCodeValue) {
        this.buySellCodeValue = buySellCodeValue;
    }

    @Column(name = "FIXED_FX_VALUE")
    public BigDecimal getFixedFxRateValue() {
        return fixedFxRateValue;
    }

    public void setFixedFxRateValue(BigDecimal fixedFxRateValue) {
        this.fixedFxRateValue = fixedFxRateValue;
    }

    @Column(name = "COST_FX_VALUE")
    public BigDecimal getCostFxRateValue() {
        return costFxRateValue;
    }

    public void setCostFxRateValue(BigDecimal costFxRateValue) {
        this.costFxRateValue = costFxRateValue;
    }

    @Transient
    @JsonIgnore
    public CurrencyCode getSettlementCurrencyCode() {
        return CurrencyCode.lookUp(settlementCurrencyCodeValue);
    }

    @Column(name = "SETTLEMENT_CURRENCY_CODE")
    public String getSettlementCurrencyCodeValue() {
        return settlementCurrencyCodeValue;
    }

    public void setSettlementCurrencyCodeValue(String settlementCurrencyCodeValue) {
        this.settlementCurrencyCodeValue = settlementCurrencyCodeValue;
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getDealUnitOfMeasureCode() {
        return UnitOfMeasureCode.lookUp(dealPriceUnitOfMeasureCodeValue);
    }

    @Column(name = "DEAL_UNIT_OF_MEASURE")
    public String getDealUnitOfMeasureCodeValue() {
        return dealUnitOfMeasureCodeValue;
    }

    public void setDealUnitOfMeasureCodeValue(String dealUnitOfMeasureCodeValue) {
        this.dealUnitOfMeasureCodeValue = dealUnitOfMeasureCodeValue;
    }

    @Column(name = "DEAL_PRICE_RF_VALUE")
    public BigDecimal getDealPriceRfValue() {
        return dealPriceRfValue;
    }

    public void setDealPriceRfValue(BigDecimal dealPriceRfValue) {
        this.dealPriceRfValue = dealPriceRfValue;
    }

    @Column(name = "DEAL_PRICE_CURRENCY_CODE")
    public String getDealPriceCurrencyCodeValue() {
        return dealPriceCurrencyCodeValue;
    }

    public void setDealPriceCurrencyCodeValue(String dealPriceCurrencyCodeValue) {
        this.dealPriceCurrencyCodeValue = dealPriceCurrencyCodeValue;
    }

    @Column(name = "DEAL_PRICE_UOM_CODE")
    public String getDealPriceUnitOfMeasureCodeValue() {
        return dealPriceUnitOfMeasureCodeValue;
    }

    public void setDealPriceUnitOfMeasureCodeValue(String dealPriceUnitOfMeasureCodeValue) {
        this.dealPriceUnitOfMeasureCodeValue = dealPriceUnitOfMeasureCodeValue;
    }

    @Column(name = "DEAL_PRICE_FX_VALUE")
    public BigDecimal getDealPriceFxRateValue() {
        return dealPriceFxRateValue;
    }


    @Transient
    @JsonIgnore
    public CurrencyCode getDealPriceCurrencyCode() {
        return CurrencyCode.lookUp(dealPriceCurrencyCodeValue);
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getDealPriceUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(dealPriceUnitOfMeasureCodeValue);
    }


    @Transient
    @JsonIgnore
    public Price getDealPrice() {
        return new Price(
                dealPriceRfValue,
                getDealPriceCurrencyCode(),
                getDealPriceUnitOfMeasure());
    }


    public void setDealPriceFxRateValue(BigDecimal dealPriceFxRateValue) {
        this.dealPriceFxRateValue = dealPriceFxRateValue;
    }

    @Column(name = "MARKET_PRICE_RF_VALUE")
    public BigDecimal getMarketPriceRfValue() {
        return marketPriceRfValue;
    }

    public void setMarketPriceRfValue(BigDecimal marketPriceRfValue) {
        this.marketPriceRfValue = marketPriceRfValue;
    }

    @Column(name = "MARKET_INDEX_CURRENCY_CODE")
    public String getMarketPriceCurrencyCodeValue() {
        return marketPriceCurrencyCodeValue;
    }

    public void setMarketPriceCurrencyCodeValue(String marketPriceCurrencyCodeValue) {
        this.marketPriceCurrencyCodeValue = marketPriceCurrencyCodeValue;
    }

    @Column(name = "MARKET_INDEX_UOM_CODE")
    public String getMarketPriceUnitOfMeasureCodeValue() {
        return marketPriceUnitOfMeasureCodeValue;
    }

    public void setMarketPriceUnitOfMeasureCodeValue(String marketPriceUnitOfMeasureCodeValue) {
        this.marketPriceUnitOfMeasureCodeValue = marketPriceUnitOfMeasureCodeValue;
    }


    @Transient
    @JsonIgnore
    public CurrencyCode getMarketPriceCurrencyCode() {
        return CurrencyCode.lookUp(marketPriceCurrencyCodeValue);
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getMarketPriceUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(marketPriceUnitOfMeasureCodeValue);
    }


    @Transient
    @JsonIgnore
    public Price getMarketPrice() {
        return new Price(
                marketPriceRfValue,
                getMarketPriceCurrencyCode(),
                getMarketPriceUnitOfMeasure());
    }


    @Column(name = "MARKET_PRICE_FX_VALUE")
    public BigDecimal getMarketPriceFxValue() {
        return marketPriceFxValue;
    }

    public void setMarketPriceFxValue(BigDecimal marketPriceFxValue) {
        this.marketPriceFxValue = marketPriceFxValue;
    }
}
