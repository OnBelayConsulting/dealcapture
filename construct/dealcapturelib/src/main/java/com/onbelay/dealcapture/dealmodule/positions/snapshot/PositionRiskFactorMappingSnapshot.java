package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import jakarta.persistence.Embedded;
import jakarta.persistence.Transient;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionRiskFactorMappingSnapshot extends AbstractSnapshot {

    private PositionRiskFactorMappingDetail detail = new PositionRiskFactorMappingDetail();
    private EntityId dealPositionId;
    private EntityId priceRiskFactorId;
    private EntityId fxRiskFactorId;

    public PositionRiskFactorMappingSnapshot() {
    }

    public PositionRiskFactorMappingSnapshot(EntityId entityId) {
        super(entityId);
    }

    public PositionRiskFactorMappingSnapshot(String errorCode) {
        super(errorCode);
    }

    public PositionRiskFactorMappingSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public PositionRiskFactorMappingSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public EntityId getDealPositionId() {
        return dealPositionId;
    }

    @Embedded
    public PositionRiskFactorMappingDetail getDetail() {
        return detail;
    }

    public void setDetail(PositionRiskFactorMappingDetail detail) {
        this.detail = detail;
    }

    public void setDealPositionId(EntityId dealPositionId) {
        this.dealPositionId = dealPositionId;
    }

    public EntityId getPriceRiskFactorId() {
        return priceRiskFactorId;
    }

    public void setPriceRiskFactorId(EntityId priceRiskFactorId) {
        this.priceRiskFactorId = priceRiskFactorId;
    }

    public EntityId getFxRiskFactorId() {
        return fxRiskFactorId;
    }

    public void setFxRiskFactorId(EntityId fxRiskFactorId) {
        this.fxRiskFactorId = fxRiskFactorId;
    }
}
