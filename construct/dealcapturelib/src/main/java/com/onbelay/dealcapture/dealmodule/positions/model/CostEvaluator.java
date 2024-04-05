package com.onbelay.dealcapture.dealmodule.positions.model;

import java.time.LocalDateTime;

public interface CostEvaluator {

    public CostPositionValuationResult valuePosition(LocalDateTime currentDateTime);
}