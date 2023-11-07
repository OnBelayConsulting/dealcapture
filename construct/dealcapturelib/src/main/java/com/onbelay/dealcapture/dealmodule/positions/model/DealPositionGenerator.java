package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.formulas.model.EvaluationContext;

import java.util.List;

public interface DealPositionGenerator {

    public void generatePositionHolders(EvaluationContext context);

    public List<DealPositionSnapshot> generateDealPositionSnapshots();

    public BaseDealSnapshot getDealSnapshot();

    public List<PositionHolder> getPositionHolders();
}
