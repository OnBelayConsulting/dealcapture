package com.onbelay.dealcapture.dealmodule.positions.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePosition;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class PowerProfilePositionAssembler extends EntityAssembler  {

    public PowerProfilePositionSnapshot assemble(PowerProfilePosition position) {
        PowerProfilePositionSnapshot snapshot = new PowerProfilePositionSnapshot();
        super.setEntityAttributes(position, snapshot);
        snapshot.setPowerProfileId(position.getPowerProfile().generateEntityId());
        snapshot.setPriceIndexId(position.getPriceIndex().generateEntityId());

        snapshot.getDetail().copyFrom(position.getDetail());
        snapshot.getHourPriceDayDetail().copyFrom(position.getHourPriceDayDetail());
        snapshot.getHourPriceRiskFactorIdMap().copyFrom(position.getHourPriceRiskFactorIdMap());

        return snapshot;
    }

    public List<PowerProfilePositionSnapshot> assemble(List<PowerProfilePosition> positions) {

        return positions
                .stream()
                .map( c-> assemble(c))
                .collect(Collectors.toList());
    }


}
