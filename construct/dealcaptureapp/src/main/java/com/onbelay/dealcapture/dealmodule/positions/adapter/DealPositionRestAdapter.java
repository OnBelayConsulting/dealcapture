package com.onbelay.dealcapture.dealmodule.positions.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshotCollection;
import com.onbelay.dealcapture.formulas.model.EvaluationContext;

import java.util.List;

public interface DealPositionRestAdapter {

    TransactionResult generatePositions(
            String queryText,
            EvaluationContext evaluationContext);

    TransactionResult save(
            EntityId dealId,
            DealPositionSnapshot dealSnapshot);

    TransactionResult save(List<DealPositionSnapshot> snapshots);

    DealPositionSnapshot load(EntityId entityId);

    DealPositionSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    public TransactionResult valuePositions(String queryText);
}
