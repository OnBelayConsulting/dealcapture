package com.onbelay.dealcapture.riskfactor.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface FxRiskFactorService {
    public static final String BEAN_NAME = "fxRiskFactorService";

    public FxRiskFactorSnapshot load(EntityId id);

    public TransactionResult save(
            EntityId fxIndexId,
            List<FxRiskFactorSnapshot> riskFactors);

    public void valueRiskFactors(EntityId fxIndexId);

    public void valueRiskFactors(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime);

    FxRiskFactorSnapshot findByMarketDate(
            EntityId fxIndexId,
            LocalDate marketDate);

    QuerySelectedPage findFxRiskFactorIds(DefinedQuery definedQuery);

    List<FxRiskFactorSnapshot> findByIds(QuerySelectedPage querySelectedPage);

    List<FxRiskFactorSnapshot> findByFxIndexIds(List<Integer> collect);
}
