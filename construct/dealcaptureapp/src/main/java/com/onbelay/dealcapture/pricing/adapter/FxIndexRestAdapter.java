package com.onbelay.dealcapture.pricing.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshotCollection;

import java.util.List;

public interface FxIndexRestAdapter {

    TransactionResult save(FxIndexSnapshot snapshot);

    TransactionResult save(List<FxIndexSnapshot> snapshots);


    FxIndexSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    FxIndexSnapshot load(EntityId fxIndexId);

    TransactionResult saveFxCurves(
            Integer fxIndexId,
            List<FxCurveSnapshot> snapshots);

    FxCurveSnapshotCollection findFxCurves(
            String queryText,
            Integer start,
            Integer limit);
}
