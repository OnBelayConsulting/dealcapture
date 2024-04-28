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
        logger.error("Run MtM Start: " + LocalDateTime.now().toString());
        String positionGenerationIdentifier = "MTM_" + System.currentTimeMillis();
        MarkToMarketResult markToMarketResult = new MarkToMarketResult();
        markToMarketResult.setPositionGenerationIdentifier(positionGenerationIdentifier);

        EvaluationContextRequest powerProfileEvaluationContextRequest = new EvaluationContextRequest();
        powerProfileEvaluationContextRequest.setQueryText("default");
        powerProfileEvaluationContextRequest.setCurrencyCodeValue(evaluationContextRequest.getCurrencyCodeValue());
        powerProfileEvaluationContextRequest.setCreatedDateTime(evaluationContextRequest.getCreatedDateTime());
        powerProfileEvaluationContextRequest.setFromDate(evaluationContextRequest.getFromDate());
        powerProfileEvaluationContextRequest.setToDate(evaluationContextRequest.getToDate());

        powerProfileEvaluationContextRequest.setPositionGenerationIdentifier(positionGenerationIdentifier);

        powerProfilePositionRestAdapter.generatePositions(powerProfileEvaluationContextRequest);

        evaluationContextRequest.setPositionGenerationIdentifier(positionGenerationIdentifier);
        dealPositionRestAdapter.generatePositions(evaluationContextRequest);

        priceRiskFactorRestAdapter.valueRiskFactors("default");
        fxRiskFactorRestAdapter.valueRiskFactors("default");

        powerProfilePositionRestAdapter.valuePositions(powerProfileEvaluationContextRequest);

        dealPositionRestAdapter.valuePositions(evaluationContextRequest);

        logger.error("Run MtM End: " + LocalDateTime.now().toString());

        return markToMarketResult;
    }
}
