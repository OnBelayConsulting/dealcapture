package com.onbelay.dealcapture.dealmodule.positions.snapshot;

import com.onbelay.core.entity.snapshot.ErrorHoldingSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreatedDateTimeCollection extends ErrorHoldingSnapshot {

    private List<LocalDateTime> createdDateTimeList = new ArrayList<LocalDateTime>();

    public CreatedDateTimeCollection() {
    }

    public CreatedDateTimeCollection(List<LocalDateTime> createdDateTimeList) {
        this.createdDateTimeList = createdDateTimeList;
    }

    public CreatedDateTimeCollection(String errorCode) {
        super(errorCode);
    }

    public CreatedDateTimeCollection(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public CreatedDateTimeCollection(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public List<LocalDateTime> getCreatedDateTimeList() {
        return createdDateTimeList;
    }

    public void setCreatedDateTimeList(List<LocalDateTime> createdDateTimeList) {
        this.createdDateTimeList = createdDateTimeList;
    }
}
