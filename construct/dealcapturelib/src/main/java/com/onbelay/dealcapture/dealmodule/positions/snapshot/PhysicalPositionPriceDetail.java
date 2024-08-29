package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.shared.enums.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Transient;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;

public class PhysicalPositionPriceDetail {
    private BigDecimal dealPriceValue;

    private BigDecimal dealIndexPriceValue;
    private BigDecimal totalDealPriceValue;
    private BigDecimal marketPriceValue;

    public void copyFrom(PhysicalPositionPriceDetail copy) {

        if (copy.dealPriceValue != null)
            this.dealPriceValue = copy.dealPriceValue;

        if (copy.dealIndexPriceValue != null)
            this.dealIndexPriceValue = copy.dealIndexPriceValue;

        if (copy.totalDealPriceValue != null)
            this.totalDealPriceValue = copy.totalDealPriceValue;

        if (copy.marketPriceValue != null)
            this.marketPriceValue = copy.marketPriceValue;

    }

    public void setDefaults() {

    }


    @Column(name = "DEAL_PRICE")
    public BigDecimal getDealPriceValue() {
        return dealPriceValue;
    }

    public void setDealPriceValue(BigDecimal priceValue) {
        this.dealPriceValue = priceValue;
    }



    @Column(name = "DEAL_INDEX_PRICE")
    public BigDecimal getDealIndexPriceValue() {
        return dealIndexPriceValue;
    }

    public void setDealIndexPriceValue(BigDecimal dealIndexPriceValue) {
        this.dealIndexPriceValue = dealIndexPriceValue;
    }

    @Column(name = "TOTAL_DEAL_PRICE")
    public BigDecimal getTotalDealPriceValue() {
        return totalDealPriceValue;
    }

    public void setTotalDealPriceValue(BigDecimal totalDealPriceValue) {
        this.totalDealPriceValue = totalDealPriceValue;
    }

    @Column(name = "MARKET_PRICE")
    public BigDecimal getMarketPriceValue() {
        return marketPriceValue;
    }

    public void setMarketPriceValue(BigDecimal marketPriceValue) {
        this.marketPriceValue = marketPriceValue;
    }
}
