package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.util.List;

public interface DealPositionService {

    public TransactionResult saveDealPositions(
            String positionGeneratorIdentifier,
            List<DealPositionSnapshot> positions);

    List<DealPositionView> fetchDealPositionViews(List<Integer> positionIds);

    List<DealPositionSnapshot> findByDeal(EntityId entityId);

    public List<DealPositionView> findDealPositionViewsByDeal(EntityId dealId);

    public QuerySelectedPage findPositionIds(DefinedQuery definedQuery);

    public List<DealPositionSnapshot> findByIds(QuerySelectedPage selectedPage);

    DealPositionSnapshot load(EntityId entityId);
}
