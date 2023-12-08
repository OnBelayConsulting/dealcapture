package com.onbelay.dealcapture.riskfactor.valuatorimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.valuator.FxRiskFactorValuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Transactional
public class FxRiskFactorValuatorBean implements FxRiskFactorValuator {

    @Autowired
    private FxRiskFactorRepository fxRiskFactorRepository;

    @Override
    public void valueRiskFactors(EntityId fxIndexId) {
            List<FxRiskFactor> factors = fxRiskFactorRepository.fetchByFxIndex(fxIndexId);
            factors.forEach( f -> f.valueRiskFactor(LocalDateTime.now()));
    }
}
