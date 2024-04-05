package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.util.List;

public interface CostPositionsBatchInserter {
    void savePositions(List<CostPositionSnapshot> positions);
}
