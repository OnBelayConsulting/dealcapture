package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.core.entity.snapshot.TransactionResult;

import java.util.List;

public interface GeneratePowerProfilePositionsService {

    TransactionResult generatePowerProfilePositions(
            String positionGenerationIdentifier,
            EvaluationContext context,
            List<Integer> powerProfileIds);

}
