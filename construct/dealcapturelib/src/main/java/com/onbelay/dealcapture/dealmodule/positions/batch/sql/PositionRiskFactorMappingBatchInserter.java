package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;

import java.util.List;

public interface PositionRiskFactorMappingBatchInserter {

    public void savePositionRiskFactorMappings(List<PositionRiskFactorMappingSnapshot> mappings);

}
