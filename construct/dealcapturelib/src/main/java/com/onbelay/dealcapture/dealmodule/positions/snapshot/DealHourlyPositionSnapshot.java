package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

public class DealHourlyPositionSnapshot extends AbstractSnapshot {

    private EntityId dealId;

    private EntityId powerProfilePositionId;

    private EntityId priceIndexId;

    private EntityId fxRiskFactorId;

    private DealHourlyPositionDetail detail = new DealHourlyPositionDetail();

    private HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap = new HourPriceRiskFactorIdMap();

    private HourFixedValueDayDetail hourFixedValueDayDetail = new HourFixedValueDayDetail();

    public EntityId getDealId() {
        return dealId;
    }

    public void setDealId(EntityId dealId) {
        this.dealId = dealId;
    }

    public DealHourlyPositionDetail getDetail() {
        return detail;
    }

    public void setDetail(DealHourlyPositionDetail detail) {
        this.detail = detail;
    }

    public EntityId getFxRiskFactorId() {
        return fxRiskFactorId;
    }

    public void setFxRiskFactorId(EntityId fxRiskFactorId) {
        this.fxRiskFactorId = fxRiskFactorId;
    }

    public EntityId getPriceIndexId() {
        return priceIndexId;
    }

    public void setPriceIndexId(EntityId priceIndexId) {
        this.priceIndexId = priceIndexId;
    }

    public HourPriceRiskFactorIdMap getHourPriceRiskFactorIdMap() {
        return hourPriceRiskFactorIdMap;
    }

    public void setHourPriceRiskFactorIdMap(HourPriceRiskFactorIdMap hourPriceRiskFactorIdMap) {
        this.hourPriceRiskFactorIdMap = hourPriceRiskFactorIdMap;
    }

    public HourFixedValueDayDetail getHourFixedValueDetail() {
        return hourFixedValueDayDetail;
    }

    public void setHourFixedValueDetail(HourFixedValueDayDetail hourFixedValueDayDetail) {
        this.hourFixedValueDayDetail = hourFixedValueDayDetail;
    }

    public EntityId getPowerProfilePositionId() {
        return powerProfilePositionId;
    }

    public void setPowerProfilePositionId(EntityId powerProfilePositionId) {
        this.powerProfilePositionId = powerProfilePositionId;
    }
}
