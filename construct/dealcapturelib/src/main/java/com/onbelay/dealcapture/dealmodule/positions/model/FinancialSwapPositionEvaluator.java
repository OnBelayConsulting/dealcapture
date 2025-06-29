package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.FxRate;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.BuySellCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;

public class FinancialSwapPositionEvaluator extends DealPositionEvaluator implements PositionEvaluator {
    private static final Logger logger = LogManager.getLogger();

    public FinancialSwapPositionEvaluator(
            DealPositionView view,
            ValuationIndexManager valuationIndexManager) {
        super(
                view,
                valuationIndexManager);

    }
    
    private FinancialSwapPositionView getFinancialSwapPositionView() {
        return (FinancialSwapPositionView) dealPositionView;
    }
    
    public PositionValuationResult valuePosition() {
        FinancialSwapPositionValuationResult valuationResult = new FinancialSwapPositionValuationResult(
                dealPositionView.getId(),
                currentDateTime);

        Price paysPrice = calculatePaysPrice(valuationResult);

        Price receivesPrice = calculateReceivesPrice(valuationResult);
        valuationResult.getPriceDetail().setReceivesPriceValue(receivesPrice.roundPrice().getValue());

        if (paysPrice.isInError())
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MTM_DEAL_PRICE);

        Amount totalCostAmount;
        if (totalCostPositionSummary != null)
            totalCostAmount = new Amount(
                    totalCostPositionSummary.getTotalCostAmount(),
                    dealPositionView.getViewDetail().getCurrencyCode());
        else
            totalCostAmount = new Amount(
                    BigDecimal.ZERO,
                    dealPositionView.getViewDetail().getCurrencyCode());

        valuationResult.getSettlementDetail().setCostSettlementAmount(totalCostAmount.getValue());

        Price netPrice;
        if (dealPositionView.getViewDetail().getBuySellCode() == BuySellCode.BUY)
            netPrice = receivesPrice.subtract(paysPrice);
        else
            netPrice = paysPrice.subtract(receivesPrice);

        setMarkToMarket(
                netPrice,
                totalCostAmount,
                valuationResult);


        if (dealPositionView.getViewDetail().getIsSettlementPosition())
            setSettlementAmounts(
                netPrice,
                totalCostAmount,
                valuationResult);

