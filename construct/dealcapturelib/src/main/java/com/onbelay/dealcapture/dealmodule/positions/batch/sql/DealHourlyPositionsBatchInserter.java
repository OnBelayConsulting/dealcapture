package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;

import java.util.List;

public interface DealHourlyPositionsBatchInserter {
    void savePositions(List<DealHourlyPositionSnapshot> positions);
}
