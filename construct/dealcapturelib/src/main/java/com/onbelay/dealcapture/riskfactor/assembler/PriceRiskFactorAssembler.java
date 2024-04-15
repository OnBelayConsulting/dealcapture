package com.onbelay.dealcapture.riskfactor.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class PriceRiskFactorAssembler extends EntityAssembler {
    
    
    public PriceRiskFactorSnapshot assemble(PriceRiskFactor factor) {
        PriceRiskFactorSnapshot snapshot = new PriceRiskFactorSnapshot();
        super.setEntityAttributes(factor, snapshot);
        snapshot.getDetail().copyFrom(factor.getDetail());
        snapshot.setPriceIndexId(factor.getIndex().generateEntityId());
        snapshot.setFrequencyCode(factor.getIndex().getDetail().getFrequencyCode());
        return snapshot;
    }
    
    public List<PriceRiskFactorSnapshot> assemble(List<PriceRiskFactor> factors) {
        return factors
                .stream()
                .map(c-> assemble(c))
                .collect(Collectors.toList());
    }
    
}
