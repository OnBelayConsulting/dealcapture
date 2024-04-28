package com.onbelay.dealcapture.riskfactor.batch.sql;

import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.util.List;

public interface FxRiskFactorBatchInserter {

    void saveRiskFactors(List<FxRiskFactorSnapshot> riskFactors);

}
