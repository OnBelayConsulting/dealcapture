package com.onbelay.dealcapture.dealmodule.positions.service;

import com.onbelay.dealcapture.formulas.model.EvaluationContext;

import java.util.List;

public interface GeneratePositionsService {

    void generatePositions(
            EvaluationContext context,
            Integer dealId);


    void generatePositions(
            EvaluationContext context,
            List<Integer> dealIds);

}
