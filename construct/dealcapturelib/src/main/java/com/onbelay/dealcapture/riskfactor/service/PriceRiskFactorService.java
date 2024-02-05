package com.onbelay.dealcapture.riskfactor.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PriceRiskFactorService {
    public static final String BEAN_NAME = "priceRiskFactorService";

    public PriceRiskFactorSnapshot load(EntityId id);

    public TransactionResult save(
            EntityId priceIndexId,
            List<PriceRiskFactorSnapshot> riskFactors);

    public void valueRiskFactors(EntityId priceIndexId);

    public void valueRiskFactors(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime);

    PriceRiskFactorSnapshot findByMarketDate(
            EntityId priceIndexId,
            LocalDate marketDate);

    QuerySelectedPage findPriceRiskFactorIds(DefinedQuery definedQuery);

    List<PriceRiskFactorSnapshot> findByIds(QuerySelectedPage querySelectedPage);

    List<PriceRiskFactorSnapshot> findByPriceIndexIds(
            List<Integer> priceIndexIds,
            LocalDate fromDate,
            LocalDate toDate);
}
