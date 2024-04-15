package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;

import java.util.List;

public interface PowerProfilePositionsBatchInserter {
    void savePositions(List<PowerProfilePositionSnapshot> positions);
}
