package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;

public class DealCostSummary {

    private Integer id;
    private Integer dealId;
    private CurrencyCode currencyCode;
    private UnitOfMeasureCode unitOfMeasureCode;
    private CostNameCode costNameCode;
    private BigDecimal costValue;

    public DealCostSummary(
            Integer id,
            Integer dealId,
            String costNameCodeValue,
            String currencyCodeValue,
            String unitOfMeasureCodeValue,
            BigDecimal costValue) {

        this.id = id;
        this.dealId = dealId;

        this.costNameCode = CostNameCode.lookUp(costNameCodeValue);
        this.costValue = costValue;
        this.currencyCode = CurrencyCode.lookUp(currencyCodeValue);
        if (unitOfMeasureCodeValue != null)
            this.unitOfMeasureCode = UnitOfMeasureCode.lookUp(unitOfMeasureCodeValue);
    }

    public Integer getId() {
        return id;
    }

    public Integer getDealId() {
        return dealId;
    }

    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }

    public UnitOfMeasureCode getUnitOfMeasureCode() {
        return unitOfMeasureCode;
    }

    public CostTypeCode getCostTypeCode() {
        return costNameCode.getCostTypeCode();
    }

    public CostNameCode getCostNameCode() {
        return costNameCode;
    }

    public BigDecimal getCostValue() {
        return costValue;
    }
}
