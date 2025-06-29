package com.onbelay.dealcapture.riskfactor.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshotCollection;

import java.util.List;

public interface FxRiskFactorRestAdapter {


    public TransactionResult save(
            EntityId priceIndexId,
            List<FxRiskFactorSnapshot> snapshots);

    public FxRiskFactorSnapshot load(EntityId entityId);

    public FxRiskFactorSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

}
