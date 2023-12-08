package com.onbelay.dealcapture.pricing.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshotCollection;

import java.util.List;

public interface PricingLocationRestAdapter {
    TransactionResult save(PricingLocationSnapshot snapshot);

    TransactionResult save(List<PricingLocationSnapshot> snapshots);

    PricingLocationSnapshotCollection find(String queryText, Integer start, Integer limit);

    PricingLocationSnapshot load(EntityId entityId);
}
