package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;

import java.util.Collection;

public interface ProfilePositionGenerator {

    public void generatePositionHolders(EvaluationContext contextIn);

    Collection<? extends PowerProfilePositionSnapshot> generatePowerProfilePositionSnapshots();
}
