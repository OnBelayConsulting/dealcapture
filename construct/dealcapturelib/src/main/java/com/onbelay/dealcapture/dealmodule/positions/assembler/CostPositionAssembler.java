package com.onbelay.dealcapture.dealmodule.positions.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.dealmodule.positions.model.CostPosition;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class CostPositionAssembler extends EntityAssembler  {

    public CostPositionSnapshot assemble(CostPosition position) {
        CostPositionSnapshot snapshot = new CostPositionSnapshot();
        super.setEntityAttributes(position, snapshot);
        snapshot.setDealId(position.getDeal().generateEntityId());
        snapshot.setDealCostId(position.getDealCost().generateEntityId());

        snapshot.getDetail().copyFrom(position.getDetail());
        if (position.getCostFxRiskFactor() != null)
            snapshot.setCostFxRiskFactorId(position.getCostFxRiskFactor().generateEntityId());

        return snapshot;
    }

    public List<CostPositionSnapshot> assemble(List<CostPosition> positions) {

        return positions
                .stream()
                .map( c-> assemble(c))
                .collect(Collectors.toList());
    }


}
