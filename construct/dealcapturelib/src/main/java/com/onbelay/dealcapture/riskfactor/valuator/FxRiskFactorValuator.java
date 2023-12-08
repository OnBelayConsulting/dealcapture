package com.onbelay.dealcapture.riskfactor.valuator;

import com.onbelay.core.entity.snapshot.EntityId;

public interface FxRiskFactorValuator {

    public void valueRiskFactors(EntityId fxIndexId);
}
