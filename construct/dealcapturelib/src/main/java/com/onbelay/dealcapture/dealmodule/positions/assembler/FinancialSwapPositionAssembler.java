package com.onbelay.dealcapture.dealmodule.positions.assembler;

import com.onbelay.dealcapture.dealmodule.positions.model.DealPosition;
import com.onbelay.dealcapture.dealmodule.positions.model.FinancialSwapPosition;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionSnapshot;

public class FinancialSwapPositionAssembler extends DealPositionAssembler implements PositionAssembler {

    @Override
    public DealPositionSnapshot assemble(DealPosition dealPosition) {
        FinancialSwapPosition position = (FinancialSwapPosition)dealPosition;
        FinancialSwapPositionSnapshot snapshot = new FinancialSwapPositionSnapshot();
        super.setEntityAttributes(position, snapshot);

        snapshot.getPositionDetail().copyFrom(position.getDetail());

        if (position.getFixedPriceFxRiskFactor() != null)
            snapshot.setFixedPriceFxRiskFactorId(position.getFixedPriceFxRiskFactor().generateEntityId());

        if (position.getPaysPriceRiskFactor() != null) {
            snapshot.setPaysPriceIndexName(position.getPaysPriceRiskFactor().getIndex().getDetail().getName());
            snapshot.setPaysPriceRiskFactorId(position.getPaysPriceRiskFactor().generateEntityId());
        }

        if (position.getPaysFxRiskFactor() != null)
            snapshot.setPaysFxRiskFactorId(position.getPaysFxRiskFactor().generateEntityId());

        if (position.getReceivesPriceRiskFactor() != null) {
            snapshot.setReceivesPriceIndexName(position.getReceivesPriceRiskFactor().getIndex().getDetail().getName());
            snapshot.setReceivesPriceRiskFactorId(position.getReceivesPriceRiskFactor().generateEntityId());
        }

        if (position.getReceivesFxRiskFactor() != null)
            snapshot.setReceivesFxRiskFactorId(position.getReceivesFxRiskFactor().generateEntityId());

        setChildren(position, snapshot);

        return snapshot;
    }

}
