package com.onbelay.dealcapture.dealmodule.positions.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshotCollection;

public interface PowerProfilePositionRestAdapter {


    PowerProfilePositionSnapshot load(EntityId entityId);

    PowerProfilePositionSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

}
