package com.onbelay.dealcapture.pricing.adapter;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.snapshot.IndexPriceSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PricingIndexSnapshotCollection;

public interface PricingIndexRestAdapter {

    TransactionResult save(PricingIndexSnapshot snapshot);

    PricingIndexSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    IndexPriceSnapshotCollection findPrices(String queryText, Integer start, Integer limit);
}
