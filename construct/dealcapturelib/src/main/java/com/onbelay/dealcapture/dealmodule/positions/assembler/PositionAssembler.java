package com.onbelay.dealcapture.dealmodule.positions.assembler;

import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.util.List;

public interface PositionAssembler {
    List<DealPositionSnapshot> assemble(List<DealPosition> positions);

    DealPositionSnapshot assemble(DealPosition dealPosition);
}
