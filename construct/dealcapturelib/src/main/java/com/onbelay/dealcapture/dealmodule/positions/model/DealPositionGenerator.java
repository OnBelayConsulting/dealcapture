package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;

import java.time.LocalDateTime;
import java.util.List;

public interface DealPositionGenerator {

    public void generatePositionHolders(EvaluationContext context);

    public List<DealPositionSnapshot> generateDealPositionSnapshots(LocalDateTime observedDateTime);

    public DealSummary getDealSummary();

    public List<PositionHolder> getPositionHolders();
}
