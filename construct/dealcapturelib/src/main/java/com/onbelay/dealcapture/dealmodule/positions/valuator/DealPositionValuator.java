package com.onbelay.dealcapture.dealmodule.positions.valuator;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.QuerySelectedPage;

public interface DealPositionValuator {

    public void valuePositions(EntityId dealId);

    public void valuePositions(QuerySelectedPage page);
}
