package com.onbelay.dealcapture.pricing.adapter;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshotCollection;

import java.util.List;

public interface PriceIndexRestAdapter {

    TransactionResult save(PriceIndexSnapshot snapshot);

    TransactionResult save(List<PriceIndexSnapshot> snapshots);


    PriceIndexSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    PriceCurveSnapshotCollection findPrices(String queryText, Integer start, Integer limit);
}
