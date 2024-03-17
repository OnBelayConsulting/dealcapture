package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.shared.enums.CurrencyCode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Transient;
import org.hibernate.type.YesNoConverter;

import java.math.BigDecimal;

public class PositionSettlementDetail {

    private String settlementReference;

    private Boolean isSettlementPosition;

    private BigDecimal markToMarketValuation;

    private BigDecimal costSettlementAmount;
    private BigDecimal settlementAmount;
    private BigDecimal totalSettlementAmount;

    private String settlementCurrencyCodeValue;

    public void copyFrom(PositionSettlementDetail copy) {

        if (copy.settlementReference != null)
            this.settlementReference = copy.settlementReference;

        if (copy.isSettlementPosition != null)
            this.isSettlementPosition = copy.isSettlementPosition;


        if (copy.costSettlementAmount != null)
            this.costSettlementAmount = copy.costSettlementAmount;

        if (copy.markToMarketValuation != null)
            this.markToMarketValuation = copy.markToMarketValuation;

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

    @Column(name = "SETTLEMENT_REFERENCE")
    public String getSettlementReference() {
        return settlementReference;
    }

    public void setSettlementReference(String settlementReference) {
        this.settlementReference = settlementReference;
    }

    @Column(name = "MTM_VALUATION")
    public BigDecimal getMarkToMarketValuation() {
        return markToMarketValuation;
    }

    public void setMarkToMarketValuation(BigDecimal markToMarketValuation) {
        this.markToMarketValuation = markToMarketValuation;
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

    @Transient
    @JsonIgnore
    public CurrencyCode getSettlementCurrencyCode() {
        return CurrencyCode.lookUp(settlementCurrencyCodeValue);
    }

    public void setSettlementCurrencyCode(CurrencyCode code) {
        this.settlementCurrencyCodeValue = code.getCode();
    }

    @Column(name = "SETTLEMENT_CURRENCY")
    public String getSettlementCurrencyCodeValue() {
        return settlementCurrencyCodeValue;
    }

    public void setSettlementCurrencyCodeValue(String settlementCurrencyCodeValue) {
        this.settlementCurrencyCodeValue = settlementCurrencyCodeValue;
    }
}
