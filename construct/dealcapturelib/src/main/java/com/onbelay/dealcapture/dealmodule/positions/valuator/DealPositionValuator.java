package com.onbelay.dealcapture.dealmodule.positions.valuator;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.QuerySelectedPage;

import java.time.LocalDateTime;

public interface DealPositionValuator {

    public void valuePositions(
            EntityId dealId,
            LocalDateTime currentDateTime);

    public void valuePositions(
            QuerySelectedPage page,
            LocalDateTime currentDateTime);
}
