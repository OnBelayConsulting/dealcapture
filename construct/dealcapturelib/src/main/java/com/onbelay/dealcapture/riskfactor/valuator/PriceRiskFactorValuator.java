package com.onbelay.dealcapture.riskfactor.valuator;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRiskFactorValuator {

    public void valueRiskFactors(EntityId priceIndexId);

    public void valueRiskFactors(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime);

    public void valueRiskFactors(
            QuerySelectedPage selectedPage,
            LocalDateTime currentDateTime);
}
