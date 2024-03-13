package com.onbelay.dealcapture.dealmodule.deal.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.model.DealCost;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;

import java.util.List;

public interface DealCostRepository {
    DealCost load(EntityId entityId);

    DealCost findByDealAndName(
            Integer dealId,
            String name);

    List<DealCost> fetchDealCosts(Integer dealId);

    List<DealCostSummary> fetchDealCostSummaries(List<Integer> dealIds);
}
