package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.enums.RiskFactorType;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.time.LocalDate;

public class PriceRiskFactorHolder extends BaseRiskFactorHolder {
    private PriceRiskFactorSnapshot riskFactor;
    private FxRiskFactorHolder fxRiskFactorHolder;
    private PriceIndexSnapshot priceIndex;
    private Conversion conversion;

    public PriceRiskFactorHolder(
            PriceRiskFactorSnapshot riskFactor,
            PriceIndexSnapshot priceIndex) {
        super(RiskFactorType.PRICE, riskFactor.getDetail().getMarketDate());
        this.riskFactor = riskFactor;
        this.priceIndex = priceIndex;
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

    public FxRiskFactorHolder getFxRiskFactorHolder() {
        return fxRiskFactorHolder;
    }

    public void setFxRiskFactorHolder(FxRiskFactorHolder fxRiskFactorHolder) {
        this.fxRiskFactorHolder = fxRiskFactorHolder;
    }

    public Conversion getConversion() {
        return conversion;
    }

    public void setConversion(Conversion conversion) {
        this.conversion = conversion;
    }

    public boolean hasUnitOfMeasureConversion() {
        return conversion != null;
    }
}
