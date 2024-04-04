package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import jakarta.persistence.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CostPositionSnapshot extends AbstractSnapshot {

    private EntityId dealId;

    private EntityId dealCostId;

    private EntityId costFxRiskFactorId;

    private CostPositionDetail detail = new CostPositionDetail();

    public CostPositionSnapshot() {
    }

    public EntityId getDealId() {
        return dealId;
    }

    public void setDealId(EntityId dealId) {
        this.dealId = dealId;
    }

    public EntityId getDealCostId() {
        return dealCostId;
    }

    public void setDealCostId(EntityId dealCostId) {
        this.dealCostId = dealCostId;
    }

    public CostPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(CostPositionDetail detail) {
        this.detail = detail;
    }

    @JsonIgnore
    public Price fetchCostPrice(UnitOfMeasureCode costUnitOfMeasureCode) {
        return new Price(
                detail.getCostValue(),
                detail.getCurrencyCode(),
                costUnitOfMeasureCode);
    }

    @JsonIgnore
    public Quantity getQuantity() {
        return new Quantity(
                detail.getVolumeQuantityValue(),
                detail.getUnitOfMeasure());
    }

    public EntityId getCostFxRiskFactorId() {
        return costFxRiskFactorId;
    }

    public void setCostFxRiskFactorId(EntityId costFxRiskFactorId) {
        this.costFxRiskFactorId = costFxRiskFactorId;
    }

}
