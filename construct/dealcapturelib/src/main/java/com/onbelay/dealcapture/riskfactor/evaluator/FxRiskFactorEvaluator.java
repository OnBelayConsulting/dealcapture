package com.onbelay.dealcapture.riskfactor.evaluator;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;

import java.time.LocalDateTime;

public interface FxRiskFactorEvaluator {

    public void valueRiskFactors(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime);

    public void valueRiskFactors(EntityId fxIndexId);

    public void valueRiskFactors(
            QuerySelectedPage selectedPage,
            LocalDateTime currentDateTime);
}
