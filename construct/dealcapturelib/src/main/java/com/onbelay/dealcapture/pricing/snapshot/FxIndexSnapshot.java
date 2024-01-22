package com.onbelay.dealcapture.pricing.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

import java.util.List;

public class FxIndexSnapshot extends AbstractSnapshot {

    private FxIndexDetail detail = new FxIndexDetail();

    public FxIndexSnapshot() {
    }

    public FxIndexSnapshot(EntityId entityId) {
        super(entityId);
    }

    public FxIndexSnapshot(String errorCode) {
        super(errorCode);
    }

    public FxIndexSnapshot(String errorCode, boolean isPermissionException) {
        super(errorCode, isPermissionException);
    }

    public FxIndexSnapshot(String errorCode, List<String> parameters) {
        super(errorCode, parameters);
    }

    public FxIndexDetail getDetail() {
        return detail;
    }

    public void setDetail(FxIndexDetail detail) {
        this.detail = detail;
    }
}
