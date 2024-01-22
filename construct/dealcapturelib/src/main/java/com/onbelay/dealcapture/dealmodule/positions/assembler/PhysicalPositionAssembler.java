package com.onbelay.dealcapture.dealmodule.positions.assembler;

import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPosition;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;

public class PhysicalPositionAssembler extends DealPositionAssembler implements PositionAssembler {

    @Override
    public DealPositionSnapshot assemble(DealPosition dealPosition) {
        PhysicalPosition position = (PhysicalPosition)dealPosition;
        PhysicalPositionSnapshot snapshot = new PhysicalPositionSnapshot();
        super.setEntityAttributes(position, snapshot);

        snapshot.getDetail().copyFrom(position.getDetail());

        if (position.getDealPriceFxRiskFactor() != null)
            snapshot.setDealPriceFxRiskFactorId(position.getDealPriceFxRiskFactor().generateEntityId());

        if (position.getMarketPriceRiskFactor() != null)
            snapshot.setMarketPriceRiskFactorId(position.getMarketPriceRiskFactor().generateEntityId());

        if (position.getMarketPriceFxRiskFactor() != null)
            snapshot.setMarketFxRiskFactorId(position.getMarketPriceFxRiskFactor().generateEntityId());

        setChildren(position, snapshot);

        return snapshot;
    }

}
