package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.shared.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TotalCostPositionSummary {
    private Integer dealId;
    private CurrencyCode currencyCode;
    private LocalDate startDate;
    private LocalDateTime createdDateTime;
    private BigDecimal totalCostAmount;

    public TotalCostPositionSummary(
            Integer dealId,
            String currencyCodeValue,
            LocalDate startDate,
            LocalDateTime createdDateTime,
            BigDecimal totalCostAmount) {

        this.dealId = dealId;
        this.currencyCode = CurrencyCode.lookUp(currencyCodeValue);
        this.startDate = startDate;
        this.createdDateTime = createdDateTime;
        this.totalCostAmount = totalCostAmount;
    }

    public Integer getDealId() {
        return dealId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public BigDecimal getTotalCostAmount() {
        return totalCostAmount;
    }
}
