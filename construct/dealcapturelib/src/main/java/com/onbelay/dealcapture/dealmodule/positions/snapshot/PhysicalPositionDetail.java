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

    private BigDecimal fixedPriceValue;
    private String fixedPriceCurrencyCodeValue;
    private String fixedPriceUnitOfMeasureCodeValue;


    public void copyFrom(PhysicalPositionDetail copy) {
        if (copy.dealPriceValuationValue != null)
            this.dealPriceValuationValue = copy.dealPriceValuationValue;

        if (copy.marketPriceValuationValue != null)
            this.marketPriceValuationValue = copy.marketPriceValuationValue;

        if (copy.fixedPriceValue != null)
            this.fixedPriceValue = copy.fixedPriceValue;

        if (copy.fixedPriceCurrencyCodeValue != null)
            this.fixedPriceCurrencyCodeValue = copy.fixedPriceCurrencyCodeValue;

        if (copy.fixedPriceUnitOfMeasureCodeValue != null)
            this.fixedPriceUnitOfMeasureCodeValue = copy.fixedPriceUnitOfMeasureCodeValue;
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

    @Transient
    @JsonIgnore
    public CurrencyCode getDealPriceCurrencyCode() {
        return CurrencyCode.lookUp(fixedPriceCurrencyCodeValue);
    }

    public void setDealPriceCurrencyCode(CurrencyCode code) {
        this.fixedPriceCurrencyCodeValue = code.getCode();
    }

    @Column(name = "FIXED_PRICE_CURRENCY_CODE")
    public String getFixedPriceCurrencyCodeValue() {
        return fixedPriceCurrencyCodeValue;
    }

    public void setFixedPriceCurrencyCodeValue(String fixedPriceCurrencyCodeValue) {
        this.fixedPriceCurrencyCodeValue = fixedPriceCurrencyCodeValue;
    }

    @Transient
    @JsonIgnore
    public UnitOfMeasureCode getDealPriceUnitOfMeasure() {
        return UnitOfMeasureCode.lookUp(fixedPriceUnitOfMeasureCodeValue);
    }

    public void setDealPriceUnitOfMeasure(UnitOfMeasureCode code) {
        this.fixedPriceUnitOfMeasureCodeValue = code.getCode();
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
    public Price getDealPrice() {
        return new Price(
                fixedPriceValue,
                getDealPriceCurrencyCode(),
                getDealPriceUnitOfMeasure());
    }
}
