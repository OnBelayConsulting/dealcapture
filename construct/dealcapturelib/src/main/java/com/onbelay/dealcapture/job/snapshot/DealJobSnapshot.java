package com.onbelay.dealcapture.job.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

import java.util.List;
public class DealJobSnapshot extends AbstractSnapshot {

    private EntityId dependsOnId;

    private DealJobDetail detail = new DealJobDetail();

    public DealJobSnapshot() {
    }

    public DealJobSnapshot(EntityId entityId) {
        super(entityId);
    }

    public DealJobSnapshot(String errorCode) {
        super(errorCode);
    }

    public DealJobSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public DealJobSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public EntityId getDependsOnId() {
        return dependsOnId;
    }

    public void setDependsOnId(EntityId dependsOnId) {
        this.dependsOnId = dependsOnId;
    }

    public DealJobDetail getDetail() {
        return detail;
    }

    public void setDetail(DealJobDetail detail) {
        this.detail = detail;
    }

}
