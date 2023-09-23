package com.onbelay.dealcapture.dealmodule.deal.shared;

import jakarta.persistence.Column;
import java.math.BigDecimal;

public class PhysicalDealPositionDetail extends DealPositionDetail {

    private String dealPriceValuationCode;
    private String marketPriceValuationCode;

    private BigDecimal dealPrice;
    private BigDecimal marketPrice;


    @Column(name = "DEAL_PRICE_VALUATION_CODE")
    public String getDealPriceValuationCode() {
        return dealPriceValuationCode;
    }

    public void setDealPriceValuationCode(String dealPriceValuationCode) {
        this.dealPriceValuationCode = dealPriceValuationCode;
    }

    @Column(name = "MARKET_VALUATION_CODE")
    public String getMarketPriceValuationCode() {
        return marketPriceValuationCode;
    }

    public void setMarketPriceValuationCode(String marketPriceValuationCode) {
        this.marketPriceValuationCode = marketPriceValuationCode;
    }

    @Column(name = "DEAL_PRICE")
    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    @Column(name = "MARKET_PRICE")
    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }
}
