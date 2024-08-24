package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

public class FinancialSwapPositionSnapshot extends DealPositionSnapshot {

    private EntityId receivesPriceRiskFactorId;

    private EntityId receivesFxRiskFactorId;

    private EntityId paysPriceRiskFactorId;

    private EntityId paysFxRiskFactorId;


    public FinancialSwapPositionSnapshot() {
        super(DealTypeCode.PHYSICAL_DEAL);
    }

    public EntityId getReceivesPriceRiskFactorId() {
        return receivesPriceRiskFactorId;
    }

    public void setReceivesPriceRiskFactorId(EntityId receivesPriceRiskFactorId) {
        this.receivesPriceRiskFactorId = receivesPriceRiskFactorId;
    }

    public EntityId getReceivesFxRiskFactorId() {
        return receivesFxRiskFactorId;
    }

    public void setReceivesFxRiskFactorId(EntityId receivesFxRiskFactorId) {
        this.receivesFxRiskFactorId = receivesFxRiskFactorId;
    }

    public EntityId getPaysPriceRiskFactorId() {
        return paysPriceRiskFactorId;
    }

    public void setPaysPriceRiskFactorId(EntityId paysPriceRiskFactorId) {
        this.paysPriceRiskFactorId = paysPriceRiskFactorId;
    }

    public EntityId getPaysFxRiskFactorId() {
        return paysFxRiskFactorId;
    }

    public void setPaysFxRiskFactorId(EntityId paysFxRiskFactorId) {
        this.paysFxRiskFactorId = paysFxRiskFactorId;
    }
}