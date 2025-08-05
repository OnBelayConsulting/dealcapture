package com.onbelay.dealcapture.job.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshotCollection;

public interface DealJobRestAdapter {

    TransactionResult createAndQueueDealJob(DealJobSnapshot snapshot);

    DealJobSnapshot load(EntityId entityId);

    DealJobSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    TransactionResult cancelJob(EntityId dealId);

    TransactionResult deleteJob(EntityId dealId);

}
