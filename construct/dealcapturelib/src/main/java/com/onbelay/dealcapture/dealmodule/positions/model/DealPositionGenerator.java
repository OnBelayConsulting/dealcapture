package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.time.LocalDateTime;
import java.util.List;

public interface DealPositionGenerator {

    public void generatePositionHolders(EvaluationContext context);

    public List<DealPositionSnapshot> generateDealPositionSnapshots(LocalDateTime observedDateTime);

    public DealSummary getDealSummary();

    public List<PositionHolder> getPositionHolders();

    public void withCosts(List<DealCostSummary> summaries);

    public void withDealDays(DealDaysContainer container);

    public List<DealCostSummary> getCostSummaries();

    public DealDaysContainer getDealDayContainer();
}
