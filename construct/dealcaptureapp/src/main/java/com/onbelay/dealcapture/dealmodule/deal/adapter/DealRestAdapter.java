package com.onbelay.dealcapture.dealmodule.deal.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSnapshotCollection;

import java.util.List;

public interface DealRestAdapter {


    TransactionResult save(BaseDealSnapshot dealSnapshot);

    TransactionResult save(List<BaseDealSnapshot> snapshots);

    BaseDealSnapshot load(EntityId entityId);

    DealSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);
}
