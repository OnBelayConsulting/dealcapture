package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.model.VanillaOptionDealSummary;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;

import java.time.LocalDate;

public class VanillaOptionPositionHolder extends BasePositionHolder {
    private LocalDate optionExpiryDate;

    private PriceRiskFactorHolder underlyingPriceRiskFactorHolder;
    private FxRiskFactorHolder underlyingFxHolder;

    public VanillaOptionPositionHolder(VanillaOptionDealSummary summary) {
        super(summary);
    }


    public PriceRiskFactorHolder getUnderlyingPriceRiskFactorHolder() {
        return underlyingPriceRiskFactorHolder;
    }

    public void setUnderlyingPriceRiskFactorHolder(PriceRiskFactorHolder underlyingPriceRiskFactorHolder) {
        this.underlyingPriceRiskFactorHolder = underlyingPriceRiskFactorHolder;
    }

    public LocalDate getOptionExpiryDate() {
        return optionExpiryDate;
    }

    public void setOptionExpiryDate(LocalDate optionExpiryDate) {
        this.optionExpiryDate = optionExpiryDate;
    }

    public FxRiskFactorHolder getUnderlyingFxHolder() {
        return underlyingFxHolder;
    }

    public void setUnderlyingFxHolder(FxRiskFactorHolder underlyingFxHolder) {
        this.underlyingFxHolder = underlyingFxHolder;
    }
}
