package com.onbelay.dealcapture.dealmodule.positions.assembler;

import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.model.VanillaOptionPosition;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.VanillaOptionPositionSnapshot;

public class VanillaOptionPositionAssembler extends DealPositionAssembler implements PositionAssembler {

    @Override
    public DealPositionSnapshot assemble(DealPosition dealPosition) {
        VanillaOptionPosition position = (VanillaOptionPosition)dealPosition;
        VanillaOptionPositionSnapshot snapshot = new VanillaOptionPositionSnapshot();
        super.setEntityAttributes(position, snapshot);

        snapshot.getPositionDetail().copyFrom(position.getDetail());

        if (position.getUnderlyingPriceRiskFactor() != null)
            snapshot.setUnderlyingPriceRiskFactorId(position.getUnderlyingPriceRiskFactor().generateEntityId());

        if (position.getUnderlyingFxRiskFactor() != null)
            snapshot.setUnderlyingFxRiskFactorId(position.getUnderlyingFxRiskFactor().generateEntityId());

        setChildren(position, snapshot);

        return snapshot;
    }

}
