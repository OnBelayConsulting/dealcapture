package com.onbelay.dealcapture.riskfactor.batch.sql;

import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;

import java.util.List;

public interface FxRiskFactorBatchInserter {

    void saveRiskFactors(List<FxRiskFactorSnapshot> riskFactors);

}
