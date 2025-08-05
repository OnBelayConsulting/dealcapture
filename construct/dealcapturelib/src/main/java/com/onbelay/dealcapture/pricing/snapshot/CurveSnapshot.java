package com.onbelay.dealcapture.pricing.snapshot;

import com.onbelay.core.entity.snapshot.EntityId;

public interface CurveSnapshot {

    public EntityId getIndexId();
    public void setIndexId(EntityId indexId);

    public CurveDetail getDetail();

    public void setDetail(CurveDetail detail);



}
