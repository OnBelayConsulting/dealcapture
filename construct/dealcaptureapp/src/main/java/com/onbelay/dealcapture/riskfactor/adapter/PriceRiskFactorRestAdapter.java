package com.onbelay.dealcapture.riskfactor.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshotCollection;

import java.util.List;

public interface PriceRiskFactorRestAdapter {

    public TransactionResult save(
            EntityId priceIndexId,
            List<PriceRiskFactorSnapshot> snapshots);

    public PriceRiskFactorSnapshot load(EntityId entityId);

    public PriceRiskFactorSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

}
