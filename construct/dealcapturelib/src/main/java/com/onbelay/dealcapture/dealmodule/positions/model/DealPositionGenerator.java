package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DealPositionGenerator {

    public void setEvaluationContext(DealPositionsEvaluationContext dealPositionsEvaluationContext);

    public void generatePositionHolders();

    public PositionGenerationResult generatePositionSnapshots();

    public DealSummary getDealSummary();

    public void withCosts(List<DealCostSummary> summaries);

    public void withDealDays(DealDaysContainer container);

    void withPowerProfilePositionViews(Map<LocalDate, List<PowerProfilePositionView>> positionMap);
}
