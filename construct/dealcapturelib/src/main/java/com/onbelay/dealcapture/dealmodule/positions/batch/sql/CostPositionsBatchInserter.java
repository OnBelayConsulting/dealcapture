package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;

import java.util.List;

public interface CostPositionsBatchInserter {
    void savePositions(List<CostPositionSnapshot> positions);
}
