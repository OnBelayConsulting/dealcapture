package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.riskfactor.enums.RiskFactorType;

import java.time.LocalDate;

public class BaseRiskFactorHolder {
    private RiskFactorType type;
    private LocalDate marketDate;

    public BaseRiskFactorHolder(final RiskFactorType type) {
        this.type = type;
    }

    public BaseRiskFactorHolder(RiskFactorType type, LocalDate marketDate) {
        this.type = type;
        this.marketDate = marketDate;
    }

    public RiskFactorType getType() {
        return type;
    }

    public LocalDate getMarketDate() {
        return marketDate;
    }
}
