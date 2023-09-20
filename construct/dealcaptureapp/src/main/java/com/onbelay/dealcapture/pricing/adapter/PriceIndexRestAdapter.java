package com.onbelay.dealcapture.pricing.adapter;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshotCollection;

public interface PriceIndexRestAdapter {

    TransactionResult save(PriceIndexSnapshot snapshot);

    PriceIndexSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    PriceCurveSnapshotCollection findPrices(String queryText, Integer start, Integer limit);
}
