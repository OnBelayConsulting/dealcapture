package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.core.entity.serviceimpl.BaseDomainService;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.batch.sql.FxRiskFactorBatchInserter;
import com.onbelay.dealcapture.riskfactor.batch.sql.PriceRiskFactorBatchInserter;
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

public abstract class BasePositionsServiceBean  extends BaseDomainService {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    protected PriceIndexService priceIndexService;

    @Autowired
    protected FxIndexService fxIndexService;

    @Autowired
    protected PriceRiskFactorService priceRiskFactorService;

    @Autowired
    private PriceRiskFactorBatchInserter priceRiskFactorBatchInserter;

    @Autowired
    private FxRiskFactorBatchInserter fxRiskFactorBatchInserter;

    @Autowired
    protected FxRiskFactorService fxRiskFactorService;

    protected RiskFactorManager createRiskFactorManager(EvaluationContext context) {


        List<PriceIndexSnapshot> activePriceIndices = priceIndexService.findActivePriceIndices();

        List<FxIndexSnapshot> activeFxIndices = fxIndexService.findActiveFxIndices();

        logger.debug("fetch active price risk factors start: " + LocalDateTime.now().toString());
        List<PriceRiskFactorSnapshot> activePriceRiskFactors = priceRiskFactorService.findByPriceIndexIds(
                activePriceIndices.stream().map(c-> c.getEntityId().getId()).toList(),
                context.getStartPositionDate(),
                context.getEndPositionDate());
        logger.debug("fetch active price risk factors end: " + LocalDateTime.now().toString());

        logger.debug("fetch active FX risk factors start: " + LocalDateTime.now().toString());
        List<FxRiskFactorSnapshot> activeFxRiskFactors = fxRiskFactorService.findByFxIndexIds(
                activeFxIndices
                        .stream()
                        .map(c -> c.getEntityId().getId())
                        .collect(Collectors.toList()),
                context.getStartPositionDate(),
                context.getEndPositionDate());
        logger.debug("fetch FX price risk factors end: " + LocalDateTime.now().toString());

        return new ConcurrentRiskFactorManager(
                activePriceIndices,
                activeFxIndices,
                activePriceRiskFactors,
                activeFxRiskFactors);
    }

    protected void processPriceRiskFactors(
            RiskFactorManager riskFactorManager,
            LocalDate startPositionDate,
            LocalDate endPositionDate,
            LocalDateTime createdDateTime) {

        logger.debug("fetch basis price risk factors start: " + LocalDateTime.now().toString());
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
        logger.debug("fetch basis price risk factors end: " + LocalDateTime.now().toString());

        List<PriceRiskFactorSnapshot> priceRiskFactorsToSave = new ArrayList<>();

        HashMap<String, PriceRiskFactorSnapshot> distinctPriceRiskFactors = new HashMap<>();

        logger.debug("Save price risk factors start: " + LocalDateTime.now().toString());
        for (PriceRiskFactorHolder holder : riskFactorManager.getPriceRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                PriceRiskFactorSnapshot snapshot = distinctPriceRiskFactors.get(holder.generateUniqueKey());
                if (snapshot != null) {
                    holder.setRiskFactor(snapshot);
                } else {
                    snapshot = new PriceRiskFactorSnapshot();
                    holder.setRiskFactor(snapshot);
                    snapshot.getDetail().setDefaults();
                    snapshot.setFrequencyCode(holder.getPriceIndex().getDetail().getFrequencyCode());
                    snapshot.setPriceIndexId(holder.getPriceIndex().getEntityId());
                    snapshot.getDetail().setCreatedDateTime(createdDateTime);
                    snapshot.getDetail().setMarketDate(holder.getMarketDate());
                    snapshot.getDetail().setHourEnding(holder.getHourEnding());

                    distinctPriceRiskFactors.put(holder.generateUniqueKey(), snapshot);
                    priceRiskFactorsToSave.add(snapshot);
                }
            }
        }

        SubLister<PriceRiskFactorSnapshot> subLister = new SubLister<>(priceRiskFactorsToSave, 1000);
        while (subLister.moreElements()) {
            priceRiskFactorBatchInserter.saveRiskFactors(subLister.nextList());
        }


        for (PriceRiskFactorSnapshot snapshot : priceRiskFactorsToSave) {
            snapshot.setEntityState(EntityState.UNMODIFIED);
        }
        logger.debug("assign new price risk factors to holders start: " + LocalDateTime.now().toString());
        for (PriceRiskFactorHolder holder : riskFactorManager.getPriceRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                throw new OBRuntimeException(PositionErrorCode.MISSING_RISK_FACTOR_ID_ASSIGNMENT.name());
            }
            if (holder.getRiskFactor().getEntityId() == null)
                throw new OBRuntimeException(PositionErrorCode.MISSING_RISK_FACTOR_ID_ASSIGNMENT.name());
        }
        logger.debug("assign new price risk factors to holders end: " + LocalDateTime.now().toString());

    }

    protected void processFxRiskFactors(
            RiskFactorManager riskFactorManager,
            LocalDateTime createdDateTime) {

        HashMap<String, FxRiskFactorSnapshot> distinctFxRiskFactors = new HashMap<>();
        List<FxRiskFactorSnapshot> fxRiskFactorsToSave = new ArrayList<>();

        logger.debug("save new fx risk factors start: " + LocalDateTime.now().toString());
        for (FxRiskFactorHolder holder : riskFactorManager.getFxRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                FxRiskFactorSnapshot snapshot = distinctFxRiskFactors.get(holder.generateUniqueKey());
                if (snapshot != null) {
                    holder.setRiskFactor(snapshot);
                } else {
                    snapshot = new FxRiskFactorSnapshot();
                    holder.setRiskFactor(snapshot);
                    snapshot.getDetail().setDefaults();
                    snapshot.setFxIndexId(holder.getFxIndex().getEntityId());
                    snapshot.getDetail().setMarketDate(holder.getMarketDate());
                    snapshot.getDetail().setCreatedDateTime(createdDateTime);
                    distinctFxRiskFactors.put(holder.generateUniqueKey(), snapshot);
                    fxRiskFactorsToSave.add(snapshot);
                }
            }
            logger.debug("assign new fx risk factors to holders end: " + LocalDateTime.now().toString());

        }
        SubLister<FxRiskFactorSnapshot> subLister = new SubLister<>(fxRiskFactorsToSave, 1000);
        while (subLister.moreElements()) {
            fxRiskFactorBatchInserter.saveRiskFactors(subLister.nextList());
        }


        for (FxRiskFactorSnapshot snapshot : fxRiskFactorsToSave) {
            snapshot.setEntityState(EntityState.UNMODIFIED);
        }
        logger.debug("assign new fx risk factors to holders start: " + LocalDateTime.now().toString());
        for (FxRiskFactorHolder holder : riskFactorManager.getFxRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                throw new OBRuntimeException(PositionErrorCode.MISSING_RISK_FACTOR_ID_ASSIGNMENT.name());
            }
            if (holder.getRiskFactor().getEntityId() == null)
                throw new OBRuntimeException(PositionErrorCode.MISSING_RISK_FACTOR_ID_ASSIGNMENT.name());
        }
        logger.debug("assign new fx risk factors to holders end: " + LocalDateTime.now().toString());


    }
}
