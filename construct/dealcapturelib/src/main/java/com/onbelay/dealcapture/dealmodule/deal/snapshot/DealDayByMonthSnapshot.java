package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

import java.util.List;

public class DealDayByMonthSnapshot extends AbstractSnapshot {

    private EntityId dealId;
    private DealDayByMonthDetail detail = new DealDayByMonthDetail();

    public DealDayByMonthSnapshot() {
    }

    public DealDayByMonthSnapshot(String errorCode) {
        super(errorCode);
    }

    public DealDayByMonthSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public DealDayByMonthSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public EntityId getDealId() {
        return dealId;
    }

    public void setDealId(EntityId dealId) {
        this.dealId = dealId;
    }

    public DealDayByMonthDetail getDetail() {
        return detail;
    }

    public void setDetail(DealDayByMonthDetail detail) {
        this.detail = detail;
    }
}
