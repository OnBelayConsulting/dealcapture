package com.onbelay.dealcapture.pricing.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PricingLocationSnapshotCollection;

public interface PricingLocationRestAdapter {
    TransactionResult save(PricingLocationSnapshot snapshot);

    PricingLocationSnapshotCollection find(String queryText, Integer start, Integer limit);

    PricingLocationSnapshot load(EntityId entityId);
}
