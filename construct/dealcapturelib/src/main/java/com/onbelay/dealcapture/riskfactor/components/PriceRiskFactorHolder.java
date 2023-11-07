package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.enums.RiskFactorType;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.time.LocalDate;

public class PriceRiskFactorHolder extends BaseRiskFactorHolder {
    private PriceRiskFactorSnapshot riskFactor;
    private PriceIndexSnapshot priceIndex;

    public PriceRiskFactorHolder(PriceRiskFactorSnapshot riskFactor) {
        super(RiskFactorType.PRICE);
        this.riskFactor = riskFactor;
    }

    public PriceRiskFactorHolder(PriceIndexSnapshot priceIndex, LocalDate positionDate) {
        super(RiskFactorType.PRICE, positionDate);
        this.priceIndex = priceIndex;
    }

    public PriceRiskFactorSnapshot getRiskFactor() {
        return riskFactor;
    }

    public void setRiskFactor(PriceRiskFactorSnapshot riskFactor) {
        this.riskFactor = riskFactor;
    }

    public PriceIndexSnapshot getPriceIndex() {
        return priceIndex;
    }

    public boolean hasRiskFactor() {
        return riskFactor != null;
    }
}
