package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.riskfactor.enums.RiskFactorType;

import java.time.LocalDate;

public class BaseRiskFactorHolder {
    private final RiskFactorType type;
    private LocalDate marketDate;
    private int hourEnding;

    public BaseRiskFactorHolder(final RiskFactorType type) {
        this.type = type;
    }

    public BaseRiskFactorHolder(final RiskFactorType type, final LocalDate marketDate) {
        this.type = type;
        this.marketDate = marketDate;
        this.hourEnding = 0;
    }

    public BaseRiskFactorHolder(
            final RiskFactorType type,
            final LocalDate marketDate,
            final int hourEnding) {
        this.type = type;
        this.marketDate = marketDate;
        this.hourEnding = hourEnding;
    }


    public RiskFactorType getType() {
        return type;
    }

    public LocalDate getMarketDate() {
        return marketDate;
    }

    public int getHourEnding() {
        return hourEnding;
    }

    public String generateUniqueKey() {
        return type.getType() + ":" + marketDate.toString() + ":" + hourEnding;
    }
}