        return valuationResult;
    }

    private Price calculatePaysPrice(FinancialSwapPositionValuationResult valuationResult) {
        Price fixedPrice = null;
        if (getFinancialSwapPositionView().getDetail().getPaysValuationCode() == ValuationCode.FIXED
                || getFinancialSwapPositionView().getDetail().getPaysValuationCode() == ValuationCode.INDEX_PLUS) {
            fixedPrice = getFixedPrice();
            valuationResult.getPriceDetail().setPaysPriceValue(fixedPrice.roundPrice().getValue());
        }

        Price paysPrice = null;
        if (getFinancialSwapPositionView().getDetail().getPaysValuationCode() == ValuationCode.INDEX
                || getFinancialSwapPositionView().getDetail().getPaysValuationCode() == ValuationCode.INDEX_PLUS) {

            if (getDealHourlyPositionView(PriceTypeCode.PAYS_PRICE) != null) {
                DealHourlyPositionView hourlyPositionView = getDealHourlyPositionView(PriceTypeCode.PAYS_PRICE);

                FxRate rate = null;
                PriceIndexSnapshot priceIndexSnapshot = valuationIndexManager.getPriceIndex(hourlyPositionView.getPriceIndexId());

                if (hourlyPositionView.getDetail().getCurrencyCode() != priceIndexSnapshot.getDetail().getCurrencyCode()) {
                    rate = hourlyPositionView.getFxRate(valuationIndexManager);
                }

                HourFixedValueDayDetail prices = calculateHourlyIndexPrices(
                        rate,
                        hourlyPositionView);

                valuationResult.addHourlyPositionResult(
                        new HourlyPositionValuationResult(
                                hourlyPositionView.getId(),
                                prices,
                                currentDateTime));


                if (getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY) != null) {
                    DealHourlyPositionView quantitiesView = getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY);

                    paysPrice = calculateWeightedAveragePrice(
                            quantitiesView.getHourFixedValueDayDetail(),
                            prices);
                } else {
                    paysPrice = calculateSimpleAveragePrice(prices);
                }

            } else {
                paysPrice  = getPaysIndexPrice();
                valuationResult.getPriceDetail().setPaysIndexPriceValue(paysPrice.roundPrice().getValue());
            }
        }

        List<PositionRiskFactorMappingSummary> summaries = dealPositionView.findMappingSummaries(PriceTypeCode.DEAL_PRICE);
        if (summaries.isEmpty() == false) {
            for (PositionRiskFactorMappingSummary summary : summaries) {
                paysPrice = paysPrice.add(summary.calculateConvertedPrice(
                        paysPrice.getCurrency(),
                        paysPrice.getUnitOfMeasure()));
            }
        }


        Price totalPaysPrice = switch (getFinancialSwapPositionView().getDetail().getPaysValuationCode()) {

            case FIXED -> fixedPrice;

            case INDEX -> paysPrice;

            case INDEX_PLUS -> paysPrice.add(fixedPrice);

            case POWER_PROFILE ->  throw new OBRuntimeException(PositionErrorCode.ERROR_INVALID_POSITION_VALUATION.getCode());
        };

        valuationResult.getPriceDetail().setTotalPaysPriceValue(totalPaysPrice.roundPrice().getValue());
        return totalPaysPrice;
    }

    private Price calculateReceivesPrice(FinancialSwapPositionValuationResult valuationResult) {

        if (getFinancialSwapPositionView().getDetail().getReceivesValuationCode() == ValuationCode.POWER_PROFILE) {
            return calculateIndexPriceWithPowerProfile(valuationResult);
        } else {
            return calculateReceivesPriceFromIndex(valuationResult);
        }

    }

    private Price calculateReceivesPriceFromIndex(FinancialSwapPositionValuationResult valuationResult) {

        Price marketPrice;
        if (getDealHourlyPositionView(PriceTypeCode.RECEIVES_PRICE) != null) {
            DealHourlyPositionView hourlyPositionView = getDealHourlyPositionView(PriceTypeCode.RECEIVES_PRICE);

            FxRate rate = null;
            PriceIndexSnapshot priceIndexSnapshot = valuationIndexManager.getPriceIndex(hourlyPositionView.getPriceIndexId());

            if (hourlyPositionView.getDetail().getCurrencyCode() != priceIndexSnapshot.getDetail().getCurrencyCode()) {
                rate = hourlyPositionView.getFxRate(valuationIndexManager);
            }

            HourFixedValueDayDetail prices = calculateHourlyIndexPrices(
                    rate,
                    hourlyPositionView);

            valuationResult.addHourlyPositionResult(
                    new HourlyPositionValuationResult(
                            hourlyPositionView.getId(),
                            prices,
                            currentDateTime));


            if (getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY) != null) {
                DealHourlyPositionView quantitiesView = getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY);

                marketPrice = calculateWeightedAveragePrice(
                        quantitiesView.getHourFixedValueDayDetail(),
                        prices);
            } else {
                marketPrice = calculateSimpleAveragePrice(prices);
            }

        } else {
            marketPrice = getFinancialSwapPositionView().getReceivesPrice(valuationIndexManager);
        }


        if (marketPrice.getCurrency() != dealPositionView.getViewDetail().getCurrencyCode()) {
            FxRate marketPriceFxRate = getFinancialSwapPositionView().getReceivesPriceFxRate(valuationIndexManager);

            if (marketPriceFxRate == null) {
                logger.error("Receives Price Fx rate is missing and is required.");
                logger.error("Receives Price Currency: " + marketPrice.getCurrency().getCode()
                        + " vs position view: " + dealPositionView.getViewDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            marketPrice = marketPrice.apply(marketPriceFxRate);
        }

        if (marketPrice.isInError())
            return marketPrice;

        if (dealPositionView.getViewDetail().getVolumeUnitOfMeasure() != marketPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    dealPositionView.getViewDetail().getVolumeUnitOfMeasure(),
                    marketPrice.getUnitOfMeasure());
            marketPrice = marketPrice.apply(conversion);
        }

        List<PositionRiskFactorMappingSummary> summaries = dealPositionView.findMappingSummaries(PriceTypeCode.MARKET_PRICE);
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


    private Price getPaysIndexPrice() {
        Price dealPrice = getFinancialSwapPositionView().getPaysPrice(valuationIndexManager);

        if (dealPrice.getCurrency() != dealPositionView.getViewDetail().getCurrencyCode()) {
            FxRate  fxRate = getFinancialSwapPositionView().getPaysPriceFxRate(valuationIndexManager);

            if (fxRate == null) {
                logger.error("DealPrice Fx rate is missing and is required.");
                logger.error("DealPrice Currency: " + dealPrice.getCurrency().getCode()
                        + " vs position view: " + dealPositionView.getViewDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            dealPrice = dealPrice.apply(fxRate);
        }

        if (dealPrice.isInError()) {
          return dealPrice;
        }

        if (dealPositionView.getViewDetail().getVolumeUnitOfMeasure() != dealPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    dealPositionView.getViewDetail().getVolumeUnitOfMeasure(),
                    dealPrice.getUnitOfMeasure());
            dealPrice = dealPrice.apply(conversion);
        }


        return dealPrice;
    }


}
