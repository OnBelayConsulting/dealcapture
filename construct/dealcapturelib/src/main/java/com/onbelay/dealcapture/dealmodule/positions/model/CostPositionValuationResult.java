package com.onbelay.dealcapture.dealmodule.positions.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CostPositionValuationResult extends BaseValuationResult {

    private BigDecimal costAmount;

    public CostPositionValuationResult(
            Integer positionId,
            LocalDateTime currentDateTime) {

        super(positionId, currentDateTime);
    }

    public BigDecimal getCostAmount() {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount) {
        this.costAmount = costAmount;
    }
}
