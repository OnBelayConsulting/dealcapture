package com.onbelay.dealcapture.dealmodule.positions.model;

import java.time.LocalDateTime;

public interface PositionValuator {

    public PositionValuationResult valuePosition(LocalDateTime currentDateTime);
}
