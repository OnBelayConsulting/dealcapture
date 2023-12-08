package com.onbelay.dealcapture.riskfactor.valuatorimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import com.onbelay.dealcapture.riskfactor.valuator.PriceRiskFactorValuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Transactional
public class PriceRiskFactorValuatorBean implements PriceRiskFactorValuator {

    @Autowired
    private PriceRiskFactorRepository priceRiskFactorRepository;

    @Override
    public void valueRiskFactors(EntityId priceIndexId) {
        List<PriceRiskFactor> factors = priceRiskFactorRepository.fetchByPriceIndex(priceIndexId);
        factors.forEach(f-> f.valueRiskFactor(LocalDateTime.now()));
    }
}
