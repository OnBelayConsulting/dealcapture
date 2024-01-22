package com.onbelay.dealcapture.riskfactor.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.busmath.model.FxRate;

import java.util.List;

public class FxRiskFactorSnapshot extends AbstractSnapshot {

    private EntityId fxIndexId;

    private RiskFactorDetail detail = new RiskFactorDetail();

    public FxRiskFactorSnapshot() {
    }

    public FxRiskFactorSnapshot(EntityId entityId) {
        super(entityId);
    }

    public FxRiskFactorSnapshot(String errorCode) {
        super(errorCode);
    }

    public FxRiskFactorSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public FxRiskFactorSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

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

}
