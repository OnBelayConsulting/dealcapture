package com.onbelay.dealcapture.formulas.model;

import com.onbelay.dealcapture.busmath.model.CalculatedEntity;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;

import java.time.LocalDate;

public interface FormulaOperand {

    public CalculatedEntity evaluate(EvaluationContext context);

    public void collectRiskFactors(
            EvaluationContext context,
            LocalDate marketDate,
            RiskFactorManager riskFactorManager);

}
