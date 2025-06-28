package com.onbelay.dealcapture.pricing.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
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

    PriceIndexSnapshot load(EntityId priceIndexId);

    TransactionResult savePriceCurves(
            Integer priceIndexId,
            List<PriceCurveSnapshot> snapshots);


    TransactionResult savePriceCurve(PriceCurveSnapshot snapshot);


    PriceCurveSnapshotCollection findPriceCurves(
            String queryText,
            Integer start,
            Integer limit);

    TransactionResult savePriceCurvesFile(
            String originalFilename,
            byte[] bytes);

    PriceCurveSnapshot loadPriceCurve(EntityId entityId);
}
