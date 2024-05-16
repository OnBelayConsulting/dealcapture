package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealDayByMonthView;
import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDayView;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.*;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGenerator;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGeneratorFactory;
import com.onbelay.dealcapture.dealmodule.positions.model.PositionGenerationResult;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionView;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.service.PowerProfilePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import com.onbelay.dealcapture.riskfactor.batch.sql.PriceRiskFactorBatchInserter;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeneratePositionsServiceBean extends BasePositionsServiceBean implements GeneratePositionsService {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private DealPositionsBatchInserter dealPositionsBatchInserter;

    @Autowired
    private DealHourlyPositionsBatchInserter dealHourlyPositionsBatchInserter;

    @Autowired
    private CostPositionsBatchInserter costPositionsBatchInserter;

    @Autowired
    private PriceRiskFactorBatchInserter priceRiskFactorBatchInserter;

    @Autowired
    private PositionRiskFactorMappingBatchInserter positionRiskFactorMappingBatchInserter;

    @Autowired
    private DealService dealService;

    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;

    @Override
    public TransactionResult generatePositions(
            String positionGenerationIdentifier,
            DealPositionsEvaluationContext context,
            Integer dealId) {

        if (context.getCreatedDateTime() == null)
            throw new RuntimeException("Missing createdDateTime");

           return generatePositions(
                   positionGenerationIdentifier,
                   context,
                   List.of(dealId));

    }

    @Override
    public TransactionResult generatePositions(
            String positionGenerationIdentifier,
            DealPositionsEvaluationContext context,
            List<Integer> dealIdsIn) {

        if (context.validate() == false)
            throw new RuntimeException("Missing at least one EvaluationContext required fields:createdDateTime, currencyCode, startPositionDate");

        RiskFactorManager riskFactorManager = createRiskFactorManager(context);


        SubLister<Integer> subLister = new SubLister<>(dealIdsIn, 100);
        while (subLister.moreElements()) {
            List<Integer> dealIds = subLister.nextList();
            logger.debug("assign pg identifiers start: " + LocalDateTime.now().toString());
            dealService.assignPositionIdentifierToDeals(
                    positionGenerationIdentifier,
                    dealIds);

            logger.debug("assign pg identifiers end: " + LocalDateTime.now().toString());

            logger.debug("get assigned deal summaries start: " + LocalDateTime.now().toString());
            List<DealSummary> summaries = dealService.getAssignedDealSummaries(positionGenerationIdentifier);
            logger.debug("get assigned deal summaries end: " + LocalDateTime.now().toString());

            List<DealCostSummary> dealCostSummaries = dealService.fetchDealCostSummaries(dealIds);

            List<DealHourByDayView> dealHourByDayViews = dealService.fetchDealHourByDayViewsByDates(
                    dealIds,
                    context.getStartPositionDate(),
                    context.getEndPositionDate());

            List<DealDayByMonthView> dealDayByMonthViews = dealService.fetchDealDayByMonthViewsByDates(
                    dealIds,
                    context.getStartPositionDate(),
                    context.getEndPositionDate());

            createAndSavePositions(
                    context,
                    riskFactorManager,
                    summaries,
                    dealCostSummaries,
                    dealDayByMonthViews,
                    dealHourByDayViews);

        }
        return new TransactionResult();
    }

    private void createAndSavePositions(
            DealPositionsEvaluationContext context,
            RiskFactorManager riskFactorManager,
            List<DealSummary> dealSummaries,
            List<DealCostSummary> dealCostSummaries,
            List<DealDayByMonthView> dealDayByMonthViews,
            List<DealHourByDayView> dealHourByDayViews) {

        List<Integer> physicalDealIds = dealSummaries
                .stream()
                .filter(c-> c.getDealTypeCode() == DealTypeCode.PHYSICAL_DEAL)
                .map(c-> c.getDealId())
                .collect(Collectors.toList());

        logger.debug("get physical deal summaries start: " + LocalDateTime.now().toString());
        List<PhysicalDealSummary> physicalDealSummaries = dealService.findPhysicalDealSummariesByIds(physicalDealIds);
        logger.debug("get physical deal summaries end: " + LocalDateTime.now().toString());


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
        for (PhysicalDealSummary summary : physicalDealSummaries) {
            DealPositionGenerator dealPositionGenerator = factory.newGenerator(
                        context,
                        summary,
                        riskFactorManager);
            dealPositionGenerators.add(dealPositionGenerator);

            // create position control
            dealPositionGenerator.generatePositionHolders();

        }
        logger.debug("generate position holders end: " + LocalDateTime.now().toString());

        processPriceRiskFactors(
                riskFactorManager,
                context.getStartPositionDate(),
                context.getEndPositionDate(),
                context.getCreatedDateTime());

        processFxRiskFactors(
                riskFactorManager,
                context.getCreatedDateTime());

        batchSavePositions(
                context.getCreatedDateTime(),
                dealPositionGenerators);
    }


    private void batchSavePositions(
            LocalDateTime createdDateTime,
            List<DealPositionGenerator> dealPositionGenerators) {


        logger.debug("save deal positions start: " + LocalDateTime.now().toString());

        ArrayList<DealPositionSnapshot> positionSnapshots = new ArrayList<>();
        ArrayList<DealHourlyPositionSnapshot> hourlyPositionSnapshots = new ArrayList<>();
        ArrayList<CostPositionSnapshot> costPositionSnapshots = new ArrayList<>();

        ArrayList<Integer> dealIds = new ArrayList<>();
        for (DealPositionGenerator dealPositionGenerator : dealPositionGenerators) {
            dealIds.add(dealPositionGenerator.getDealSummary().getDealId());
            PositionGenerationResult generationResult = dealPositionGenerator.generatePositionSnapshots();
            costPositionSnapshots.addAll(
                    generationResult.getCostPositionSnapshots());
            positionSnapshots.addAll(
                    generationResult.getDealPositionSnapshots());
            hourlyPositionSnapshots.addAll(
                    generationResult.getDealHourlyPositionSnapshots());
        }


        SubLister<DealPositionSnapshot> positionSubLister = new SubLister<>(positionSnapshots, 1000);
        while (positionSubLister.moreElements()) {
            dealPositionsBatchInserter.savePositions(
                    DealTypeCode.PHYSICAL_DEAL,
                    positionSubLister.nextList());
        }

        if (hourlyPositionSnapshots.isEmpty() == false) {
            SubLister<DealHourlyPositionSnapshot> hourlyPositionSnapshotSubLister = new SubLister<>(hourlyPositionSnapshots, 1000);
            while (hourlyPositionSnapshotSubLister.moreElements()) {
                dealHourlyPositionsBatchInserter.savePositions(hourlyPositionSnapshotSubLister.nextList());
            }
        }


        if (costPositionSnapshots.isEmpty() == false) {
            SubLister<CostPositionSnapshot> costPositionSubLister = new SubLister<>(costPositionSnapshots, 1000);
            while (costPositionSubLister.moreElements()) {
                costPositionsBatchInserter.savePositions(costPositionSubLister.nextList());
            }
        }

        logger.debug("save deal positions end: " + LocalDateTime.now().toString());

        logger.debug("save position risk factor mappings start: " + LocalDateTime.now().toString());

        ArrayList<PositionRiskFactorMappingSnapshot> mappings = new ArrayList<>();
        for (DealPositionSnapshot snapshot : positionSnapshots) {
            if (snapshot.getRiskFactorMappingSnapshots().isEmpty() == false) {
                snapshot.setIdInMappings();
                mappings.addAll(snapshot.getRiskFactorMappingSnapshots());
            }
        }

        if (mappings.size() > 0)
            positionRiskFactorMappingBatchInserter.savePositionRiskFactorMappings(mappings);
        logger.debug("save position risk factor mappings emd: " + LocalDateTime.now().toString());


        SubLister<Integer> subLister = new SubLister<>(dealIds, 2000);
        logger.debug("Update deal position generation start: " + LocalDateTime.now().toString());
        while (subLister.moreElements()) {
            dealService.updateDealPositionStatusToComplete(
                    subLister.nextList(),
                    createdDateTime);
        }
        logger.debug("Update deal position generation end: " + LocalDateTime.now().toString());

    }

}
