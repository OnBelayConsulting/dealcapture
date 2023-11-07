package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.dealcapture.dealmodule.deal.service.DealService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.BaseDealSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGenerator;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGeneratorFactory;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.formulas.model.EvaluationContext;
import com.onbelay.dealcapture.formulas.model.FxRiskFactorHolder;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.riskfactor.components.ConcurrentRiskFactorManager;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.service.FxRiskFactorService;
import com.onbelay.dealcapture.riskfactor.service.PriceRiskFactorService;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeneratePositionsServiceBean implements GeneratePositionsService {

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
    public void generatePositions(
            EvaluationContext context,
            Integer dealId) {

        generatePositions(
                context,
                List.of(dealId));
    }

    @Override
    public void generatePositions(
            EvaluationContext context,
            List<Integer> dealIds) {

        ConcurrentRiskFactorManager riskFactorManager = new ConcurrentRiskFactorManager(
                priceIndexService.loadAll(),
                fxIndexService.loadAll(),
                priceRiskFactorService.loadAll(),
                fxRiskFactorService.loadAll());

        DealPositionGeneratorFactory factory = new DealPositionGeneratorFactory();

        List<DealPositionGenerator> dealPositionGenerators = new ArrayList<>(dealIds.size());

        for (Integer dealId : dealIds) {
            BaseDealSnapshot deal = dealService.load(new EntityId(dealId));

            DealPositionGenerator dealPositionGenerator = factory.newGenerator(
                        deal,
                        riskFactorManager);
            dealPositionGenerators.add(dealPositionGenerator);

            // create position control
            dealPositionGenerator.generatePositionHolders(context);

        }

        HashMap<Integer, Map<LocalDate, PriceRiskFactorSnapshot>> newPriceRiskFactors = new HashMap<>();

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
            TransactionResult result = priceRiskFactorService.savePriceRiskFactors(
                    new EntityId(priceIndexId),
                    newPriceRiskFactors.get(priceIndexId).values().stream().collect(Collectors.toList()));
        }

        for (PriceRiskFactorHolder holder : riskFactorManager.getPriceRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                holder.setRiskFactor(
                        priceRiskFactorService.findByMarketDate(
                            holder.getPriceIndex().getEntityId(),
                            holder.getMarketDate()));
            }
        }

        HashMap<Integer, Map<LocalDate, FxRiskFactorSnapshot>> newFxRiskFactors = new HashMap<>();
        for (FxRiskFactorHolder holder : riskFactorManager.getFxRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                Map<LocalDate, FxRiskFactorSnapshot> snapshotMap =  newFxRiskFactors.get(holder.getFxIndex().getEntityId().getId());

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
            TransactionResult result = fxRiskFactorService.saveFxRiskFactors(
                    new EntityId(fxIndexId),
                    newFxRiskFactors.get(fxIndexId).values().stream().collect(Collectors.toList()));
        }

        for (FxRiskFactorHolder holder : riskFactorManager.getFxRiskFactorHolderQueue()) {
            if (holder.hasRiskFactor() == false) {
                holder.setRiskFactor(
                        fxRiskFactorService.findByMarketDate(
                                holder.getFxIndex().getEntityId(),
                                holder.getMarketDate()));
            }
        }

        for (DealPositionGenerator dealPositionGenerator : dealPositionGenerators) {
            List<DealPositionSnapshot> positions = dealPositionGenerator.generateDealPositionSnapshots();

            dealPositionService.saveDealPositions(
                    dealPositionGenerator.getDealSnapshot().getEntityId(),
                    positions);
        }

    }

}
