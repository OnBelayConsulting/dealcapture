package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.model.FinancialSwapDealSummary;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;

import java.util.ArrayList;
import java.util.List;

public class FinancialSwapPositionHolder extends BasePositionHolder {

    private PriceRiskFactorHolder receivesPriceRiskFactorHolder;
    private FxRiskFactorHolder receivesFxHolder;

    private List<PriceRiskFactorHolder> basisReceivesHolders = new ArrayList<>();

    private PriceRiskFactorHolder paysPriceRiskFactorHolder;
    private FxRiskFactorHolder paysFxHolder;

    private List<PriceRiskFactorHolder> basisPaysPriceHolders = new ArrayList<>();

    public FinancialSwapPositionHolder(FinancialSwapDealSummary summary) {
        super(summary);
    }

    public PriceRiskFactorHolder getReceivesPriceRiskFactorHolder() {
        return receivesPriceRiskFactorHolder;
    }

    public void setReceivesPriceRiskFactorHolder(PriceRiskFactorHolder receivesPriceRiskFactorHolder) {
        this.receivesPriceRiskFactorHolder = receivesPriceRiskFactorHolder;
    }

    public FxRiskFactorHolder getReceivesFxHolder() {
        return receivesFxHolder;
    }

    public void setReceivesFxHolder(FxRiskFactorHolder receivesFxHolder) {
        this.receivesFxHolder = receivesFxHolder;
    }

    public PriceRiskFactorHolder getPaysPriceRiskFactorHolder() {
        return paysPriceRiskFactorHolder;
    }

    public void setPaysPriceRiskFactorHolder(PriceRiskFactorHolder paysPriceRiskFactorHolder) {
        this.paysPriceRiskFactorHolder = paysPriceRiskFactorHolder;
    }

    public FxRiskFactorHolder getPaysFxHolder() {
        return paysFxHolder;
    }

    public void setPaysFxHolder(FxRiskFactorHolder paysFxHolder) {
        this.paysFxHolder = paysFxHolder;
    }

    public List<PriceRiskFactorHolder> getBasisPaysPriceHolders() {
        return basisPaysPriceHolders;
    }
    public void setBasisPaysPriceHolders(List<PriceRiskFactorHolder> basisPaysPriceHolders) {
        this.basisPaysPriceHolders = basisPaysPriceHolders;
    }

    public List<PriceRiskFactorHolder> getBasisReceivesHolders() {
        return basisReceivesHolders;
    }

    public void setBasisReceivesHolders(List<PriceRiskFactorHolder> basisReceivesHolders) {
        this.basisReceivesHolders = basisReceivesHolders;
    }
}
