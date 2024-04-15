package com.onbelay.dealcapture.dealmodule.positions.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.dealmodule.positions.model.DealHourlyPosition;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class DealHourlyPositionAssembler extends EntityAssembler  {

    public DealHourlyPositionSnapshot assemble(DealHourlyPosition position) {
        DealHourlyPositionSnapshot snapshot = new DealHourlyPositionSnapshot();
        super.setEntityAttributes(position, snapshot);

        snapshot.setDealId(position.getDeal().generateEntityId());
        if (position.getPowerProfilePosition() != null)
            snapshot.setPowerProfilePositionId(position.getPowerProfilePosition().generateEntityId());

        snapshot.getDetail().copyFrom(position.getDetail());
        snapshot.getHourPriceRiskFactorIdMap().copyFrom(position.getHourPriceRiskFactorIdMap());
        snapshot.getHourFixedValueDetail().copyFrom(position.getHourQuantityDayDetail());

        return snapshot;
    }

    public List<DealHourlyPositionSnapshot> assemble(List<DealHourlyPosition> positions) {

        return positions
                .stream()
                .map( c-> assemble(c))
                .collect(Collectors.toList());
    }


}
