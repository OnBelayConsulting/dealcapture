package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.busmath.model.*;
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

public class PhysicalPositionEvaluator extends DealPositionEvaluator implements PositionEvaluator {
    private static final Logger logger = LogManager.getLogger();

    public PhysicalPositionEvaluator(
            DealPositionView view,
            ValuationIndexManager valuationIndexManager) {
        super(
                view,
                valuationIndexManager);

    }

    private PhysicalPositionView getPhysicalPositionView() {
        return (PhysicalPositionView) dealPositionView;
    }

    public PositionValuationResult valuePosition() {
        PhysicalPositionValuationResult valuationResult = new PhysicalPositionValuationResult(
                dealPositionView.getId(),
                currentDateTime);

        Price dealPrice = calculateDealPrice(valuationResult);

        Price marketPrice = calculateMarketPrice(valuationResult);
        valuationResult.getPriceDetail().setMarketPriceValue(marketPrice.roundPrice().getValue());

        if (dealPrice.isInError())
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

        if (dealPrice.isInError() == false && marketPrice.isInError() == false) {
            dealPrice = dealPrice.roundPrice();
            marketPrice = marketPrice.roundPrice();

            Price netPrice;
            if (dealPositionView.getViewDetail().getBuySellCode() == BuySellCode.BUY)
                netPrice = marketPrice.subtract(dealPrice);
            else
                netPrice = dealPrice.subtract(marketPrice);

            setMarkToMarket(
                    netPrice,
                    totalCostAmount,
                    valuationResult);
        }

        if (dealPositionView.getViewDetail().getIsSettlementPosition()) {

            Price settlementPrice;
            if (dealPositionView.getViewDetail().getBuySellCode() == BuySellCode.BUY) {
                settlementPrice = dealPrice.negate();
            } else {
                settlementPrice = dealPrice;
            }

            setSettlementAmounts(
                    settlementPrice,
                    totalCostAmount,
                    valuationResult);
        }


        return valuationResult;
    }

    private Price calculateDealPrice(PhysicalPositionValuationResult valuationResult) {
        Price fixedPrice = null;
        if (getPhysicalPositionView().getDetail().getDealPriceValuationCode() == ValuationCode.FIXED
                || getPhysicalPositionView().getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {
            fixedPrice = getFixedPrice();
            valuationResult.getPriceDetail().setDealPriceValue(fixedPrice.roundPrice().getValue());
        }

        Price dealPrice = null;
        if (getPhysicalPositionView().getDetail().getDealPriceValuationCode() == ValuationCode.INDEX
                || getPhysicalPositionView().getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {

            if (getDealHourlyPositionView(PriceTypeCode.DEAL_PRICE) != null) {
                DealHourlyPositionView hourlyPositionView = getDealHourlyPositionView(PriceTypeCode.DEAL_PRICE);

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

                    dealPrice = calculateWeightedAveragePrice(
                            quantitiesView.getHourFixedValueDayDetail(),
                            prices);
                } else {
                    dealPrice = calculateSimpleAveragePrice(prices);
                }

            } else {
                dealPrice  = getDealIndexPrice();
                valuationResult.getPriceDetail().setDealIndexPriceValue(dealPrice.roundPrice().getValue());
            }
        }

        List<PositionRiskFactorMappingSummary> summaries = dealPositionView.findMappingSummaries(PriceTypeCode.DEAL_PRICE);
        if (summaries.isEmpty() == false) {
            for (PositionRiskFactorMappingSummary summary : summaries) {
                dealPrice = dealPrice.add(summary.calculateConvertedPrice(
                        dealPrice.getCurrency(),
                        dealPrice.getUnitOfMeasure()));
            }
        }


        Price totalDealPrice = switch (getPhysicalPositionView().getDetail().getDealPriceValuationCode()) {

            case FIXED -> fixedPrice;

            case INDEX -> dealPrice;

            case INDEX_PLUS -> dealPrice.add(fixedPrice);

            case POWER_PROFILE ->  throw new OBRuntimeException(PositionErrorCode.ERROR_INVALID_POSITION_VALUATION.getCode());
        };

        valuationResult.getPriceDetail().setTotalDealPriceValue(totalDealPrice.roundPrice().getValue());
        return totalDealPrice;
    }

    private Price calculateMarketPrice(PhysicalPositionValuationResult valuationResult) {

        if (getPhysicalPositionView().getDetail().getMarketPriceValuationCode() == ValuationCode.POWER_PROFILE) {
            return calculateIndexPriceWithPowerProfile(valuationResult);
        } else {
            return calculateMarketPriceFromIndex(valuationResult);
        }

    }

    private Price calculateMarketPriceFromIndex(PhysicalPositionValuationResult valuationResult) {

        Price marketPrice;
        if (getDealHourlyPositionView(PriceTypeCode.MARKET_PRICE) != null) {
            DealHourlyPositionView hourlyPositionView = getDealHourlyPositionView(PriceTypeCode.MARKET_PRICE);

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
            marketPrice = getPhysicalPositionView().getMarketPrice(valuationIndexManager);
        }


        if (marketPrice.getCurrency() != dealPositionView.getViewDetail().getCurrencyCode()) {
            FxRate marketPriceFxRate = getPhysicalPositionView().getMarketPriceFxRate(valuationIndexManager);

            if (marketPriceFxRate == null) {
                logger.error("Market Price Fx rate is missing and is required.");
                logger.error("Market Price Currency: " + marketPrice.getCurrency().getCode()
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


    private Price getDealIndexPrice() {
        Price dealPrice = getPhysicalPositionView().getDealPrice(valuationIndexManager);

        if (dealPrice.getCurrency() != dealPositionView.getViewDetail().getCurrencyCode()) {
            FxRate  fxRate = getPhysicalPositionView().getDealPriceFxRate(valuationIndexManager);

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
