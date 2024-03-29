package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.onbelay.core.codes.annotations.CodeLabelSerializer;
import com.onbelay.core.codes.annotations.InjectCodeLabel;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Transient;
import org.hibernate.type.YesNoConverter;

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

    private BigDecimal fixedFxRateValue;
    private Integer    fixedFxIndexId;

    private String marketPriceValuationValue;
    private String ticketNo;
    private String buySellCodeValue;

    private String costCurrencyCodeValue;
    private BigDecimal costFxRateValue;
    private Integer    costFxIndexId;

    private Boolean isSettlementPosition;
    private String settlementCurrencyCodeValue;
    private String dealUnitOfMeasureCodeValue;

    private BigDecimal dealPriceRfValue;
    private Integer dealPriceIndexId;
    private BigDecimal dealPriceFxRateValue;
    private Integer dealPriceFxIndexId;

    private BigDecimal marketPriceRfValue;
    private Integer marketPriceIndexId;
    private BigDecimal marketPriceFxValue;
    private Integer marketPriceFxIndexId;

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
    @JsonIgnore
    public UnitOfMeasureCode getDealUnitOfMeasureCode() {
        return UnitOfMeasureCode.lookUp(dealUnitOfMeasureCodeValue);
    }

    @Column(name = "DEAL_UNIT_OF_MEASURE")
    public String getDealUnitOfMeasureCodeValue() {
        return dealUnitOfMeasureCodeValue;
    }

    public void setDealUnitOfMeasureCodeValue(String dealUnitOfMeasureCodeValue) {
        this.dealUnitOfMeasureCodeValue = dealUnitOfMeasureCodeValue;
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

    @Column(name = "FIXED_FX_INDEX_ID")
    public Integer getFixedFxIndexId() {
        return fixedFxIndexId;
    }

    public void setFixedFxIndexId(Integer fixedFxIndexId) {
        this.fixedFxIndexId = fixedFxIndexId;
    }

    @Column(name = "COST_FX_VALUE")
    public BigDecimal getCostFxRateValue() {
        return costFxRateValue;
    }

    public void setCostFxRateValue(BigDecimal costFxRateValue) {
        this.costFxRateValue = costFxRateValue;
    }

    @Column(name = "COST_FX_INDEX_ID")
    public Integer getCostFxIndexId() {
        return costFxIndexId;
    }

    public void setCostFxIndexId(Integer costFxIndexId) {
        this.costFxIndexId = costFxIndexId;
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
    public CurrencyCode getCostCurrencyCode() {
        return CurrencyCode.lookUp(costCurrencyCodeValue);
    }

    @Column(name = "COST_CURRENCY_CODE")
    public String getCostCurrencyCodeValue() {
        return costCurrencyCodeValue;
    }

    public void setCostCurrencyCodeValue(String costCurrencyCodeValue) {
        this.costCurrencyCodeValue = costCurrencyCodeValue;
    }

    @Column(name = "IS_SETTLEMENT_POSITION")
    @Convert(
            converter = YesNoConverter.class
    )
    public Boolean getIsSettlementPosition() {
        return isSettlementPosition;
    }

    public void setIsSettlementPosition(Boolean settlementPosition) {
        isSettlementPosition = settlementPosition;
    }

    @Column(name = "DEAL_PRICE_RF_VALUE")
    public BigDecimal getDealPriceRfValue() {
        return dealPriceRfValue;
    }

    public void setDealPriceRfValue(BigDecimal dealPriceRfValue) {
        this.dealPriceRfValue = dealPriceRfValue;
    }

    @Column(name = "DEAL_PRICE_INDEX_ID")
    public Integer getDealPriceIndexId() {
        return dealPriceIndexId;
    }

    public void setDealPriceIndexId(Integer dealPriceIndexId) {
        this.dealPriceIndexId = dealPriceIndexId;
    }

    @Column(name = "DEAL_PRICE_FX_VALUE")
    public BigDecimal getDealPriceFxRateValue() {
        return dealPriceFxRateValue;
    }


    public void setDealPriceFxRateValue(BigDecimal dealPriceFxRateValue) {
        this.dealPriceFxRateValue = dealPriceFxRateValue;
    }

    @Column(name = "DEAL_PRICE_FX_INDEX_ID")
    public Integer getDealPriceFxIndexId() {
        return dealPriceFxIndexId;
    }

    public void setDealPriceFxIndexId(Integer dealPriceFxIndexId) {
        this.dealPriceFxIndexId = dealPriceFxIndexId;
    }

    @Column(name = "MARKET_PRICE_RF_VALUE")
    public BigDecimal getMarketPriceRfValue() {
        return marketPriceRfValue;
    }

    public void setMarketPriceRfValue(BigDecimal marketPriceRfValue) {
        this.marketPriceRfValue = marketPriceRfValue;
    }

    @Column(name = "MARKET_INDEX_ID")
    public Integer getMarketPriceIndexId() {
        return marketPriceIndexId;
    }

    public void setMarketPriceIndexId(Integer marketPriceIndexId) {
        this.marketPriceIndexId = marketPriceIndexId;
    }

    @Column(name = "MARKET_FX_INDEX_ID")
    public Integer getMarketPriceFxIndexId() {
        return marketPriceFxIndexId;
    }

    public void setMarketPriceFxIndexId(Integer marketPriceFxIndexId) {
        this.marketPriceFxIndexId = marketPriceFxIndexId;
    }


    @Column(name = "MARKET_FX_VALUE")
    public BigDecimal getMarketPriceFxValue() {
        return marketPriceFxValue;
    }

    public void setMarketPriceFxValue(BigDecimal marketPriceFxValue) {
        this.marketPriceFxValue = marketPriceFxValue;
    }
}
