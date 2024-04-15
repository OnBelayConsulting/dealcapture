package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.dealcapture.dealmodule.positions.model.ValuationIndexManager;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public abstract class AbstractValuePositionsServiceBean extends BaseDomainService  {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    protected PriceIndexService priceIndexService;

    @Autowired
    protected FxIndexService fxIndexService;

    @Autowired
    protected PriceRiskFactorService priceRiskFactorService;

    @Autowired
    protected FxRiskFactorService fxRiskFactorService;

    protected ValuationIndexManager createValuationIndexManager(
            LocalDate startDate,
            LocalDate endDate) {


        List<PriceIndexSnapshot> activePriceIndices = priceIndexService.findActivePriceIndices();

        List<FxIndexSnapshot> activeFxIndices = fxIndexService.findActiveFxIndices();

        logger.info("fetch active price risk factors start: " + LocalDateTime.now().toString());
        List<PriceRiskFactorSnapshot> activePriceRiskFactors = priceRiskFactorService.findByPriceIndexIds(
                activePriceIndices.stream().map(c-> c.getEntityId().getId()).toList(),
                startDate,
                endDate);

        logger.info("fetch active price risk factors end: " + LocalDateTime.now().toString());

        logger.info("fetch active FX risk factors start: " + LocalDateTime.now().toString());
        List<FxRiskFactorSnapshot> activeFxRiskFactors = fxRiskFactorService.findByFxIndexIds(
                activeFxIndices
                        .stream().map(c -> c.getEntityId().getId()).toList(),
                startDate,
                endDate);
        logger.info("fetch FX price risk factors end: " + LocalDateTime.now().toString());

        return new ValuationIndexManager(
                activePriceIndices,
                activeFxIndices,
                activePriceRiskFactors,
                activeFxRiskFactors);
    }

}
