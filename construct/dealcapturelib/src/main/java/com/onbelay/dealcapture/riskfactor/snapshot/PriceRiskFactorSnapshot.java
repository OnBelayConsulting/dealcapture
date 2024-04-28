package com.onbelay.dealcapture.riskfactor.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.shared.enums.FrequencyCode;

import java.util.List;

public class PriceRiskFactorSnapshot extends AbstractSnapshot {

    private EntityId priceIndexId;

    private RiskFactorDetail detail = new RiskFactorDetail();

    private FrequencyCode frequencyCode;

    public PriceRiskFactorSnapshot() {
    }

    public PriceRiskFactorSnapshot(EntityId entityId) {
        super(entityId);
    }

    public PriceRiskFactorSnapshot(String errorCode) {
        super(errorCode);
    }

    public PriceRiskFactorSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public PriceRiskFactorSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public EntityId getPriceIndexId() {
        return priceIndexId;
    }

    public void setPriceIndexId(EntityId priceIndexId) {
        this.priceIndexId = priceIndexId;
    }

    public RiskFactorDetail getDetail() {
        return detail;
    }

    public void setDetail(RiskFactorDetail detail) {
        this.detail = detail;
    }

    @JsonIgnore
    public FrequencyCode getFrequencyCode() {
        return frequencyCode;
    }

    public void setFrequencyCode(FrequencyCode frequencyCode) {
        this.frequencyCode = frequencyCode;
    }
}
