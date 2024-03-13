package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import jakarta.persistence.Column;

import java.math.BigDecimal;

public class PositionSettlementDetail {

    private String settlementReference;

    private BigDecimal costSettlementAmount;
    private BigDecimal settlementAmount;
    private BigDecimal totalSettlementAmount;

    private String settlementCurrencyCodeValue;

    public void copyFrom(PositionSettlementDetail copy) {

        if (copy.settlementReference != null)
            this.settlementReference = copy.settlementReference;

        if (copy.costSettlementAmount != null)
            this.costSettlementAmount = copy.costSettlementAmount;

        if (copy.settlementAmount != null)
            this.settlementAmount = copy.settlementAmount;

        if (copy.totalSettlementAmount != null)
            this.totalSettlementAmount = copy.totalSettlementAmount;

        if (copy.settlementCurrencyCodeValue != null)
            this.settlementCurrencyCodeValue = copy.settlementCurrencyCodeValue;

    }

    public void setDefaults() {
        costSettlementAmount = BigDecimal.ZERO;
    }


    @Column(name = "COST_SETTLEMENT_AMOUNT")
    public BigDecimal getCostSettlementAmount() {
        return costSettlementAmount;
    }

    public void setCostSettlementAmount(BigDecimal costSettlementAmount) {
        this.costSettlementAmount = costSettlementAmount;
    }

    @Column(name = "SETTLEMENT_REFERENCE")
    public String getSettlementReference() {
        return settlementReference;
    }

    public void setSettlementReference(String settlementReference) {
        this.settlementReference = settlementReference;
    }

    @Column(name = "SETTLEMENT_AMOUNT")
    public BigDecimal getSettlementAmount() {
        return settlementAmount;
    }

    public void setSettlementAmount(BigDecimal settlementAmount) {
        this.settlementAmount = settlementAmount;
    }

    @Column(name = "TOTAL_SETTLEMENT_AMOUNT")
    public BigDecimal getTotalSettlementAmount() {
        return totalSettlementAmount;
    }

    public void setTotalSettlementAmount(BigDecimal totalSettlementAmount) {
        this.totalSettlementAmount = totalSettlementAmount;
    }

    @Column(name = "SETTLEMENT_CURRENCY")
    public String getSettlementCurrencyCodeValue() {
        return settlementCurrencyCodeValue;
    }

    public void setSettlementCurrencyCodeValue(String settlementCurrencyCodeValue) {
        this.settlementCurrencyCodeValue = settlementCurrencyCodeValue;
    }
}
