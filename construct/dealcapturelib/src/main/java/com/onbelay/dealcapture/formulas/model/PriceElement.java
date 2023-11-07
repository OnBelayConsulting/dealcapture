package com.onbelay.dealcapture.formulas.model;

import com.onbelay.dealcapture.busmath.model.CalculatedEntity;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.parsing.model.IsToken;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.shared.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceElement extends BaseElement implements FormulaElement, FormulaOperand {
    
    private BigDecimal value;
    private CurrencyCode currencyCode;
    private UnitOfMeasureCode unitOfMeasureCode;

    private FxRiskFactorHolder fxRiskFactorHolder;


    public PriceElement(
            BigDecimal value,
            CurrencyCode currencyCode,
            UnitOfMeasureCode unitOfMeasureCode) {

        this.value = value;
        this.currencyCode = currencyCode;
        this.unitOfMeasureCode = unitOfMeasureCode;
    }

    public PriceElement(Price price) {
        value = price.getValue();
        currencyCode = price.getCurrency();
        unitOfMeasureCode = price.getUnitOfMeasure();
    }

    public BigDecimal getValue() {
        return value;
    }

    public CurrencyCode getCurrencyType() {
        return currencyCode;
    }

    public UnitOfMeasureCode getUnitOfMeasureType() {
        return unitOfMeasureCode;
    }

    @Override
    public IsToken createCopy() {
        return new PriceElement(
                value,
                currencyCode,
                unitOfMeasureCode);
    }

    @Override
    public CalculatedEntity evaluate(EvaluationContext context) {
        return new Price(currencyCode, unitOfMeasureCode, value);
    }

    @Override
    public void collectRiskFactors(
            EvaluationContext context,
            LocalDate marketDate,
            RiskFactorManager riskFactorManager) {
            if (currencyCode != context.getCurrencyCode()) {
                this.fxRiskFactorHolder = riskFactorManager.determineFxRiskFactor(
                        currencyCode,
                        context.getCurrencyCode(),
                        marketDate);
            }
    }
}
