package com.onbelay.dealcapture.dealmodule.deal.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.*;

import java.util.List;

public interface DealRestAdapter {


    TransactionResult save(BaseDealSnapshot dealSnapshot);

    TransactionResult save(List<BaseDealSnapshot> snapshots);

    MarkToMarketResult queueMarkToMarketJobs(MarkToMarketJobRequest request);

    TransactionResult saveFile(String originalFileName, byte[] fileContent);

    BaseDealSnapshot load(EntityId dealId);

    DealSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit);

    TransactionResult saveDealCosts(
            Integer dealId,
            List<DealCostSnapshot> dealCostSnapshots);


    TransactionResult saveDealCost(DealCostSnapshot dealCostSnapshot);

    DealCostSnapshotCollection fetchDealCosts(Integer dealId);

    DealCostSnapshot loadDealCost(EntityId dealCostId);
}
