package com.onbelay.dealcapture.dealmodule.positions.model;

import java.time.LocalDateTime;

public interface PositionEvaluator {

    public PositionValuationResult valuePosition(LocalDateTime currentDateTime);
}
