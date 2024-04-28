package com.onbelay.dealcapture.dealmodule.positions.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.EvaluationContextRequest;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshotCollection;

public interface PowerProfilePositionRestAdapter {

    public TransactionResult generatePositions(EvaluationContextRequest evaluationContextRequest);

    PowerProfilePositionSnapshot load(EntityId entityId);

    PowerProfilePositionSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    public TransactionResult valuePositions(EvaluationContextRequest evaluationContext);


}
