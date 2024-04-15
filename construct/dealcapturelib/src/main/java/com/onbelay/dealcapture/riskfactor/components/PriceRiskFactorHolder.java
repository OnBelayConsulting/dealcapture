package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.enums.RiskFactorType;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.time.LocalDate;

public class PriceRiskFactorHolder extends BaseRiskFactorHolder {
    private final PriceIndexSnapshot priceIndex;

    private PriceRiskFactorSnapshot riskFactor;
    private FxRiskFactorHolder fxRiskFactorHolder;

    public PriceRiskFactorHolder(
            PriceRiskFactorSnapshot riskFactor,
            PriceIndexSnapshot priceIndex) {
        super(RiskFactorType.PRICE, riskFactor.getDetail().getMarketDate(), riskFactor.getDetail().getHourEnding());
        this.riskFactor = riskFactor;
        this.priceIndex = priceIndex;
    }

    public PriceRiskFactorHolder(PriceIndexSnapshot priceIndex, LocalDate positionDate) {
        super(RiskFactorType.PRICE, positionDate);
        this.priceIndex = priceIndex;
    }

    public PriceRiskFactorHolder(
            PriceIndexSnapshot priceIndex,
            LocalDate positionDate,
            int hourEnding) {
        super(RiskFactorType.PRICE, positionDate, hourEnding);
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

    public FxRiskFactorHolder getFxRiskFactorHolder() {
        return fxRiskFactorHolder;
    }

    public void setFxRiskFactorHolder(FxRiskFactorHolder fxRiskFactorHolder) {
        this.fxRiskFactorHolder = fxRiskFactorHolder;
    }

}
