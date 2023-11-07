package com.onbelay.dealcapture.dealmodule.positions.subscribe.subscriber;

import com.onbelay.dealcapture.dealmodule.deal.publish.snapshot.GeneratePositionsRequest;
import com.onbelay.dealcapture.dealmodule.positions.service.GeneratePositionsService;
import com.onbelay.dealcapture.formulas.model.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GeneratePositionsRequestProcessorBean implements GeneratePositionsRequestProcessor {

    @Autowired
    private GeneratePositionsService generatePositionsService;

    @Override
    public void processRequest(GeneratePositionsRequest request) {

        EvaluationContext context = EvaluationContext
                .build()
                .withObservedDateTime(request.getObservedDateTime())
                .withCurrency(request.getCurrencyCode());

        generatePositionsService.generatePositions(
                context,
                request.getDealIds());
    }

}
