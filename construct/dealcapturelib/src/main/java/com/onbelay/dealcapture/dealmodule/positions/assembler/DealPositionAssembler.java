package com.onbelay.dealcapture.dealmodule.positions.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.model.PositionRiskFactorMapping;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;

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

    protected void setChildren(
            DealPosition dealPosition,
            DealPositionSnapshot snapshot) {

        snapshot.setRiskFactorMappingSnapshots(
            dealPosition.fetchPositionRiskFactorMappings()
                .stream()
                .map( c-> assemble(snapshot.getEntityId(), c))
                .collect(Collectors.toList()));
    }

    protected void setEntityAttributes(
            DealPosition entity,
            DealPositionSnapshot snapshot) {
        super.setEntityAttributes(entity, snapshot);

        snapshot.setDealId(entity.getDeal().generateEntityId());
        snapshot.getDealPositionDetail().copyFrom(entity.getDealPositionDetail());
        snapshot.getSettlementDetail().copyFrom(entity.getSettlementDetail());
    }

    protected PositionRiskFactorMappingSnapshot assemble(
            EntityId positionId,
            PositionRiskFactorMapping mapping) {

        PositionRiskFactorMappingSnapshot snapshot = new PositionRiskFactorMappingSnapshot();
        snapshot.setDealPositionId(positionId);

        snapshot.getDetail().copyFrom(mapping.getDetail());

        snapshot.setPriceRiskFactorId(mapping.getPriceRiskFactor().generateEntityId());
        if (mapping.getFxRiskFactor() != null)
            snapshot.setFxRiskFactorId(mapping.getFxRiskFactor().generateEntityId());
        return snapshot;
    }

}
