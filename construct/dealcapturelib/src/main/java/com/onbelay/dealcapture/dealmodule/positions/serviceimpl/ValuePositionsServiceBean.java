package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.DealPositionsBatchUpdater;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionValuator;
import com.onbelay.dealcapture.dealmodule.positions.model.PositionValuationResult;
import com.onbelay.dealcapture.dealmodule.positions.model.ValuationIndexManager;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionService;
import com.onbelay.dealcapture.dealmodule.positions.service.ValuePositionsService;
import com.onbelay.dealcapture.pricing.service.FxIndexService;
import com.onbelay.dealcapture.pricing.service.PriceIndexService;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValuePositionsServiceBean implements ValuePositionsService {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private DealPositionRepository dealPositionRepository;

    @Autowired
    private DealPositionService dealPositionService;

    @Autowired
    private FxIndexService fxIndexService;

    @Autowired
    private PriceIndexService priceIndexService;

    @Autowired
    private DealPositionsBatchUpdater dealPositionsBatchUpdater;

    @Override
    public TransactionResult valuePositions(
            EntityId dealId,
            LocalDateTime currentDateTime) {

        List<PriceIndexSnapshot> activePriceIndices = priceIndexService.findActivePriceIndices();

        List<FxIndexSnapshot> activeFxIndices = fxIndexService.findActiveFxIndices();

        ValuationIndexManager valuationIndexManager = new ValuationIndexManager(
                activeFxIndices,
                activePriceIndices);

        List<Integer> ids = dealPositionRepository.findByDeal(dealId)
                .stream()
                .map(c-> c.getId())
                .collect(Collectors.toList());

        valuePositionsUsingView(
                ids,
                valuationIndexManager,
                currentDateTime);

        return new TransactionResult();
    }

    @Override
    public TransactionResult valuePositions(
            DefinedQuery definedQuery,
            LocalDateTime currentDateTime) {
        List<PriceIndexSnapshot> activePriceIndices = priceIndexService.findActivePriceIndices();

        List<FxIndexSnapshot> activeFxIndices = fxIndexService.findActiveFxIndices();

        ValuationIndexManager valuationIndexManager = new ValuationIndexManager(
                activeFxIndices,
                activePriceIndices);

        List<Integer> ids = dealPositionRepository.findPositionIds(definedQuery);
        valuePositionsUsingView(
                ids,
                valuationIndexManager,
                currentDateTime);

        return new TransactionResult();
    }

    private void valuePositionsUsingView(
            List<Integer> positionIds,
            ValuationIndexManager valuationIndexManager,
            LocalDateTime currentDateTime) {

        logger.info("value positions start: " + LocalDateTime.now().toString());

        List<DealPositionView> views = dealPositionService.fetchDealPositionViews(positionIds);
        ArrayList<PositionValuationResult> results = new ArrayList<>();

        for (DealPositionView view : views) {
                PhysicalPositionValuator valuator = new PhysicalPositionValuator(
                        valuationIndexManager,
                        view);

                results.add(valuator.valuePosition(currentDateTime));
        }

        SubLister<PositionValuationResult> subLister = new SubLister<>(results, 1000);
        while (subLister.moreElements()) {
            dealPositionsBatchUpdater.updatePositions(subLister.nextList());
        }
        logger.info("value positions end: " + LocalDateTime.now().toString());
    }

}
