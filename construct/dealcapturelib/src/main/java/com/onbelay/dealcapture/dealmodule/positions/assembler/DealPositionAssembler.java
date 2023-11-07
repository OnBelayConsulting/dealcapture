package com.onbelay.dealcapture.dealmodule.positions.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public abstract class DealPositionAssembler extends EntityAssembler implements PositionAssembler {

    @Override
    public List<DealPositionSnapshot> assemble(List<DealPosition> positions) {

        return positions
                .stream()
                .map( c-> assemble(c))
                .collect(Collectors.toList());
    }



    protected void setEntityAttributes(
            DealPosition entity,
            DealPositionSnapshot snapshot) {
        super.setEntityAttributes(entity, snapshot);
        snapshot.getDealPositionDetail().copyFrom(entity.getDealPositionDetail());
    }

}
