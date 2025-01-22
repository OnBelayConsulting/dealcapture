package com.onbelay.dealcapture.pricing.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshotCollection;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshotCollection;

import java.util.List;

public interface InterestIndexRestAdapter {

    TransactionResult save(InterestIndexSnapshot snapshot);

    TransactionResult save(List<InterestIndexSnapshot> snapshots);


    InterestIndexSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    InterestIndexSnapshot load(EntityId interestIndexId);

    TransactionResult saveInterestCurves(
            Integer interestIndexId,
            List<InterestCurveSnapshot> snapshots);

    InterestCurveSnapshotCollection findInterestCurves(
            String queryText,
            Integer start,
            Integer limit);
}
