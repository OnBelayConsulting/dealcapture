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
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.CostPositionsBatchInserter;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.DealHourlyPositionsBatchInserter;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.DealPositionsBatchInserter;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.PositionRiskFactorMappingBatchInserter;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGenerator;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGeneratorFactory;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private PositionRiskFactorMappingBatchInserter positionRiskFactorMappingBatchInserter;

    @Autowired
    private DealService dealService;

    @Override
    public TransactionResult generatePositions(
            String positionGenerationIdentifier,
            EvaluationContext context,
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
            EvaluationContext context,
            List<Integer> dealIds) {

        if (context.getCreatedDateTime() == null)
            throw new RuntimeException("Missing createdDateTime");

        logger.info("assign pg identifiers start: " + LocalDateTime.now().toString());
        dealService.assignPositionIdentifierToDeals(
                positionGenerationIdentifier,
                dealIds);
        logger.info("assign pg identifiers end: " + LocalDateTime.now().toString());

        logger.info("get assigned deal summaries start: " + LocalDateTime.now().toString());
        List<DealSummary> summaries = dealService.getAssignedDealSummaries(positionGenerationIdentifier);
        logger.info("get assigned deal summaries end: " + LocalDateTime.now().toString());

        List<DealCostSummary> dealCostSummaries = dealService.fetchDealCostSummaries(dealIds);

        List<DealDayByMonthView> dealDayByMonthViews = dealService.fetchDealDayByMonthViewsByDates(
                dealIds,
                context.getStartPositionDate(),
                context.getEndPositionDate());


        List<DealHourByDayView> dealHourByDayViews = dealService.fetchDealHourByDayViewsByDates(
                dealIds,
                context.getStartPositionDate(),
                context.getEndPositionDate());


        return createAndSavePositions(
                context,
                summaries,
                dealCostSummaries,
                dealDayByMonthViews,
                dealHourByDayViews);

    }

    private TransactionResult createAndSavePositions(
            EvaluationContext context,
            List<DealSummary> dealSummaries,
            List<DealCostSummary> dealCostSummaries,
            List<DealDayByMonthView> dealDayByMonthViews,
            List<DealHourByDayView> dealHourByDayViews) {

        List<Integer> physicalDealIds = dealSummaries
                .stream()
                .filter(c-> c.getDealTypeCode() == DealTypeCode.PHYSICAL_DEAL)
                .map(c-> c.getDealId().getId())
                .collect(Collectors.toList());

        logger.info("get physical deal summaries start: " + LocalDateTime.now().toString());
        List<PhysicalDealSummary> physicalDealSummaries = dealService.findPhysicalDealSummariesByIds(physicalDealIds);
        logger.info("get physical deal summaries end: " + LocalDateTime.now().toString());

        HashSet<Integer> uniquePriceIndexIds = new HashSet<>();
        physicalDealSummaries.forEach( c-> {
               if (c.getDealPriceIndexId() != null)
                   uniquePriceIndexIds.add(c.getDealPriceIndexId());
               if (c.getMarketIndexId() != null)
                   uniquePriceIndexIds.add(c.getMarketIndexId());
        });

        RiskFactorManager riskFactorManager = createRiskFactorManager(
                new ArrayList<>(uniquePriceIndexIds),
                context);

        DealPositionGeneratorFactory factory = DealPositionGeneratorFactory.newFactory();
        if (dealCostSummaries.size() > 0)
            factory.withCosts(dealCostSummaries);

        if (dealDayByMonthViews.size() > 0)
            factory.withDealDayByMonthViews(dealDayByMonthViews);

        if (dealHourByDayViews.size() > 0)
            factory.withHourByDayViews(dealHourByDayViews);

        List<DealPositionGenerator> dealPositionGenerators = new ArrayList<>(dealSummaries.size());

        logger.info("generate position holders start: " + LocalDateTime.now().toString());
        for (PhysicalDealSummary summary : physicalDealSummaries) {

            DealPositionGenerator dealPositionGenerator = factory.newGenerator(
                        summary,
                        riskFactorManager);
            dealPositionGenerators.add(dealPositionGenerator);

            // create position control
            dealPositionGenerator.generatePositionHolders(context);

        }
        logger.info("generate position holders end: " + LocalDateTime.now().toString());

        processPriceRiskFactors(
                riskFactorManager,
                context.getStartPositionDate(),
                context.getEndPositionDate());

        processFxRiskFactors(riskFactorManager);

        batchSavePositions(
                context.getCreatedDateTime(),
                dealPositionGenerators);

        return  new TransactionResult();
    }


    private void batchSavePositions(
            LocalDateTime createdDateTime,
            List<DealPositionGenerator> dealPositionGenerators) {


        logger.info("save deal positions start: " + LocalDateTime.now().toString());

        ArrayList<DealPositionSnapshot> positionSnapshots = new ArrayList<>();
        ArrayList<DealHourlyPositionSnapshot> hourlyPositionSnapshots = new ArrayList<>();
        ArrayList<CostPositionSnapshot> costPositionSnapshots = new ArrayList<>();

        ArrayList<Integer> dealIds = new ArrayList<>();
        for (DealPositionGenerator dealPositionGenerator : dealPositionGenerators) {
            dealIds.add(dealPositionGenerator.getDealSummary().getDealId().getId());
            dealPositionGenerator.generateCostPositionSnapshots(createdDateTime);
            dealPositionGenerator.generateDealPositionSnapshots(createdDateTime);
            costPositionSnapshots.addAll(
                    dealPositionGenerator.getCostPositionSnapshots());
            positionSnapshots.addAll(
                    dealPositionGenerator.getDealPositionSnapshots());
            hourlyPositionSnapshots.addAll(
                    dealPositionGenerator.getDealHourlyPositionSnapshots());
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

        logger.info("save deal positions end: " + LocalDateTime.now().toString());

        logger.info("save position risk factor mappings start: " + LocalDateTime.now().toString());

        ArrayList<PositionRiskFactorMappingSnapshot> mappings = new ArrayList<>();
        for (DealPositionSnapshot snapshot : positionSnapshots) {
            if (snapshot.getRiskFactorMappingSnapshots().isEmpty() == false) {
                snapshot.setIdInMappings();
                mappings.addAll(snapshot.getRiskFactorMappingSnapshots());
            }
        }

        if (mappings.size() > 0)
            positionRiskFactorMappingBatchInserter.savePositionRiskFactorMappings(mappings);
        logger.info("save position risk factor mappings emd: " + LocalDateTime.now().toString());


        SubLister<Integer> subLister = new SubLister<>(dealIds, 2000);
        logger.info("Update deal position generation start: " + LocalDateTime.now().toString());
        while (subLister.moreElements()) {
            dealService.updateDealPositionStatusToComplete(
                    subLister.nextList(),
                    createdDateTime);
        }
        logger.info("Update deal position generation end: " + LocalDateTime.now().toString());

    }

}
