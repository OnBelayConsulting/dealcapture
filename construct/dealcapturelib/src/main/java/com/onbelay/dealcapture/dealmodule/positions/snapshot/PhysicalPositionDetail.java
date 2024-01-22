package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhysicalPositionDetail extends AbstractDetail {

    private String dealPriceValuationValue;
    private String marketPriceValuationValue;

    private BigDecimal dealPriceValue;
    private String dealPriceCurrencyCodeValue;
    private String dealPriceUnitOfMeasureCodeValue;

    private BigDecimal dealPriceUOMConversion;
    private BigDecimal marketPriceUOMConversion;


    public void copyFrom(PhysicalPositionDetail copy) {
        if (copy.dealPriceValuationValue != null)
            this.dealPriceValuationValue = copy.dealPriceValuationValue;

        if (copy.marketPriceValuationValue != null)
            this.marketPriceValuationValue = copy.marketPriceValuationValue;

        if (copy.dealPriceValue != null)
            this.dealPriceValue = copy.dealPriceValue;

        if (copy.dealPriceCurrencyCodeValue != null)
            this.dealPriceCurrencyCodeValue = copy.dealPriceCurrencyCodeValue;

        if (copy.dealPriceUnitOfMeasureCodeValue != null)
            this.dealPriceUnitOfMeasureCodeValue = copy.dealPriceUnitOfMeasureCodeValue;

        if (copy.dealPriceUOMConversion != null)
            this.dealPriceUOMConversion = copy.dealPriceUOMConversion;

        if (copy.marketPriceUOMConversion != null)
            this.marketPriceUOMConversion = copy.marketPriceUOMConversion;
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


    @Column(name = "DEAL_PRICE")
    public BigDecimal getDealPriceValue() {
        return dealPriceValue;
    }

    public void setDealPriceValue(BigDecimal dealPriceValue) {
        this.dealPriceValue = dealPriceValue;
    }

    @Transient
    @JsonIgnore
    public CurrencyCode getDealPriceCurrencyCode() {
        return CurrencyCode.lookUp(dealPriceCurrencyCodeValue);
    }

    public void setDealPriceCurrencyCode(CurrencyCode code) {
        this.dealPriceCurrencyCodeValue = code.getCode();
    }

    @Column(name = "DEAL_PRICE_CURRENCY_CODE")
    public String getDealPriceCurrencyCodeValue() {
        return dealPriceCurrencyCodeValue;
    }

    public void setDealPriceCurrencyCodeValue(String dealPriceCurrencyCodeValue) {
        this.dealPriceCurrencyCodeValue = dealPriceCurrencyCodeValue;
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getDealPriceUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(dealPriceUnitOfMeasureCodeValue);
    }

    public void setDealPriceUnitOfMeasure(UnitOfMeasureCode code) {
        this.dealPriceUnitOfMeasureCodeValue = code.getCode();
    }

    @Column(name = "DEAL_PRICE_UOM_CODE")
    public String getDealPriceUnitOfMeasureCodeValue() {
        return dealPriceUnitOfMeasureCodeValue;
    }

    public void setDealPriceUnitOfMeasureCodeValue(String dealPriceUnitOfMeasureCodeValue) {
        this.dealPriceUnitOfMeasureCodeValue = dealPriceUnitOfMeasureCodeValue;
    }

    @Column(name = "DEAL_PRICE_UOM_CONVERSION")
    public BigDecimal getDealPriceUOMConversion() {
        return dealPriceUOMConversion;
    }

    public void setDealPriceUOMConversion(BigDecimal dealPriceUOMConversion) {
        this.dealPriceUOMConversion = dealPriceUOMConversion;
    }

    @Column(name = "MKT_PRICE_UOM_CONVERSION")
    public BigDecimal getMarketPriceUOMConversion() {
        return marketPriceUOMConversion;
    }

    public void setMarketPriceUOMConversion(BigDecimal marketPriceUOMConversion) {
        this.marketPriceUOMConversion = marketPriceUOMConversion;
    }

    @Transient
    @JsonIgnore
    public Price getDealPrice() {
        return new Price(
                dealPriceValue,
                getDealPriceCurrencyCode(),
                getDealPriceUnitOfMeasure());
    }
}
