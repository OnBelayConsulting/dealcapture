package com.onbelay.dealcapture.riskfactor.valuatorimpl;

import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.model.BaseValuationResult;

import java.time.LocalDateTime;

public class PriceRiskFactorEvaluationResult extends BaseValuationResult {

    private Price price;

    public PriceRiskFactorEvaluationResult(Integer domainId, LocalDateTime currentDateTime) {
        super(domainId, currentDateTime);
    }


    public PriceRiskFactorEvaluationResult(
            Integer domainId,
            LocalDateTime currentDateTime,
            Price price) {
        super(domainId, currentDateTime);
        this.price = price;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }
}
