package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.time.LocalDateTime;
import java.util.List;

public interface DealPositionService {

    public TransactionResult saveDealPositions(
            String positionGeneratorIdentifier,
            List<DealPositionSnapshot> positions);

    List<DealPositionSnapshot> findByDeal(EntityId entityId);

    public TransactionResult valuePositions(
            EntityId dealId,
            LocalDateTime currentDateTime);

    public TransactionResult valuePositions(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime);

    public QuerySelectedPage findPositionIds(DefinedQuery definedQuery);

    public List<DealPositionSnapshot> findByIds(QuerySelectedPage selectedPage);

    DealPositionSnapshot load(EntityId entityId);
}
