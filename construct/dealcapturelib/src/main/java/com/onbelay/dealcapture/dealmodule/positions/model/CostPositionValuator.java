package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.BuySellCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

public class CostPositionValuator implements CostEvaluator {
    private static final Logger logger = LogManager.getLogger();
    private CostPositionView positionView;
    private ValuationIndexManager valuationIndexManager;

    public CostPositionValuator(
            ValuationIndexManager valuationIndexManager,
            CostPositionView positionView) {

        this.valuationIndexManager = valuationIndexManager;
        this.positionView = positionView;
    }

    public CostPositionValuationResult valuePosition(LocalDateTime currentDateTime) {
        CostPositionValuationResult valuationResult = new CostPositionValuationResult(
                positionView.getId(),
                currentDateTime);

        Amount costAmount = null;
        if (positionView.getDetail().getCostNameCode().getCostTypeCode() == CostTypeCode.FIXED) {
             costAmount = positionView.getCostAsAmount();
            if (positionView.getDetail().getCurrencyCode() != positionView.getDetail().getCostCurrencyCode()) {
                FxRate fxRate = positionView.getCostFxRate(valuationIndexManager);
                costAmount = costAmount.apply(fxRate);
            }
        } else {
            Price costPrice = positionView.getCostAsPrice();
            if (positionView.getDetail().getCurrencyCode() != positionView.getDetail().getCostCurrencyCode()) {
                FxRate fxRate = positionView.getCostFxRate(valuationIndexManager);
                costPrice = costPrice.apply(fxRate);
            }
            if (positionView.getDetail().getUnitOfMeasure() != positionView.getDetail().getCostUnitOfMeasure()) {
                Conversion conversion = UnitOfMeasureConverter.findConversion(
                        positionView.getDetail().getUnitOfMeasure(),
                        positionView.getDetail().getCostUnitOfMeasure());
                costPrice = costPrice.apply(conversion);
            }
            costAmount = positionView.getQuantity().multiply(costPrice);
        }
        costAmount = costAmount.round();

        if (costAmount.isInError())
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_MISSING_COST_FX_RATE_CONVERSION);
        else
            valuationResult.setCostAmount(costAmount.getValue());

        return valuationResult;
    }

}
