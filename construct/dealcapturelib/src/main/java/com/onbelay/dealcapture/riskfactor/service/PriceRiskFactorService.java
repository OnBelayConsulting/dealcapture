package com.onbelay.dealcapture.riskfactor.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.time.LocalDate;
import java.util.List;

public interface PriceRiskFactorService {
    public static final String BEAN_NAME = "priceRiskFactorService";

    public PriceRiskFactorSnapshot load(EntityId id);

    public TransactionResult savePriceRiskFactors(
            EntityId priceIndexId,
            List<PriceRiskFactorSnapshot> riskFactors);

    PriceRiskFactorSnapshot findByMarketDate(
            EntityId priceIndexId,
            LocalDate marketDate);

    List<PriceRiskFactorSnapshot> loadAll();
}
