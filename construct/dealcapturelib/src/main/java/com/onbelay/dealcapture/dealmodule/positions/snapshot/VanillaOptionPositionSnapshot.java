package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

public class VanillaOptionPositionSnapshot extends DealPositionSnapshot {

    private EntityId underlyingPriceRiskFactorId;

    private EntityId underlyingFxRiskFactorId;

    private VanillaOptionPositionPriceDetail priceDetail = new VanillaOptionPositionPriceDetail();

    public VanillaOptionPositionSnapshot() {
        super(DealTypeCode.VANILLA_OPTION);
    }

    public VanillaOptionPositionPriceDetail getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(VanillaOptionPositionPriceDetail priceDetail) {
        this.priceDetail = priceDetail;
    }

    public EntityId getUnderlyingPriceRiskFactorId() {
        return underlyingPriceRiskFactorId;
    }

    public void setUnderlyingPriceRiskFactorId(EntityId underlyingPriceRiskFactorId) {
        this.underlyingPriceRiskFactorId = underlyingPriceRiskFactorId;
    }

    public EntityId getUnderlyingFxRiskFactorId() {
        return underlyingFxRiskFactorId;
    }

    public void setUnderlyingFxRiskFactorId(EntityId underlyingFxRiskFactorId) {
        this.underlyingFxRiskFactorId = underlyingFxRiskFactorId;
    }
}
