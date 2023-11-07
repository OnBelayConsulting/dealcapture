package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.AbstractDetail;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

public class PhysicalPositionDetail extends AbstractDetail {

    private String dealPriceValuationValue;
    private String marketPriceValuationValue;

    private BigDecimal dealPrice;
    private BigDecimal marketAdjustment;


    public void copyFrom(PhysicalPositionDetail copy) {
        if (copy.dealPriceValuationValue != null)
            this.dealPriceValuationValue = copy.dealPriceValuationValue;

        if (copy.marketPriceValuationValue != null)
            this.marketPriceValuationValue = copy.marketPriceValuationValue;

        if (copy.dealPrice != null)
            this.dealPrice = copy.dealPrice;

        if (copy.marketAdjustment != null)
            this.marketAdjustment = copy.marketAdjustment;
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
    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    @Column(name = "MARKET_ADJUSTMENT")
    public BigDecimal getMarketAdjustment() {
        return marketAdjustment;
    }

    public void setMarketAdjustment(BigDecimal marketAdjustment) {
        this.marketAdjustment = marketAdjustment;
    }
}
