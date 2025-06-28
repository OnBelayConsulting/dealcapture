package com.onbelay.dealcapture.dealmodule.deal.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshotCollection;

import java.util.List;

public interface PowerProfileRestAdapter {


    TransactionResult save(PowerProfileSnapshot snapshot);

    TransactionResult save(List<PowerProfileSnapshot> snapshots);

    PowerProfileSnapshot load(EntityId powerProfileId);

    PowerProfileSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);
}
