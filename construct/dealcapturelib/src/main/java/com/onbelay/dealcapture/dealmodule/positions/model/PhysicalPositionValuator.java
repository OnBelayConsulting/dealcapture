package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.BuySellCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PhysicalPositionValuator implements PositionValuator {
    private static final Logger logger = LogManager.getLogger();
    private DealPositionView positionView;
    private TotalCostPositionSummary totalCostPositionSummary;
    private ValuationIndexManager valuationIndexManager;

    public PhysicalPositionValuator(
            ValuationIndexManager valuationIndexManager,
            TotalCostPositionSummary totalCostPositionSummary,
            DealPositionView positionView) {

        this.totalCostPositionSummary = totalCostPositionSummary;
        this.valuationIndexManager = valuationIndexManager;
        this.positionView = positionView;
    }

    public PositionValuationResult valuePosition(LocalDateTime currentDateTime) {
        PositionValuationResult valuationResult = new PositionValuationResult(
                positionView.getId(),
                currentDateTime);

        Price dealPrice = switch (positionView.getDetail().getDealPriceValuationCode()) {

            case FIXED -> getFixedPrice();

            case INDEX -> getDealIndexPrice();

            case INDEX_PLUS -> getDealIndexPrice().add(getFixedPrice());

        };

        if (dealPrice.isInError())
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MTM_DEAL_PRICE);

        Amount totalCostAmount;
        if (totalCostPositionSummary != null)
            totalCostAmount = new Amount(
                    totalCostPositionSummary.getTotalCostAmount(),
                    positionView.getDetail().getCurrencyCode());
        else
            totalCostAmount = new Amount(
                    BigDecimal.ZERO,
                    positionView.getDetail().getCurrencyCode());

        valuationResult.getSettlementDetail().setCostSettlementAmount(totalCostAmount.getValue());

        setMarkToMarket(
                dealPrice,
                totalCostAmount,
                valuationResult);

        if (positionView.getDetail().getIsSettlementPosition())
            setSettlementAmounts(
                dealPrice,
                totalCostAmount,
                valuationResult);

        return valuationResult;
    }

    private void setMarkToMarket(
            Price dealPrice,
            Amount totalCostAmount,
            PositionValuationResult valuationResult) {

        Price marketPrice = getMarketIndexPrice();
        if (marketPrice.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MISSING_MARKET_PRICE);
        }

        if (dealPrice.isInError() == false && marketPrice.isInError() == false) {
            dealPrice = dealPrice.roundPrice();
            marketPrice = marketPrice.roundPrice();
            Price netPrice;
            if (positionView.getDetail().getBuySellCode() == BuySellCode.BUY)
                netPrice = marketPrice.subtract(dealPrice);
            else
                netPrice = dealPrice.subtract(marketPrice);
            Amount amount = netPrice.multiply(positionView.getDetail().getQuantity());

            if (amount.isInError()) {
                valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MTM_CALCULATION);
            } else {
                amount = amount.add(totalCostAmount);
                if (amount.isInError()) {
                    valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MTM_CALCULATION);
                    valuationResult.addErrorMessage(PositionErrorCode.ERROR_MISSING_COST_FX_RATE_CONVERSION);
                } else {
                    amount = amount.round();
                    valuationResult.getSettlementDetail().setMarkToMarketValuation(amount.getValue());
                }
            }

        }
    }

    private void setSettlementAmounts(
            Price dealPrice,
            Amount totalCostAmount,
            PositionValuationResult valuationResult) {

        if (dealPrice.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_SET_DEAL_PRICE);
            return;
        }

        Amount settlementAmount = dealPrice.multiply(positionView.getDetail().getQuantity());

        if (positionView.getDetail().getBuySellCode() == BuySellCode.BUY) {
            settlementAmount = settlementAmount.negate();
        }

        settlementAmount = settlementAmount.round();
        if (settlementAmount.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_INVALID_SETTLE_AMOUNT);
        } else {
            valuationResult.getSettlementDetail().setSettlementAmount(settlementAmount.getValue());
        }

        if (settlementAmount.isInError()) {
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_INVALID_TOTAL_SETTLE_MISSING_COST);
        } else {
            Amount totalSettlementAmount = settlementAmount.add(totalCostAmount);

            if (totalSettlementAmount.isInError()) {
                valuationResult.addErrorMessage(PositionErrorCode.ERROR_INVALID_TOTAL_SETTLE_MISSING_COST);
            } else {
                totalSettlementAmount = totalSettlementAmount.round();
                valuationResult.getSettlementDetail().setTotalSettlementAmount(totalSettlementAmount.getValue());
            }
        }

    }

    private Price getMarketIndexPrice() {

        Price marketPrice = positionView.getMarketPrice(valuationIndexManager);

        if (marketPrice.getCurrency() != positionView.getDetail().getCurrencyCode()) {
            FxRate marketPriceFxRate = positionView.getMarketPriceFxRate(valuationIndexManager);

            if (marketPriceFxRate == null) {
                logger.error("Market Price Fx rate is missing and is required.");
                logger.error("Market Price Currency: " + marketPrice.getCurrency().getCode()
                        + " vs position view: " + positionView.getDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            marketPrice = marketPrice.apply(marketPriceFxRate);
        }

        if (positionView.getDetail().getVolumeUnitOfMeasure() != marketPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    positionView.getDetail().getVolumeUnitOfMeasure(),
                    marketPrice.getUnitOfMeasure());
            marketPrice = marketPrice.apply(conversion);
        }

        if (marketPrice.isInError())
            return marketPrice;

        List<PositionRiskFactorMappingSummary> summaries = positionView.findMappingSummaries(PriceTypeCode.MARKET_PRICE);
        if (summaries.isEmpty() == false) {
            for (PositionRiskFactorMappingSummary summary : summaries) {
                marketPrice = marketPrice.add(
                        summary.calculateConvertedPrice(
                            marketPrice.getCurrency(),
                            marketPrice.getUnitOfMeasure()));
            }
        }
        return marketPrice;
    }

    public Price getDealIndexPrice() {
        Price dealPrice = positionView.getDealPrice(valuationIndexManager);

        if (dealPrice.getCurrency() != positionView.getDetail().getCurrencyCode()) {
            FxRate  fxRate = positionView.getDealPriceFxRate(valuationIndexManager);

            if (fxRate == null) {
                logger.error("DealPrice Fx rate is missing and is required.");
                logger.error("DealPrice Currency: " + dealPrice.getCurrency().getCode()
                        + " vs position view: " + positionView.getDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            dealPrice = dealPrice.apply(fxRate);
        }

        if (dealPrice.isInError()) {
          return dealPrice;
        }

        if (positionView.getDetail().getVolumeUnitOfMeasure() != dealPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    positionView.getDetail().getVolumeUnitOfMeasure(),
                    dealPrice.getUnitOfMeasure());
            dealPrice = dealPrice.apply(conversion);
        }

        List<PositionRiskFactorMappingSummary> summaries = positionView.findMappingSummaries(PriceTypeCode.DEAL_PRICE);
        if (summaries.isEmpty() == false) {
            for (PositionRiskFactorMappingSummary summary : summaries) {
                dealPrice = dealPrice.add(summary.calculateConvertedPrice(
                        dealPrice.getCurrency(),
                        dealPrice.getUnitOfMeasure()));
            }
        }

        return dealPrice;
    }

    public Price getFixedPrice() {
        Price fixedPrice = positionView.getFixedPrice();

        if (fixedPrice.getCurrency() != positionView.getDetail().getCurrencyCode()) {
            FxRate fxRate = positionView.getFixedFxRate(valuationIndexManager);

            if (fxRate == null) {
                logger.error("Fixed Price Fx rate is missing and is required.");
                logger.error("Fixed Price Currency: " + fixedPrice.getCurrency().getCode()
                        + " vs position view: " + positionView.getDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            fixedPrice = fixedPrice.apply(fxRate);
        }

        if (fixedPrice.isInError())
            return fixedPrice;

        if (positionView.getDetail().getVolumeUnitOfMeasure() != fixedPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    positionView.getDetail().getVolumeUnitOfMeasure(),
                    fixedPrice.getUnitOfMeasure());
            fixedPrice = fixedPrice.apply(conversion);
        }
        return fixedPrice;
    }

}
