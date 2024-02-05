package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;

import java.util.List;

public interface DealPositionGenerator {

    public void generatePositionHolders(EvaluationContext context);

    public List<DealPositionSnapshot> generateDealPositionSnapshots();

    public DealSummary getDealSummary();

    public List<PositionHolder> getPositionHolders();
}
