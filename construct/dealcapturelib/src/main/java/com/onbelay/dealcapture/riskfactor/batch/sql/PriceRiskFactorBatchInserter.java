package com.onbelay.dealcapture.riskfactor.batch.sql;

import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.util.List;

public interface PriceRiskFactorBatchInserter {

    void saveRiskFactors(List<PriceRiskFactorSnapshot> riskFactors);

}
