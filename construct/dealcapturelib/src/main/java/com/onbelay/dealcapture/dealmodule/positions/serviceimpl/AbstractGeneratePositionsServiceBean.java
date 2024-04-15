package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.components.ConcurrentRiskFactorManager;
import com.onbelay.dealcapture.riskfactor.components.FxRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import com.onbelay.shared.enums.FrequencyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractGeneratePositionsServiceBean extends BaseDomainService  {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    protected PriceIndexService priceIndexService;

    @Autowired
    protected FxIndexService fxIndexService;

    @Autowired
    protected PriceRiskFactorService priceRiskFactorService;

    @Autowired
    protected FxRiskFactorService fxRiskFactorService;

    protected RiskFactorManager createRiskFactorManager(
            List<Integer> uniquePriceIndexIds,
            EvaluationContext context) {


        List<PriceIndexSnapshot> activePriceIndices = priceIndexService.findActivePriceIndices();

        List<FxIndexSnapshot> activeFxIndices = fxIndexService.findActiveFxIndices();

        logger.info("fetch active price risk factors start: " + LocalDateTime.now().toString());
        List<PriceRiskFactorSnapshot> activePriceRiskFactors = priceRiskFactorService.findByPriceIndexIds(
                new ArrayList<>(uniquePriceIndexIds),
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

        return new ConcurrentRiskFactorManager(
                activePriceIndices,
                activeFxIndices,
                activePriceRiskFactors,
                activeFxRiskFactors);
    }

    protected void processPriceRiskFactors(
            RiskFactorManager riskFactorManager,
            LocalDate startPositionDate,
            LocalDate endPositionDate) {

        logger.info("fetch basis price risk factors start: " + LocalDateTime.now().toString());
        if (riskFactorManager.getPriceRiskFactorsSearch().keySet().size() > 0) {
            List<PriceRiskFactorSnapshot> existingSnapshots = priceRiskFactorService.findByPriceIndexIds(
                    riskFactorManager.getPriceRiskFactorsSearch().keySet().stream().toList(),
                    startPositionDate,
                    endPositionDate);

            HashMap<Integer, Map<LocalDate, PriceRiskFactorSnapshot>> existingMap = new HashMap<>();

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

        HashMap<Integer, Map<LocalDate, Map<Integer,PriceRiskFactorSnapshot>>> newHourlyPriceRiskFactors = new HashMap<>();
        HashMap<Integer, List<PriceRiskFactorSnapshot>> priceRiskFactorsToSave = new HashMap<>();


        logger.info("Save price risk factors start: " + LocalDateTime.now().toString());
        for (PriceRiskFactorHolder holder : riskFactorManager.getPriceRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                if (holder.getPriceIndex().getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
                    Map<LocalDate, Map<Integer, PriceRiskFactorSnapshot>> snapshotMap = newHourlyPriceRiskFactors.computeIfAbsent(
                            holder.getPriceIndex().getEntityId().getId(),
                            k -> new HashMap<>());

                    Map<Integer, PriceRiskFactorSnapshot> hourMap = snapshotMap.computeIfAbsent(
                            holder.getMarketDate(),
                            k -> new HashMap<>());

                    PriceRiskFactorSnapshot snapshot = hourMap.get(holder.getHourEnding());
                    if (snapshot == null) {
                        snapshot = new PriceRiskFactorSnapshot();
                        snapshot.setPriceIndexId(holder.getPriceIndex().getEntityId());
                        snapshot.getDetail().setMarketDate(holder.getMarketDate());
                        snapshot.getDetail().setHourEnding(holder.getHourEnding());
                        hourMap.putIfAbsent(holder.getHourEnding(), snapshot);
                        List<PriceRiskFactorSnapshot> saveList = priceRiskFactorsToSave.computeIfAbsent(
                                holder.getPriceIndex().getEntityId().getId(),
                                k -> new ArrayList<>());
                        saveList.add(snapshot);
                    }
                } else {
                    Map<LocalDate, PriceRiskFactorSnapshot> snapshotMap = newPriceRiskFactors.computeIfAbsent(
                            holder.getPriceIndex().getEntityId().getId(),
                            k -> new HashMap<>());

                    PriceRiskFactorSnapshot snapshot = snapshotMap.get(holder.getMarketDate());
                    if (snapshot == null) {
                        snapshot = new PriceRiskFactorSnapshot();
                        snapshot.setPriceIndexId(holder.getPriceIndex().getEntityId());
                        snapshot.getDetail().setMarketDate(holder.getMarketDate());
                        snapshotMap.putIfAbsent(holder.getMarketDate(), snapshot);
                        List<PriceRiskFactorSnapshot> saveList = priceRiskFactorsToSave.computeIfAbsent(
                                holder.getPriceIndex().getEntityId().getId(),
                                k -> new ArrayList<>());
                        saveList.add(snapshot);
                    }
                }
            }
        }

        for (Integer priceIndexId : priceRiskFactorsToSave.keySet()) {
            SubLister<PriceRiskFactorSnapshot> subLister = new SubLister<>(
                    priceRiskFactorsToSave.get(priceIndexId),
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
                PriceRiskFactorSnapshot existing;
                if (snapshot.getFrequencyCode() == FrequencyCode.HOURLY) {
                    existing = newHourlyPriceRiskFactors
                            .get(priceIndexId)
                            .get(snapshot.getDetail().getMarketDate())
                            .get(snapshot.getDetail().getHourEnding());
                } else {
                    existing = newPriceRiskFactors
                            .get(priceIndexId)
                            .get(snapshot.getDetail().getMarketDate());
                }
                existing.setEntityId(snapshot.getEntityId());
                existing.setEntityState(EntityState.UNMODIFIED);
            }
        }
        logger.info("Save price risk factors end: " + LocalDateTime.now().toString());

        logger.info("assign new price risk factors to holders start: " + LocalDateTime.now().toString());
        for (PriceRiskFactorHolder holder : riskFactorManager.getPriceRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                if (holder.getPriceIndex().getDetail().getFrequencyCode() == FrequencyCode.HOURLY) {
                    holder.setRiskFactor(
                            newHourlyPriceRiskFactors
                                    .get(holder.getPriceIndex().getEntityId().getId())
                                    .get(holder.getMarketDate())
                                    .get(holder.getHourEnding()));
                } else {
                    holder.setRiskFactor(
                            newPriceRiskFactors
                                    .get(holder.getPriceIndex().getEntityId().getId())
                                    .get(holder.getMarketDate()));
                }
            }
        }
        logger.info("assign new price risk factors to holders end: " + LocalDateTime.now().toString());

    }

    protected void processFxRiskFactors(RiskFactorManager riskFactorManager) {

        HashMap<Integer, Map<LocalDate, FxRiskFactorSnapshot>> newFxRiskFactors = new HashMap<>();

        logger.info("save new fx risk factors start: " + LocalDateTime.now().toString());
        for (FxRiskFactorHolder holder : riskFactorManager.getFxRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                Map<LocalDate, FxRiskFactorSnapshot> snapshotMap = newFxRiskFactors
                        .computeIfAbsent(
                                holder.getFxIndex().getEntityId().getId(),
                                k -> new HashMap<>());

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

    }

}
