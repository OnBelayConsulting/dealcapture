package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.PowerProfilePositionsBatchInserter;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionGenerator;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePowerProfilePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeneratePowerProfilePositionsServiceBean extends BasePositionsServiceBean implements GeneratePowerProfilePositionsService {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private PowerProfilePositionsBatchInserter powerProfilePositionsBatchInserter;

    @Override
    public TransactionResult generatePowerProfilePositions(
            String positionGenerationIdentifier,
            EvaluationContext context,
            List<Integer> powerProfileIds) {

        List<PowerProfileSnapshot> profiles = powerProfileService.findByIds(new QuerySelectedPage(powerProfileIds));

        List<PriceIndexSnapshot> activePriceIndices = priceIndexService.findActivePriceIndices();
        List<Integer> uniquePriceIndexIds = activePriceIndices.stream().map( c-> c.getEntityId().getId()).toList();

        RiskFactorManager riskFactorManager = createRiskFactorManager(
                uniquePriceIndexIds,
                context);

        createAndSavePowerProfilePositions(
                context,
                profiles,
                riskFactorManager);

        return new TransactionResult();
    }

    private void createAndSavePowerProfilePositions(
            EvaluationContext context,
            List<PowerProfileSnapshot> profiles,
            RiskFactorManager riskFactorManager) {

        List<PowerProfilePositionGenerator> generators = new ArrayList<>();

        for (PowerProfileSnapshot profile : profiles) {
            PowerProfilePositionGenerator generator = new PowerProfilePositionGenerator(
                    profile,
                    riskFactorManager);
            generators.add(generator);

            generator.generatePositionHolders(context);
        }

        processPriceRiskFactors(
                riskFactorManager,
                context.getStartPositionDate(),
                context.getEndPositionDate());

        processFxRiskFactors(riskFactorManager);

        ArrayList<PowerProfilePositionSnapshot> snapshots = new ArrayList<>();
        for (PowerProfilePositionGenerator generator : generators) {
            snapshots.addAll(generator.generatePowerProfilePositionSnapshots());
        }

        SubLister<PowerProfilePositionSnapshot> subLister = new SubLister<>(snapshots, 1000);
        while (subLister.moreElements()) {
            powerProfilePositionsBatchInserter.savePositions(subLister.nextList());
        }

    }
}
