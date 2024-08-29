package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import jakarta.persistence.Column;

import java.math.BigDecimal;

public class FinancialSwapPositionPriceDetail {
    private BigDecimal paysPriceValue;

    private BigDecimal paysIndexPriceValue;
    private BigDecimal totalPaysPriceValue;
    private BigDecimal receivesPriceValue;

    public void copyFrom(FinancialSwapPositionPriceDetail copy) {

        if (copy.paysPriceValue != null)
            this.paysPriceValue = copy.paysPriceValue;

        if (copy.paysIndexPriceValue != null)
            this.paysIndexPriceValue = copy.paysIndexPriceValue;

        if (copy.totalPaysPriceValue != null)
            this.totalPaysPriceValue = copy.totalPaysPriceValue;

        if (copy.receivesPriceValue != null)
            this.receivesPriceValue = copy.receivesPriceValue;

    }

    public void setDefaults() {

    }


    @Column(name = "PAYS_PRICE")
    public BigDecimal getPaysPriceValue() {
        return paysPriceValue;
    }

    public void setPaysPriceValue(BigDecimal priceValue) {
        this.paysPriceValue = priceValue;
    }



    @Column(name = "PAYS_INDEX_PRICE")
    public BigDecimal getPaysIndexPriceValue() {
        return paysIndexPriceValue;
    }

    public void setPaysIndexPriceValue(BigDecimal paysIndexPriceValue) {
        this.paysIndexPriceValue = paysIndexPriceValue;
    }

    @Column(name = "TOTAL_PAYS_PRICE")
    public BigDecimal getTotalPaysPriceValue() {
        return totalPaysPriceValue;
    }

    public void setTotalPaysPriceValue(BigDecimal totalPaysPriceValue) {
        this.totalPaysPriceValue = totalPaysPriceValue;
    }

    @Column(name = "RECEIVES_PRICE")
    public BigDecimal getReceivesPriceValue() {
        return receivesPriceValue;
    }

    public void setReceivesPriceValue(BigDecimal receivesPriceValue) {
        this.receivesPriceValue = receivesPriceValue;
    }
}
