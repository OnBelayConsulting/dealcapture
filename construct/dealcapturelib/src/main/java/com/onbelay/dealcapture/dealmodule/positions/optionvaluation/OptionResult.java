package com.onbelay.dealcapture.dealmodule.positions.optionvaluation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OptionResult {

    private Double valuation = null;

    public OptionResult(Double valuation) {
        this.valuation = valuation;
    }

    public Double getValuation() {
        return valuation;
    }

    public BigDecimal getValuationAsBigDecimal() {
        BigDecimal val = new BigDecimal(valuation);
        return val.setScale(4, RoundingMode.HALF_EVEN);
    }
}
