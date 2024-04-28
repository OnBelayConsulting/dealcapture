package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.entity.snapshot.TransactionResult;

import java.util.List;

public interface GeneratePositionsService {

    TransactionResult generatePositions(
            String positionGenerationIdentifier,
            DealPositionsEvaluationContext context,
            Integer dealId);


    TransactionResult generatePositions(
            String positionGenerationIdentifier,
            DealPositionsEvaluationContext context,
            List<Integer> dealIds);

}
