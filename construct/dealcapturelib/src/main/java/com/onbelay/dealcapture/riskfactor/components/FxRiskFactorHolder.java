package com.onbelay.dealcapture.riskfactor.components;

import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.enums.RiskFactorType;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;

import java.time.LocalDate;

public class FxRiskFactorHolder extends BaseRiskFactorHolder {

    private FxRiskFactorSnapshot riskFactor;
    private FxIndexSnapshot fxIndex;

    public FxRiskFactorHolder(FxRiskFactorSnapshot riskFactor) {
        super(RiskFactorType.FX);
        this.riskFactor = riskFactor;
    }

    public FxRiskFactorHolder(FxIndexSnapshot fxIndex, LocalDate marketDate) {
        super(RiskFactorType.FX, marketDate);
        this.fxIndex = fxIndex;
    }

    public FxRiskFactorSnapshot getRiskFactor() {
        return riskFactor;
    }

    public void setRiskFactor(FxRiskFactorSnapshot riskFactor) {
        this.riskFactor = riskFactor;
    }

    public FxIndexSnapshot getFxIndex() {
        return fxIndex;
    }

    public boolean hasRiskFactor() {
        return riskFactor != null;
    }

    @Override
    public String generateUniqueKey() {
        return "FXI:" + ":" + super.generateUniqueKey();
    }
}
