package com.onbelay.dealcapture.dealmodule.positions.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshotCollection;

import java.util.List;

public interface DealPositionRestAdapter {


    TransactionResult save(
            EntityId dealId,
            DealPositionSnapshot dealSnapshot);

    TransactionResult save(
            EntityId dealId,
            List<DealPositionSnapshot> snapshots);

    DealPositionSnapshot load(EntityId entityId);

    DealPositionSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);
}
