package com.onbelay.dealcapture.dealmodule.positions.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.parsing.DefinedQueryBuilder;
import com.onbelay.core.query.snapshot.DefinedOrderExpression;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.service.PowerProfileService;
import com.onbelay.dealcapture.dealmodule.positions.adapter.PowerProfilePositionRestAdapter;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePowerProfilePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.service.PowerProfilePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.service.ValuePowerProfilePositionsService;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.EvaluationContextRequest;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshotCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PowerProfilePositionRestAdapterBean extends BaseRestAdapterBean implements PowerProfilePositionRestAdapter {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private PowerProfilePositionsService powerProfilePositionsService;

    @Autowired
    private GeneratePowerProfilePositionsService generatePowerProfilePositionsService;

    @Autowired
    private ValuePowerProfilePositionsService valuePowerProfilePositionsService;

    @Autowired
    private PowerProfileService powerProfileService;

    @Override
    public TransactionResult generatePositions(EvaluationContextRequest evaluationContextRequest) {

        initializeSession();
        logger.error("Generate Positions Start: " + LocalDateTime.now().toString());

        DefinedQuery definedQuery;

        if (evaluationContextRequest.getQueryText() != null) {
            if (evaluationContextRequest.getQueryText().equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("PowerProfile");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("PowerProfile", evaluationContextRequest.getQueryText());
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("PowerProfile");
        }

        QuerySelectedPage selection = powerProfileService.findPowerProfileIds(definedQuery);

        powerProfileService.updatePositionGenerationStatusToPending(selection.getIds());

        String positionGenerationIdentifier;
        if (evaluationContextRequest.getPositionGenerationIdentifier() != null)
            positionGenerationIdentifier = evaluationContextRequest.getPositionGenerationIdentifier();
        else
            positionGenerationIdentifier = "PG_" + Thread.currentThread().getId();

        if (evaluationContextRequest.getCurrencyCode() == null)
            throw new OBRuntimeException(PositionErrorCode.MISSING_BASIS_CONTAINER.getCode());

        DealPositionsEvaluationContext dealPositionsEvaluationContext = new DealPositionsEvaluationContext(
                evaluationContextRequest.getCurrencyCode(),
                evaluationContextRequest.getCreatedDateTime(),
                evaluationContextRequest.getFromDate(),
                evaluationContextRequest.getToDate());


        if (dealPositionsEvaluationContext.validate() == false) {
            throw new OBRuntimeException(PositionErrorCode.MISSING_REQUIRED_EVAL_CONTEXT_FIELDS.getCode());
        }

        if (dealPositionsEvaluationContext.getEndPositionDate() == null)
            throw new OBRuntimeException(PositionErrorCode.MISSING_REQUIRED_EVAL_CONTEXT_FIELDS.getCode());

        TransactionResult result =  generatePowerProfilePositionsService.generatePowerProfilePositions(
                positionGenerationIdentifier,
                dealPositionsEvaluationContext,
                selection.getIds());

        logger.error("Generate Positions End: " + LocalDateTime.now().toString());
        return result;
    }

    @Override
    public TransactionResult valuePositions(EvaluationContextRequest evaluationContextRequest) {
        initializeSession();

        DefinedQuery definedQuery;
        LocalDateTime currentDateTime = LocalDateTime.now();

        logger.error("Value positions start: " + LocalDateTime.now().toString());

        if (evaluationContextRequest.getQueryText() != null) {
            if (evaluationContextRequest.getQueryText().equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("PowerProfile");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("PowerProfile", evaluationContextRequest.getQueryText());
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("PowerProfile");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "name"));
        }

        TransactionResult result = valuePowerProfilePositionsService.valuePositions(
                definedQuery,
                evaluationContextRequest.getCurrencyCode(),
                evaluationContextRequest.getCreatedDateTime(),
                LocalDateTime.now());

        logger.error("Value positions end: " + LocalDateTime.now().toString());
        return result;
    }

    @Override
    public PowerProfilePositionSnapshotCollection find(
            String queryText,
            Integer start,
            Integer limit) {

        initializeSession();

        DefinedQuery definedQuery;

        if (queryText != null) {
            if (queryText.equalsIgnoreCase("default")) {
                definedQuery = new DefinedQuery("PowerProfilePosition");
            } else {
                DefinedQueryBuilder builder = new DefinedQueryBuilder("PowerProfilePosition", queryText);
                definedQuery = builder.build();
            }
        } else {
            definedQuery = new DefinedQuery("PowerProfilePosition");
        }

        if (definedQuery.getOrderByClause().hasExpressions() == false) {
            definedQuery.getOrderByClause().addOrderExpression(
                    new DefinedOrderExpression(
                            "startDate"));
        }

        QuerySelectedPage allIds = powerProfilePositionsService.findPositionIds(definedQuery);

        if (allIds.getIds().size() == 0 || start >= allIds.getIds().size()) {
            return new PowerProfilePositionSnapshotCollection(
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

        List<PowerProfilePositionSnapshot> snapshots = powerProfilePositionsService.findByIds(limitedPageSelection);
        return new PowerProfilePositionSnapshotCollection(
                start,
                limit,
                allIds.getIds().size(),
                snapshots);
    }

    @Override
    public PowerProfilePositionSnapshot load(EntityId entityId) {
        initializeSession();
        return powerProfilePositionsService.load(entityId);
    }
}
