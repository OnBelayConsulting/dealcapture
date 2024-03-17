package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import java.math.BigDecimal;

public class DealCostSummary {

    private Integer id;
    private Integer dealId;

    private DealCostDetail detail = new DealCostDetail();

    public DealCostSummary(
            Integer id,
            Integer dealId,
            String costNameCodeValue,
            BigDecimal costValue) {

        this.id = id;
        this.dealId = dealId;

        detail.setCostNameCodeValue(costNameCodeValue);
        detail.setCostValue(costValue);
    }

    public Integer getId() {
        return id;
    }

    public Integer getDealId() {
        return dealId;
    }

    public DealCostDetail getDetail() {
        return detail;
    }
}
