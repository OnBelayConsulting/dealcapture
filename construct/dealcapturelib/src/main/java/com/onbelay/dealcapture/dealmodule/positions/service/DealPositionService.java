package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.util.List;

public interface DealPositionService {

    public TransactionResult saveDealPositions(
            EntityId dealId,
            List<DealPositionSnapshot> positions);

    public TransactionResult saveAllDealPositions(List<DealPositionSnapshot> positions);


    List<DealPositionSnapshot> findByDeal(EntityId entityId);

    public TransactionResult valuePositions(EntityId dealId);

    public TransactionResult valuePositions(DefinedQuery definedQuery);

    public QuerySelectedPage findPositionIds(DefinedQuery definedQuery);

    public List<DealPositionSnapshot> findByIds(QuerySelectedPage selectedPage);

    DealPositionSnapshot load(EntityId entityId);
}
