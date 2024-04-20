package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.time.LocalDateTime;
import java.util.List;

public interface DealPositionGenerator {

    public void generatePositionHolders(EvaluationContext context);

    public void generateDealPositionSnapshots(LocalDateTime createdDateTime);

    public void generateCostPositionSnapshots(LocalDateTime createdDateTime);

    public List<DealPositionSnapshot> getDealPositionSnapshots();

    public List<DealHourlyPositionSnapshot> getDealHourlyPositionSnapshots();

    public List<CostPositionSnapshot> getCostPositionSnapshots();


    public DealSummary getDealSummary();

    public List<BasePositionHolder> getPositionHolders();

    public void withCosts(List<DealCostSummary> summaries);

    public void withDealDays(DealDaysContainer container);

    void withPowerProfilePositionViews(List<PowerProfilePositionView> views);

    public List<DealCostSummary> getCostSummaries();

    public DealDaysContainer getDealDayContainer();
}
