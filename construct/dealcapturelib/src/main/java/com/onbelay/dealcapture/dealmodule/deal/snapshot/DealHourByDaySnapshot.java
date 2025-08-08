package com.onbelay.dealcapture.dealmodule.deal.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;

import java.time.LocalDate;
import java.util.List;

public class DealHourByDaySnapshot extends AbstractSnapshot {

    private EntityId dealId;
    private DealHourByDayDetail detail = new DealHourByDayDetail();

    public DealHourByDaySnapshot() {
    }

    public DealHourByDaySnapshot(LocalDate dayDate, DayTypeCode type) {
        detail.setDealDayDate(dayDate);
        detail.setDealDayTypeCode(type);
    }

    public DealHourByDaySnapshot(String errorCode) {
        super(errorCode);
    }

    public DealHourByDaySnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public DealHourByDaySnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public EntityId getDealId() {
        return dealId;
    }

    public void setDealId(EntityId dealId) {
        this.dealId = dealId;
    }

    public DealHourByDayDetail getDetail() {
        return detail;
    }

    public void setDetail(DealHourByDayDetail detail) {
        this.detail = detail;
    }
}
