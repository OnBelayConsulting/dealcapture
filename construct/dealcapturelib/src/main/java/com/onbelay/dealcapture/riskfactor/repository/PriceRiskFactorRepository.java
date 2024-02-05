package com.onbelay.dealcapture.riskfactor.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;

import java.time.LocalDate;
import java.util.List;

public interface PriceRiskFactorRepository {
    public static final String BEAN_NAME = "priceRiskFactorRepository";

    PriceRiskFactor load(EntityId entityId);

    PriceRiskFactor fetchByMarketDate(EntityId entityId, LocalDate marketDate);

    List<PriceRiskFactor> fetchByDatesInclusive(
            EntityId entityId,
            LocalDate fromMarketDate,
            LocalDate toMarketDate);

    List<Integer> findPriceRiskFactorIds(DefinedQuery definedQuery);

    List<PriceRiskFactor> fetchByIds(QuerySelectedPage selectedPage);

    List<PriceRiskFactor> fetchByPriceIndex(EntityId priceIndexId);

    List<PriceRiskFactor> find(DefinedQuery definedQuery);

    List<PriceRiskFactor> fetchByPriceIndices(
            List<Integer> priceIndexIds,
            LocalDate fromDate,
            LocalDate toDate);
}
