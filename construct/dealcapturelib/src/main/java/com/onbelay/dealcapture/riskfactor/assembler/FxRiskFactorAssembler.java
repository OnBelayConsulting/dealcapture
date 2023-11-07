package com.onbelay.dealcapture.riskfactor.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class FxRiskFactorAssembler extends EntityAssembler {


    public FxRiskFactorSnapshot assemble(FxRiskFactor factor) {
        FxRiskFactorSnapshot snapshot = new FxRiskFactorSnapshot();
        super.setEntityAttributes(factor, snapshot);
        snapshot.getDetail().copyFrom(factor.getDetail());
        snapshot.setFxIndexId(factor.getIndex().generateEntityId());

        return snapshot;
    }

    public List<FxRiskFactorSnapshot> assemble(List<FxRiskFactor> factors) {
        return factors
                .stream()
                .map(c-> assemble(c))
                .collect(Collectors.toList());
    }

}
