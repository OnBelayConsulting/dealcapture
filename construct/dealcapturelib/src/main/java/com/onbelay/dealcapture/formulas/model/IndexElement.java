package com.onbelay.dealcapture.formulas.model;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.busmath.model.CalculatedEntity;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.parsing.model.IsToken;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.dealcapture.riskfactor.enums.RiskFactorErrorCode;

import java.time.LocalDate;

public class IndexElement extends BaseElement implements FormulaElement, FormulaOperand {

    private String indexName;
    private PriceRiskFactorHolder priceRiskFactorHolder;
    private FxRiskFactorHolder fxRiskFactorHolder;

    public IndexElement(boolean isInError, String errorCode) {
        super(isInError, errorCode);
    }

    public IndexElement(String indexName) {
        this.indexName = indexName;
    }

    @Override
    public IsToken createCopy() {
        IndexElement element = new IndexElement(isInError(), getErrorCode());
        element.indexName = this.indexName;
        return element;
    }

    @Override
    public CalculatedEntity evaluate(EvaluationContext context) {
        if (priceRiskFactorHolder != null) {
            Price price =  new Price(
                    priceRiskFactorHolder.getRiskFactor().getDetail().getValue(),
                    priceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                    priceRiskFactorHolder.getPriceIndex().getDetail().getUnitOfMeasureCode());
            if (price != null) {

                if (fxRiskFactorHolder != null) {
                    FxRate rate = new FxRate(
                            fxRiskFactorHolder.getRiskFactor().getDetail().getValue(),
                            fxRiskFactorHolder.getFxIndex().getDetail().getToCurrencyCode(),
                            fxRiskFactorHolder.getFxIndex().getDetail().getFromCurrencyCode());
                    return price.apply(rate);
                } else {
                    return price;
                }
            }
        }

        throw new OBRuntimeException(RiskFactorErrorCode.MISSING_RISK_FACTOR_DATE.getCode());
    }

    @Override
    public void collectRiskFactors(
            EvaluationContext context,
            LocalDate marketDate,
            RiskFactorManager riskFactorManager) {

        this.priceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                indexName,
                marketDate);

        if (priceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != context.getCurrencyCode()) {
            fxRiskFactorHolder = riskFactorManager.determineFxRiskFactor(
                    priceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                    context.getCurrencyCode(),
                    marketDate);
        }




    }
}
