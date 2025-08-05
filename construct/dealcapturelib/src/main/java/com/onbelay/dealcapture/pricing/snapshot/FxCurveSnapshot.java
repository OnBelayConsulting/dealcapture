package com.onbelay.dealcapture.pricing.snapshot;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;

public class FxCurveSnapshot extends AbstractSnapshot implements CurveSnapshot {

    private EntityId indexId;

    private CurveDetail detail = new CurveDetail();

    public CurveDetail getDetail() {
        return detail;
    }

    public void setDetail(CurveDetail detail) {
        this.detail = detail;
    }

    public EntityId getIndexId() {
        return indexId;
    }

    public void setIndexId(EntityId indexId) {
        this.indexId = indexId;
    }
}
