package com.onbelay.dealcapture.riskfactor.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.busmath.model.FxRate;

public class FxRiskFactorSnapshot extends AbstractSnapshot {

    private EntityId fxIndexId;

    private RiskFactorDetail detail = new RiskFactorDetail();

    public EntityId getFxIndexId() {
        return fxIndexId;
    }

    public void setFxIndexId(EntityId fxIndexId) {
        this.fxIndexId = fxIndexId;
    }

    public RiskFactorDetail getDetail() {
        return detail;
    }

    public void setDetail(RiskFactorDetail detail) {
        this.detail = detail;
    }

    public FxRate getCurrentFxRate() {
        return null;
    }
}
