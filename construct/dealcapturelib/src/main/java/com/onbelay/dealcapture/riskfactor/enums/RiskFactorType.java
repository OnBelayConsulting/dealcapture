package com.onbelay.dealcapture.riskfactor.enums;

public enum RiskFactorType {

    PRICE ("P"),
    FX("FX");

    private final String type;

    RiskFactorType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
