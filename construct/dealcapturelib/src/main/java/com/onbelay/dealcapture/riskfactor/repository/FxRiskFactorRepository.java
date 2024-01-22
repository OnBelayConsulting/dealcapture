package com.onbelay.dealcapture.riskfactor.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;

import java.time.LocalDate;
import java.util.List;

public interface FxRiskFactorRepository {
    public static final String BEAN_NAME = "fxRiskFactorRepository";

    FxRiskFactor load(EntityId entityId);

    FxRiskFactor fetchByMarketDate(EntityId entityId, LocalDate marketDate);

    List<FxRiskFactor> fetchByDatesInclusive(
            EntityId entityId,
            LocalDate fromMarketDate,
            LocalDate toMarketDate);

    List<Integer> findFxRiskFactorIds(DefinedQuery definedQuery);

    List<FxRiskFactor> fetchByIds(QuerySelectedPage selectedPage);

    List<FxRiskFactor> fetchByFxIndex(EntityId fxIndexId);

    public List<FxRiskFactor> fetchByFxIndices(List<Integer> fxIndexIds);

    List<FxRiskFactor> find(DefinedQuery definedQuery);
}
