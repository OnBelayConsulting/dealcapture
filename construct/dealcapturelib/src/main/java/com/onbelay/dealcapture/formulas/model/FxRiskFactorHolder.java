package com.onbelay.dealcapture.formulas.model;

import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.components.BaseRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.enums.RiskFactorType;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import org.springframework.cglib.core.Local;

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

}
