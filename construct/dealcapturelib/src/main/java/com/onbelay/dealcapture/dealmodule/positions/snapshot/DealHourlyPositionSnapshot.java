package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

public class DealHourlyPositionSnapshot extends AbstractSnapshot {

    private EntityId dealId;

    private EntityId powerProfilePositionId;

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
