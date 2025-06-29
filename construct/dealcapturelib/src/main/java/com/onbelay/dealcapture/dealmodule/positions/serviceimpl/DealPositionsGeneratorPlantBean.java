package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.dealcapture.dealmodule.deal.model.DealDayByMonthView;
import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDayView;
import com.onbelay.dealcapture.dealmodule.deal.model.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGenerator;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGeneratorFactory;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionView;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.PowerProfilePositionsService;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DealPositionsGeneratorPlantBean implements DealPositionsGeneratorPlant {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private DealService dealService;

    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;


    @Override
    public  List<DealPositionGenerator> createDealPositionGenerators(
            DealPositionsEvaluationContext context,
            RiskFactorManager riskFactorManager,
            List<DealSummary> dealSummaries,
            List<DealCostSummary> dealCostSummaries,
            List<DealDayByMonthView> dealDayByMonthViews,
            List<DealHourByDayView> dealHourByDayViews) {

        DealPositionGeneratorFactory factory = DealPositionGeneratorFactory.newFactory();
        if (dealCostSummaries.size() > 0)
            factory.withCosts(dealCostSummaries);

        if (dealDayByMonthViews.size() > 0)
            factory.withDealDayByMonthViews(dealDayByMonthViews);

        if (dealHourByDayViews.size() > 0)
            factory.withHourByDayViews(dealHourByDayViews);


        List<PowerProfilePositionView> powerProfilePositionViews = powerProfilePositionsService.fetchPowerProfilePositionViews(
                context.getStartPositionDate(),
                context.getEndPositionDate(),
                context.getCreatedDateTime());

        if(powerProfilePositionViews.size() > 0)
            factory.withPowerProfilePositionViews(powerProfilePositionViews);


        List<DealPositionGenerator> dealPositionGenerators = new ArrayList<>(dealSummaries.size());

        logger.debug("generate position holders start: " + LocalDateTime.now().toString());
        for (DealSummary summary : dealSummaries) {
            DealPositionGenerator dealPositionGenerator = factory.newGenerator(
                    context,
                    summary,
                    riskFactorManager);
            dealPositionGenerators.add(dealPositionGenerator);

            // create position control
            dealPositionGenerator.generatePositionHolders();

        }
        logger.debug("generate position holders end: " + LocalDateTime.now().toString());

        return dealPositionGenerators;
    }



}
