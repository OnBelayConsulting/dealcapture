package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.positions.batch.sql.PowerProfilePositionsBatchUpdater;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionEvaluator;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionValuationResult;
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionView;
import com.onbelay.dealcapture.dealmodule.positions.model.ValuationIndexManager;
import com.onbelay.dealcapture.dealmodule.positions.service.PowerProfilePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.service.ValuePowerProfilePositionsService;
import com.onbelay.shared.enums.CurrencyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValuePowerProfilePositionsServiceBean extends AbstractValuePositionsServiceBean  implements ValuePowerProfilePositionsService {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private PowerProfilePositionsBatchUpdater powerProfilePositionsBatchUpdater;


    @Override
    public TransactionResult valuePositions(
            EntityId powerProfileId,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime) {

        valuePositions(
                List.of(powerProfileId.getId()),
                currencyCode,
                createdDateTime,
                currentDateTime);

        return new TransactionResult();
    }

    @Override
    public TransactionResult valuePositions(
            DefinedQuery definedQuery,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime) {

        QuerySelectedPage selectedPage  = powerProfileService.findPowerProfileIds(definedQuery);
        if (selectedPage.getIds().isEmpty())
            return new TransactionResult();

        valuePositions(
                selectedPage.getIds(),
                currencyCode,
                createdDateTime,
                currentDateTime);

        return new TransactionResult();
    }

    private void valuePositions(
            List<Integer> powerProfileIds,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            LocalDateTime currentDateTime) {

        logger.debug("value positions start: " + LocalDateTime.now().toString());


        List<PowerProfilePositionView> views = powerProfilePositionsService.fetchPowerProfilePositionViews(
                powerProfileIds,
                createdDateTime);

        LocalDate startDate = views.stream().map(c -> c.getDetail().getStartDate()).min(LocalDate::compareTo).get();
        LocalDate endDate = views.stream().map(c -> c.getDetail().getStartDate()).max(LocalDate::compareTo).get();

        ValuationIndexManager valuationIndexManager = createValuationIndexManager(
                startDate,
                endDate,
                currentDateTime);



        ArrayList<PowerProfilePositionValuationResult> results = new ArrayList<>();

        for (PowerProfilePositionView view : views) {
            PowerProfilePositionEvaluator evaluator = new PowerProfilePositionEvaluator(
                    valuationIndexManager,
                    view);

            results.add(
                    evaluator.valuePosition(currentDateTime));
        }

        SubLister<PowerProfilePositionValuationResult> subLister = new SubLister<>(results, 1000);
        while (subLister.moreElements()) {
            powerProfilePositionsBatchUpdater.updatePositions(subLister.nextList());
        }
        logger.debug("value positions end: " + LocalDateTime.now().toString());
    }


}
