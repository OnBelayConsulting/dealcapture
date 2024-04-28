package com.onbelay.dealcapture.dealmodule.deal.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.adapter.PowerProfileRestAdapter;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PowerProfileSnapshotCollection;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePowerProfilePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.EvaluationContextRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PowerProfileRestAdapterBean extends BaseRestAdapterBean implements PowerProfileRestAdapter {

    @Autowired
    private PowerProfileService powerProfileService;

    @Autowired
    private GeneratePowerProfilePositionsService generatePowerProfilePositionsService;

    @Override
    public PowerProfileSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText != null) {
            if (queryText.equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("PowerProfile");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("PowerProfile", queryText);
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("PowerProfile");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "ticketNo"));
        }

        QuerySelectedPage allIds = powerProfileService.findPowerProfileIds(definedQuery);

        if (allIds.getIds().size() == 0 || start >= allIds.getIds().size()) {
            return new PowerProfileSnapshotCollection(
                    start,
                    limit,
                    allIds.getIds().size());
        }

        int toIndex = start + limit;

        if (toIndex > allIds.getIds().size())
            toIndex =  allIds.getIds().size();
        int fromIndex = start;

        List<Integer> selected = allIds.getIds().subList(fromIndex, toIndex);
        QuerySelectedPage limitedPageSelection = new QuerySelectedPage(
                selected,
                allIds.getOrderByClause());

        List<PowerProfileSnapshot> snapshots = powerProfileService.findByIds(limitedPageSelection);
        return new PowerProfileSnapshotCollection(
                start,
                limit,
                allIds.getIds().size(),
                snapshots);
    }

    @Override
    public TransactionResult save(PowerProfileSnapshot dealSnapshot) {
        initializeSession();
        return powerProfileService.save(dealSnapshot);
    }

    @Override
    public TransactionResult generatePositions(Integer powerProfileId, EvaluationContextRequest request) {
        initializeSession();

        String positionGenerationIdentifier = "PP_" + powerProfileId + System.currentTimeMillis();

        DealPositionsEvaluationContext context = new DealPositionsEvaluationContext(
                request.getCurrencyCode(),
                request.getCreatedDateTime(),
                request.getFromDate(),
                request.getToDate());

        return generatePowerProfilePositionsService.generatePowerProfilePositions(
                positionGenerationIdentifier,
                context,
                List.of(powerProfileId));
    }

    @Override
    public TransactionResult save(List<PowerProfileSnapshot> snapshots) {
        initializeSession();
        return powerProfileService.save(snapshots);
    }

    @Override
    public PowerProfileSnapshot load(EntityId dealId) {
        initializeSession();
        return powerProfileService.load(dealId);
    }
}
