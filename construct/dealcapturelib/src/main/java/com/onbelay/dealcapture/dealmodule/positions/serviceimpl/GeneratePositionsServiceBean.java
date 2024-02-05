package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGenerator;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGeneratorFactory;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.components.ConcurrentRiskFactorManager;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeneratePositionsServiceBean implements GeneratePositionsService {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private DealService dealService;

    @Autowired
    private PriceIndexService priceIndexService;

    @Autowired
    private FxIndexService fxIndexService;

    @Autowired
    private PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private FxRiskFactorService fxRiskFactorService;

    @Autowired
    private DealPositionService dealPositionService;

    @Override
    public TransactionResult generatePositions(
            String positionGenerationIdentifier,
            EvaluationContext context,
            Integer dealId) {

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

        logger.info("assign pg identifiers start: " + LocalDateTime.now().toString());
        dealService.assignPositionIdentifierToDeals(
                positionGenerationIdentifier,
                dealIds);
        logger.info("assign pg identifiers end: " + LocalDateTime.now().toString());

        logger.info("get assigned deal summaries start: " + LocalDateTime.now().toString());
        List<DealSummary> summaries = dealService.getAssignedDealSummaries(positionGenerationIdentifier);
        logger.info("get assigned deal summaries end: " + LocalDateTime.now().toString());

        return createAndSavePositions(
                positionGenerationIdentifier,
                context,
                summaries);

    }

    private TransactionResult createAndSavePositions(
            String positionGenerationIdentifier,
            EvaluationContext context,
            List<DealSummary> dealSummaries) {

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

        List<PriceIndexSnapshot> activePriceIndices = priceIndexService.findActivePriceIndices();

        List<FxIndexSnapshot> activeFxIndices = fxIndexService.findActiveFxIndices();

        logger.info("fetch active price risk factors start: " + LocalDateTime.now().toString());
        List<PriceRiskFactorSnapshot> activePriceRiskFactors = priceRiskFactorService.findByPriceIndexIds(
                uniquePriceIndexIds.stream().collect(Collectors.toList()),
                context.getStartPositionDate(),
                context.getEndPositionDate());
        logger.info("fetch active price risk factors end: " + LocalDateTime.now().toString());

        logger.info("fetch active FX risk factors start: " + LocalDateTime.now().toString());
        List<FxRiskFactorSnapshot> activeFxRiskFactors = fxRiskFactorService.findByFxIndexIds(
                activeFxIndices
                        .stream()
                        .map(c -> c.getEntityId().getId())
                        .collect(Collectors.toList()),
                context.getStartPositionDate(),
                context.getEndPositionDate());
        logger.info("fetch FX price risk factors end: " + LocalDateTime.now().toString());

        ConcurrentRiskFactorManager riskFactorManager = new ConcurrentRiskFactorManager(
                activePriceIndices,
                activeFxIndices,
                activePriceRiskFactors,
                activeFxRiskFactors);

        DealPositionGeneratorFactory factory = new DealPositionGeneratorFactory();

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

        logger.info("fetch basis price risk factors start: " + LocalDateTime.now().toString());
       if (riskFactorManager.getPriceRiskFactorsSearch().keySet().size() > 0) {
            List<PriceRiskFactorSnapshot> existingSnapshots = priceRiskFactorService.findByPriceIndexIds(
                    riskFactorManager.getPriceRiskFactorsSearch().keySet().stream().toList(),
                    context.getStartPositionDate(),
                    context.getEndPositionDate());

            HashMap<Integer, Map<LocalDate, PriceRiskFactorSnapshot>> existingMap = new HashMap();

            for (PriceRiskFactorSnapshot snapshot : existingSnapshots) {
                Map<LocalDate, PriceRiskFactorSnapshot> indexMap = existingMap.get(snapshot.getPriceIndexId().getId());
                if (indexMap == null) {
                    indexMap = new HashMap<>();
                    existingMap.put(snapshot.getPriceIndexId().getId(), indexMap);
                }
                indexMap.put(snapshot.getDetail().getMarketDate(), snapshot);
            }

            for (Integer priceIndexId : riskFactorManager.getPriceRiskFactorsSearch().keySet()) {
                Map<LocalDate, PriceRiskFactorSnapshot> indexMap = existingMap.get(priceIndexId);
                if (indexMap != null) {
                    for (PriceRiskFactorHolder holder : riskFactorManager.getPriceRiskFactorsSearch().get(priceIndexId)) {
                        PriceRiskFactorSnapshot existingSnapshot = indexMap.get(holder.getMarketDate());
                        if (existingSnapshot != null)
                            holder.setRiskFactor(existingSnapshot);
                    }
                }
            }

        }
        logger.info("fetch basis price risk factors end: " + LocalDateTime.now().toString());

        HashMap<Integer, Map<LocalDate, PriceRiskFactorSnapshot>> newPriceRiskFactors = new HashMap<>();

        logger.info("Save price risk factors start: " + LocalDateTime.now().toString());
        for (PriceRiskFactorHolder holder : riskFactorManager.getPriceRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                Map<LocalDate, PriceRiskFactorSnapshot> snapshotMap =  newPriceRiskFactors.get(holder.getPriceIndex().getEntityId().getId());
                if (snapshotMap == null) {
                    snapshotMap = new HashMap<>();
                    newPriceRiskFactors.put(
                            holder.getPriceIndex().getEntityId().getId(),
                            snapshotMap);
                }

                PriceRiskFactorSnapshot snapshot = snapshotMap.get(holder.getMarketDate());
                if (snapshot == null) {
                    snapshot = new PriceRiskFactorSnapshot();
                    snapshot.setPriceIndexId(holder.getPriceIndex().getEntityId());
                    snapshot.getDetail().setMarketDate(holder.getMarketDate());
                    snapshotMap.putIfAbsent(holder.getMarketDate(), snapshot);
                }
            }
        }

        for (Integer priceIndexId : newPriceRiskFactors.keySet()) {
            SubLister<PriceRiskFactorSnapshot> subLister = new SubLister<>(
                    newPriceRiskFactors.get(priceIndexId)
                            .values()
                            .stream()
                            .collect(Collectors.toList()),
                    100);

            ArrayList<Integer> ids = new ArrayList<>();

            while (subLister.moreElements()) {
                TransactionResult result = priceRiskFactorService.save(
                        new EntityId(priceIndexId),
                        subLister.nextList());

                ids.addAll(result.getIds());
            }

            List<PriceRiskFactorSnapshot> saved = priceRiskFactorService.findByIds(
                    new QuerySelectedPage(ids));

            for (PriceRiskFactorSnapshot snapshot : saved) {
                PriceRiskFactorSnapshot existing = newPriceRiskFactors
                        .get(priceIndexId)
                            .get(snapshot.getDetail().getMarketDate());
                existing.setEntityId(snapshot.getEntityId());
                existing.setEntityState(EntityState.UNMODIFIED);
            }
        }
        logger.info("Save price risk factors end: " + LocalDateTime.now().toString());

        logger.info("assign new price risk factors to holders start: " + LocalDateTime.now().toString());
        for (PriceRiskFactorHolder holder : riskFactorManager.getPriceRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                holder.setRiskFactor(
                        newPriceRiskFactors
                                .get(holder.getPriceIndex().getEntityId().getId())
                                    .get(holder.getMarketDate()));
            }
        }
        logger.info("assign new price risk factors to holders end: " + LocalDateTime.now().toString());

        /////////////////// FX Risk Factors /////////////////

        HashMap<Integer, Map<LocalDate, FxRiskFactorSnapshot>> newFxRiskFactors = new HashMap<>();

        logger.info("save new fx risk factors start: " + LocalDateTime.now().toString());
        for (FxRiskFactorHolder holder : riskFactorManager.getFxRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                Map<LocalDate, FxRiskFactorSnapshot> snapshotMap =  newFxRiskFactors
                        .get(holder.getFxIndex().getEntityId().getId());

                if (snapshotMap == null) {
                    snapshotMap = new HashMap<>();
                    newFxRiskFactors.put(
                            holder.getFxIndex().getEntityId().getId(),
                            snapshotMap);
                }

                FxRiskFactorSnapshot snapshot = snapshotMap.get(holder.getMarketDate());
                if (snapshot == null) {
                    snapshot = new FxRiskFactorSnapshot();
                    snapshot.setFxIndexId(holder.getFxIndex().getEntityId());
                    snapshot.getDetail().setMarketDate(holder.getMarketDate());
                    snapshotMap.putIfAbsent(holder.getMarketDate(), snapshot);
                }
            }
        }

        for (Integer fxIndexId : newFxRiskFactors.keySet()) {
            SubLister<FxRiskFactorSnapshot> subLister = new SubLister<>(
                    newFxRiskFactors.get(fxIndexId)
                            .values()
                            .stream()
                            .collect(Collectors.toList()),
                    1000);

            ArrayList<Integer> ids = new ArrayList<>();
            while (subLister.moreElements()) {
                TransactionResult result = fxRiskFactorService.save(
                        new EntityId(fxIndexId),
                        subLister.nextList());

                ids.addAll(result.getIds());
            }

            List<FxRiskFactorSnapshot> saved = fxRiskFactorService.findByIds(
                    new QuerySelectedPage(ids));

            for (FxRiskFactorSnapshot snapshot : saved) {
                FxRiskFactorSnapshot existing = newFxRiskFactors
                        .get(fxIndexId)
                            .get(snapshot.getDetail().getMarketDate());
                existing.setEntityId(snapshot.getEntityId());
                existing.setEntityState(EntityState.UNMODIFIED);
            }

        }
        logger.info("save new fx risk factors start: " + LocalDateTime.now().toString());

        logger.info("assign new fx risk factors to holders start: " + LocalDateTime.now().toString());
        for (FxRiskFactorHolder holder : riskFactorManager.getFxRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                holder.setRiskFactor(
                        newFxRiskFactors
                                .get(holder.getFxIndex().getEntityId().getId())
                                    .get(holder.getMarketDate()));
            }
        }
        logger.info("assign new fx risk factors to holders end: " + LocalDateTime.now().toString());

        TransactionResult overallResult = new TransactionResult();
        logger.info("save deal positions start: " + LocalDateTime.now().toString());
        for (DealPositionGenerator dealPositionGenerator : dealPositionGenerators) {
            List<DealPositionSnapshot> positions = dealPositionGenerator.generateDealPositionSnapshots();

            dealPositionService.saveDealPositions(
                    positionGenerationIdentifier,
                    dealPositionGenerator.getDealSummary().getDealId(),
                    positions);
        }
        logger.info("save deal positions end: " + LocalDateTime.now().toString());
        return overallResult;
    }

}
