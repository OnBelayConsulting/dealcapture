package com.onbelay.dealcapture.dealmodule.deal.adapterimpl;

import com.onbelay.core.controller.BaseRestAdapterBean;
import com.onbelay.dealcapture.dealmodule.deal.adapter.MarkToMarketRestAdapter;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.MarkToMarketResult;
import com.onbelay.dealcapture.dealmodule.positions.adapter.DealPositionRestAdapter;
import com.onbelay.dealcapture.dealmodule.positions.adapter.PowerProfilePositionRestAdapter;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.EvaluationContextRequest;
import com.onbelay.dealcapture.riskfactor.adapter.FxRiskFactorRestAdapter;
import com.onbelay.dealcapture.riskfactor.adapter.PriceRiskFactorRestAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MarkToMarketAdapterBean extends BaseRestAdapterBean implements MarkToMarketRestAdapter {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private DealPositionRestAdapter dealPositionRestAdapter;

    @Autowired
    private PowerProfilePositionRestAdapter powerProfilePositionRestAdapter;

    @Autowired
    private FxRiskFactorRestAdapter fxRiskFactorRestAdapter;

    @Autowired
    private PriceRiskFactorRestAdapter priceRiskFactorRestAdapter;

    @Override
    public MarkToMarketResult runMarkToMarket(EvaluationContextRequest evaluationContextRequest) {
        initializeSession();
        LocalDateTime startDateTime = LocalDateTime.now();
        logger.info("Run MtM Start: " + startDateTime);

        final String positionGenerationIdentifier;
        if (evaluationContextRequest.getPositionGenerationIdentifier() != null)
            positionGenerationIdentifier = evaluationContextRequest.getPositionGenerationIdentifier();
        else
            positionGenerationIdentifier = "MTM_" + System.currentTimeMillis();

        MarkToMarketResult markToMarketResult = new MarkToMarketResult(startDateTime, positionGenerationIdentifier);

        EvaluationContextRequest powerProfileEvaluationContextRequest = new EvaluationContextRequest();
        powerProfileEvaluationContextRequest.setQueryText("default");
        powerProfileEvaluationContextRequest.setCurrencyCodeValue(evaluationContextRequest.getCurrencyCodeValue());
        powerProfileEvaluationContextRequest.setCreatedDateTime(evaluationContextRequest.getCreatedDateTime());
        powerProfileEvaluationContextRequest.setFromDate(evaluationContextRequest.getFromDate());
        powerProfileEvaluationContextRequest.setToDate(evaluationContextRequest.getToDate());

        powerProfileEvaluationContextRequest.setPositionGenerationIdentifier(positionGenerationIdentifier);

        logger.debug("Generate PowerProfile Positions: " + LocalDateTime.now().toString());
        powerProfilePositionRestAdapter.generatePositions(powerProfileEvaluationContextRequest);


        evaluationContextRequest.setPositionGenerationIdentifier(positionGenerationIdentifier);
        logger.debug("Generate Deal Positions: " + LocalDateTime.now().toString());
        dealPositionRestAdapter.generatePositions(evaluationContextRequest);

        logger.debug("Value Risk Factors: " + LocalDateTime.now().toString());
        priceRiskFactorRestAdapter.valueRiskFactors("default");
        fxRiskFactorRestAdapter.valueRiskFactors("default");

        logger.debug("Value Power Profile positions: " + LocalDateTime.now().toString());
        powerProfilePositionRestAdapter.valuePositions(powerProfileEvaluationContextRequest);

        logger.debug("Value deal positions: " + LocalDateTime.now().toString());
        dealPositionRestAdapter.valuePositions(evaluationContextRequest);

        LocalDateTime endDateTime = LocalDateTime.now();
        logger.info("Run MtM End: " + endDateTime);

        markToMarketResult.computeElapsedTime(endDateTime);

        return markToMarketResult;
    }
}
