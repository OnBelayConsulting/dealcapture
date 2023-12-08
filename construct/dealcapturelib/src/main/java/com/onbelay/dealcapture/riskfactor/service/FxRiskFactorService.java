package com.onbelay.dealcapture.riskfactor.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;

import java.time.LocalDate;
import java.util.List;

public interface FxRiskFactorService {
    public static final String BEAN_NAME = "fxRiskFactorService";

    public FxRiskFactorSnapshot load(EntityId id);

    public TransactionResult saveFxRiskFactors(
            EntityId fxIndexId,
            List<FxRiskFactorSnapshot> riskFactors);

    public void valueRiskFactors(EntityId fxIndexId);

    FxRiskFactorSnapshot findByMarketDate(
            EntityId fxIndexId,
            LocalDate marketDate);

    List<FxRiskFactorSnapshot> loadAll();
}
