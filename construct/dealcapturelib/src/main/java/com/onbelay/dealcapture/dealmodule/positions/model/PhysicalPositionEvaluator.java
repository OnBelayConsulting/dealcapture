package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.busmath.model.*;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.BuySellCode;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhysicalPositionEvaluator implements PositionEvaluator {
    private static final Logger logger = LogManager.getLogger();
    private DealPositionView positionView;
    private List<DealHourlyPositionView> hourlyPositionViews = new ArrayList<>();
    private TotalCostPositionSummary totalCostPositionSummary;
    private ValuationIndexManager valuationIndexManager;

    private LocalDateTime currentDateTime;

    public static PhysicalPositionEvaluator build(LocalDateTime currentDateTime,
                                                  ValuationIndexManager valuationIndexManager,
                                                  DealPositionView positionView) {

        return new PhysicalPositionEvaluator(
                currentDateTime,
                valuationIndexManager,
                positionView);
    }

    protected PhysicalPositionEvaluator(
            LocalDateTime currentDateTime,
            ValuationIndexManager valuationIndexManager,
            DealPositionView positionView) {

        this.currentDateTime = currentDateTime;
        this.valuationIndexManager = valuationIndexManager;
        this.positionView = positionView;
    }

    public PhysicalPositionEvaluator withCosts(TotalCostPositionSummary totalCostPositionSummary) {
        this.totalCostPositionSummary = totalCostPositionSummary;
        return this;
    }

    public PhysicalPositionEvaluator withHourlyPositions(List<DealHourlyPositionView> hourlyPositionViews) {
        this.hourlyPositionViews = hourlyPositionViews;
        return this;
    }

    public PositionValuationResult valuePosition() {
        PositionValuationResult valuationResult = new PositionValuationResult(
                positionView.getId(),
                currentDateTime);

        Price dealPrice = calculateDealPrice(valuationResult);

        Price marketPrice = calculateMarketIndexPrice(valuationResult);


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
                marketPrice,
                totalCostAmount,
                valuationResult);

        if (positionView.getDetail().getIsSettlementPosition())
            setSettlementAmounts(
                dealPrice,
                totalCostAmount,
                valuationResult);

        return valuationResult;
    }

    private Price calculateDealPrice(PositionValuationResult valuationResult) {
        Price fixedPrice = null;
        if (positionView.getDetail().getDealPriceValuationCode() == ValuationCode.FIXED
                || positionView.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {
            fixedPrice = getFixedPrice();
        }

        Price dealPrice = null;
        if (positionView.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX
                || positionView.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {

            if (getDealHourlyPositionView(PriceTypeCode.DEAL_PRICE) != null) {
                DealHourlyPositionView view = getDealHourlyPositionView(PriceTypeCode.DEAL_PRICE);

                HourFixedValueDayDetail prices = getHourlyIndexPrices(view);
                valuationResult.addHourlyPositionResult(
                        new HourlyPositionValuationResult(
                                view.getId(),
                                prices,
                                currentDateTime));

                FxRate rate = null;
                if (view.getDetail().getCurrencyCode() != positionView.getDetail().getCurrencyCode()) {
                    rate = positionView.getDealPriceFxRate(valuationIndexManager);
                }

                if (getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY) != null) {
                    DealHourlyPositionView quantitiesView = getDealHourlyPositionView(PriceTypeCode.DEAL_PRICE);

                    dealPrice = calculateWeightedAveragePrice(
                            view.getDetail().getCurrencyCode(),
                            view.getDetail().getUnitOfMeasure(),
                            positionView.getDetail().getVolumeUnitOfMeasure(),
                            rate,
                            quantitiesView.getHourFixedValueDayDetail(),
                            prices);
                } else {
                    dealPrice = calculateSimpleAveragePrice(
                            view.getDetail().getCurrencyCode(),
                            rate,
                            prices);
                }

            } else {
                dealPrice  = getDealIndexPrice();
            }
        }

        List<PositionRiskFactorMappingSummary> summaries = positionView.findMappingSummaries(PriceTypeCode.DEAL_PRICE);
        if (summaries.isEmpty() == false) {
            for (PositionRiskFactorMappingSummary summary : summaries) {
                dealPrice = dealPrice.add(summary.calculateConvertedPrice(
                        dealPrice.getCurrency(),
                        dealPrice.getUnitOfMeasure()));
            }
        }


        return switch (positionView.getDetail().getDealPriceValuationCode()) {

            case FIXED -> fixedPrice;

            case INDEX -> dealPrice;

            case INDEX_PLUS -> dealPrice.add(fixedPrice);
        };

    }

    private Price calculateWeightedAveragePrice(
            CurrencyCode priceCurrencyCode,
            UnitOfMeasureCode priceUnitOfMeasureCode,
            UnitOfMeasureCode volumeUnitOfMeasureCode,
            FxRate rate,
            HourFixedValueDayDetail quantities,
            HourFixedValueDayDetail prices) {

        Conversion conversion = null;
        if (priceUnitOfMeasureCode != volumeUnitOfMeasureCode) {
            conversion = UnitOfMeasureConverter.findConversion(
                    volumeUnitOfMeasureCode,
                    priceUnitOfMeasureCode);
        }

        Quantity totalQuantity = new Quantity(
                BigDecimal.ZERO,
                volumeUnitOfMeasureCode);

        Amount totalAmount = new Amount(
                BigDecimal.ZERO,
                priceCurrencyCode);

        for (int i=1; i < 25; i++) {
            BigDecimal value = quantities.getHourFixedValue(i);
            if (value == null)
                continue;
            Quantity quantity = new Quantity(
                    value,
                    volumeUnitOfMeasureCode);

            totalQuantity = totalQuantity.add(quantity);

            Price price = new Price(
                    prices.getHourFixedValue(i),
                    priceCurrencyCode,
                    priceUnitOfMeasureCode);

            if (conversion != null)
                price = price.apply(conversion);

            Amount amount = quantity.multiply(price);
            totalAmount = totalAmount.add(amount);
        }

        // convert unit of measure

        if (totalQuantity.isZero() == false) {
            Price averagePrice = totalAmount.divide(totalQuantity);
            if (rate != null)
                averagePrice = averagePrice.apply(rate);
            return averagePrice.roundPrice();
        } else {
            return new Price(
                    BigDecimal.ZERO,
                    positionView.getDetail().getCurrencyCode(),
                    volumeUnitOfMeasureCode);
        }

    }


    private Price calculateSimpleAveragePrice(
            CurrencyCode priceCurrencyCode,
            FxRate rate,
            HourFixedValueDayDetail prices) {

        Price totalPrice = new Price(
                BigDecimal.ZERO,
                priceCurrencyCode,
                positionView.getDetail().getVolumeUnitOfMeasure());

        int count = 0;
        for (int i=1; i < 25; i++) {

            BigDecimal value = prices.getHourFixedValue(i);
            if (value == null)
                continue;
            count++;

            Price price = new Price(
                    value,
                    priceCurrencyCode,
                    positionView.getDetail().getVolumeUnitOfMeasure());
            totalPrice = totalPrice.add(price);
        }

        if (count != 0) {
            Price averagePrice = totalPrice.divide(BigDecimal.valueOf(count));
            if (rate != null) {
                averagePrice = averagePrice.apply(rate);
            }

            return averagePrice.roundPrice();
        } else {
            return new Price(
                    BigDecimal.ZERO,
                    priceCurrencyCode,
                    positionView.getDetail().getVolumeUnitOfMeasure());
        }

    }


    private void setMarkToMarket(
            Price dealPrice,
            Price marketPrice,
            Amount totalCostAmount,
            PositionValuationResult valuationResult) {

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

    private Price calculateMarketIndexPrice(PositionValuationResult valuationResult) {

        Price marketPrice;
        if (getDealHourlyPositionView(PriceTypeCode.MARKET_PRICE) != null) {
            DealHourlyPositionView view = getDealHourlyPositionView(PriceTypeCode.MARKET_PRICE);

            HourFixedValueDayDetail prices = getHourlyIndexPrices(view);
            valuationResult.addHourlyPositionResult(
                    new HourlyPositionValuationResult(
                            view.getId(),
                            prices,
                            currentDateTime));

            FxRate rate = null;
            if (view.getDetail().getCurrencyCode() != positionView.getDetail().getCurrencyCode()) {
                rate = positionView.getDealPriceFxRate(valuationIndexManager);
            }

            if (getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY) != null) {
                DealHourlyPositionView quantitiesView = getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY);

                marketPrice = calculateWeightedAveragePrice(
                        view.getDetail().getCurrencyCode(),
                        view.getDetail().getUnitOfMeasure(),
                        positionView.getDetail().getVolumeUnitOfMeasure(),
                        rate,
                        quantitiesView.getHourFixedValueDayDetail(),
                        prices);
            } else {
                marketPrice = calculateSimpleAveragePrice(
                        view.getDetail().getCurrencyCode(),
                        rate,
                        prices);
            }

        } else {
            marketPrice = positionView.getMarketPrice(valuationIndexManager);
        }


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

    private Price getDealIndexPrice() {
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


        return dealPrice;
    }



    private HourFixedValueDayDetail getHourlyIndexPrices(DealHourlyPositionView dealPriceHourlyPositionView) {

        if (dealPriceHourlyPositionView == null)
            return null;
        HourFixedValueDayDetail dealPriceHourlyDetail = new HourFixedValueDayDetail();
        for (int i = 1; i < 25;i++) {
            Price price = dealPriceHourlyPositionView.getPrice(i, valuationIndexManager);
            if (price != null)
                dealPriceHourlyDetail.setHourFixedValue(i, price.getValue());
        }

        return dealPriceHourlyDetail;
    }


    private Price getFixedPrice() {
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

    private DealHourlyPositionView getDealHourlyPositionView(PriceTypeCode priceTypeCode) {
        return  hourlyPositionViews
                .stream()
                .filter(c-> c.getDetail().getPriceTypeCode() == priceTypeCode)
                .findFirst()
                .orElse(null);
    }

}
