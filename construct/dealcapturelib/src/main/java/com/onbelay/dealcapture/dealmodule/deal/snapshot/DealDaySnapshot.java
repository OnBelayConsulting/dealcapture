package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

import java.util.List;

public class DealDaySnapshot extends AbstractSnapshot {

    private EntityId dealId;
    private DealDayDetail detail = new DealDayDetail();

    public DealDaySnapshot() {
    }

    public DealDaySnapshot(String errorCode) {
        super(errorCode);
    }

    public DealDaySnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public DealDaySnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public EntityId getDealId() {
        return dealId;
    }

    public void setDealId(EntityId dealId) {
        this.dealId = dealId;
    }

    public DealDayDetail getDetail() {
        return detail;
    }

    public void setDetail(DealDayDetail detail) {
        this.detail = detail;
    }
}
