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
            String costTypeCodeValue,
            BigDecimal costValue,
            String currencyCodeValue,
            String unitOfMeasureCodeValue) {

        this.id = id;
        this.dealId = dealId;

        detail.setCostNameCodeValue(costNameCodeValue);
        detail.setCostTypeCodeValue(costTypeCodeValue);
        detail.setCostValue(costValue);
        detail.setCurrencyCodeValue(currencyCodeValue);
        detail.setUnitOfMeasureCodeValue(unitOfMeasureCodeValue);
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
