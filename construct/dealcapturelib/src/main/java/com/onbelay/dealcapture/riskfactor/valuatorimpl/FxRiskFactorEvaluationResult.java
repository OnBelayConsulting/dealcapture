package com.onbelay.dealcapture.riskfactor.valuatorimpl;

import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.positions.model.BaseValuationResult;

import java.time.LocalDateTime;

public class FxRiskFactorEvaluationResult extends BaseValuationResult {

    private FxRate rate;

    public FxRiskFactorEvaluationResult(Integer domainId, LocalDateTime currentDateTime) {
        super(domainId, currentDateTime);
    }


    public FxRiskFactorEvaluationResult(
            Integer domainId,
            LocalDateTime currentDateTime,
            FxRate rate) {
        super(domainId, currentDateTime);
        this.rate = rate;
    }

    public FxRate getRate() {
        return rate;
    }

    public void setRate(FxRate rate) {
        this.rate = rate;
    }
}
