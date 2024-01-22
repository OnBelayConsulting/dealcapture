package com.onbelay.dealcapture.dealmodule.deal.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSnapshotCollection;
import org.hibernate.sql.exec.spi.ExecutionContext;

import java.util.List;

public interface DealRestAdapter {


    TransactionResult save(BaseDealSnapshot dealSnapshot);

    TransactionResult save(List<BaseDealSnapshot> snapshots);

    BaseDealSnapshot load(EntityId dealId);

    DealSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    TransactionResult generatePositions(
            Integer dealId,
            ExecutionContext context);
}
