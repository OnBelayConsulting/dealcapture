package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.util.List;

public interface DealPositionsBatchInserter {
    void savePositions(
            DealTypeCode dealTypeCode,
            List<DealPositionSnapshot> positions);
}
