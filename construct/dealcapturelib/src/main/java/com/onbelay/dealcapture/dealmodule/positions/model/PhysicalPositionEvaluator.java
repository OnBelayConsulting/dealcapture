package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.busmath.model.*;
import com.onbelay.dealcapture.common.enums.CalculatedErrorType;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PositionErrorCode;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSummary;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.TotalCostPositionSummary;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;
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
    private DealPositionView dealPositionView;
    private List<DealHourlyPositionView> hourlyPositionViews = new ArrayList<>();

    private List<PowerProfilePositionView> powerProfilePositionViews = new ArrayList<>();

    private TotalCostPositionSummary totalCostPositionSummary;
    private ValuationIndexManager valuationIndexManager;

    private LocalDateTime currentDateTime;

    public static PhysicalPositionEvaluator build(
            LocalDateTime currentDateTime,
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
            DealPositionView dealPositionView) {

        this.currentDateTime = currentDateTime;
        this.valuationIndexManager = valuationIndexManager;
        this.dealPositionView = dealPositionView;
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
                dealPositionView.getId(),
                currentDateTime);

        Price dealPrice = calculateDealPrice(valuationResult);

        Price marketPrice = calculateMarketPrice(valuationResult);
        valuationResult.getSettlementDetail().setMarketPriceValue(marketPrice.roundPrice().getValue());

        if (dealPrice.isInError())
            valuationResult.addErrorMessage(PositionErrorCode.ERROR_VALUE_MTM_DEAL_PRICE);

        Amount totalCostAmount;
        if (totalCostPositionSummary != null)
            totalCostAmount = new Amount(
                    totalCostPositionSummary.getTotalCostAmount(),
                    dealPositionView.getDetail().getCurrencyCode());
        else
            totalCostAmount = new Amount(
                    BigDecimal.ZERO,
                    dealPositionView.getDetail().getCurrencyCode());

        valuationResult.getSettlementDetail().setCostSettlementAmount(totalCostAmount.getValue());

        setMarkToMarket(
                dealPrice,
                marketPrice,
                totalCostAmount,
                valuationResult);

        if (dealPositionView.getDetail().getIsSettlementPosition())
            setSettlementAmounts(
                dealPrice,
                totalCostAmount,
                valuationResult);

        return valuationResult;
    }

    private Price calculateDealPrice(PositionValuationResult valuationResult) {
        Price fixedPrice = null;
        if (dealPositionView.getDetail().getDealPriceValuationCode() == ValuationCode.FIXED
                || dealPositionView.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {
            fixedPrice = getFixedPrice();
            valuationResult.getSettlementDetail().setDealPriceValue(fixedPrice.roundPrice().getValue());
        }

        Price dealPrice = null;
        if (dealPositionView.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX
                || dealPositionView.getDetail().getDealPriceValuationCode() == ValuationCode.INDEX_PLUS) {

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
                valuationResult.getSettlementDetail().setDealIndexPriceValue(dealPrice.roundPrice().getValue());
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


        Price totalDealPrice = switch (dealPositionView.getDetail().getDealPriceValuationCode()) {

            case FIXED -> fixedPrice;

            case INDEX -> dealPrice;

            case INDEX_PLUS -> dealPrice.add(fixedPrice);

            case POWER_PROFILE ->  throw new OBRuntimeException(PositionErrorCode.ERROR_INVALID_POSITION_VALUATION.getCode());
        };

        valuationResult.getSettlementDetail().setTotalDealPriceValue(totalDealPrice.roundPrice().getValue());
        return totalDealPrice;
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
            if (dealPositionView.getDetail().getBuySellCode() == BuySellCode.BUY)
                netPrice = marketPrice.subtract(dealPrice);
            else
                netPrice = dealPrice.subtract(marketPrice);
            Amount amount = netPrice.multiply(dealPositionView.getDetail().getQuantity());

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

        Amount settlementAmount = dealPrice.multiply(dealPositionView.getDetail().getQuantity());

        if (dealPositionView.getDetail().getBuySellCode() == BuySellCode.BUY) {
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

    private Price calculateMarketPrice(PositionValuationResult valuationResult) {

        if (dealPositionView.getDetail().getMarketPriceValuationCode() == ValuationCode.POWER_PROFILE) {
            return calculateMarketIndexPriceWithPowerProfile(valuationResult);
        } else {
            return calculateMarketPriceFromIndex(valuationResult);
        }

    }

    private Price calculateMarketPriceFromIndex(PositionValuationResult valuationResult) {

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
            marketPrice = dealPositionView.getMarketPrice(valuationIndexManager);
        }


        if (marketPrice.getCurrency() != dealPositionView.getDetail().getCurrencyCode()) {
            FxRate marketPriceFxRate = dealPositionView.getMarketPriceFxRate(valuationIndexManager);

            if (marketPriceFxRate == null) {
                logger.error("Market Price Fx rate is missing and is required.");
                logger.error("Market Price Currency: " + marketPrice.getCurrency().getCode()
                        + " vs position view: " + dealPositionView.getDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            marketPrice = marketPrice.apply(marketPriceFxRate);
        }

        if (marketPrice.isInError())
            return marketPrice;

        if (dealPositionView.getDetail().getVolumeUnitOfMeasure() != marketPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    dealPositionView.getDetail().getVolumeUnitOfMeasure(),
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


    private Price calculateMarketIndexPriceWithPowerProfile(PositionValuationResult valuationResult) {

        if (powerProfilePositionViews.isEmpty())
            throw new OBRuntimeException(PositionErrorCode.MISSING_POWER_PROFILE_POSITIONS.getCode());

        Price totalPrice = new Price(
                BigDecimal.ZERO,
                dealPositionView.getDetail().getCurrencyCode(),
                dealPositionView.getDetail().getVolumeUnitOfMeasure());

        int totalPriceCount = 0;

        Amount totalAmount = new Amount(
                BigDecimal.ZERO,
                dealPositionView.getDetail().getCurrencyCode());

        Quantity totalQuantiy = new Quantity(
                BigDecimal.ZERO,
                dealPositionView.getDetail().getVolumeUnitOfMeasure());

        boolean hasVariableQuantity = false;
        DealHourlyPositionView quantitiesView = null;
        if (getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY) != null) {
            hasVariableQuantity = true;
            quantitiesView = getDealHourlyPositionView(PriceTypeCode.FIXED_QUANTITY);
        }


            // Fetch current prices for this hourly position via the associated powerProfilePosition.
        for (DealHourlyPositionView hourlyPositionView : hourlyPositionViews) {

            PowerProfilePositionView powerProfilePositionView = findPowerProfilePositionViewById(hourlyPositionView.getPowerProfilePositionId());
            PriceIndexSnapshot indexSnapshot = valuationIndexManager.getPriceIndex(powerProfilePositionView.getPriceIndexId());

            FxRate fxRate = null;
            if (indexSnapshot.getDetail().getCurrencyCode() != hourlyPositionView.getDetail().getCurrencyCode()) {
                fxRate = hourlyPositionView.getFxRate(valuationIndexManager);
            }

            Conversion conversion = null;
            if (indexSnapshot.getDetail().getUnitOfMeasureCode()  != hourlyPositionView.getDetail().getUnitOfMeasure()) {
                conversion = UnitOfMeasureConverter.findConversion(
                        hourlyPositionView.getDetail().getUnitOfMeasure(),
                        indexSnapshot.getDetail().getUnitOfMeasureCode());
            }

            for (int i=1; i <25; i++) {
                Integer priceRiskFactorId = powerProfilePositionView.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i);
                if (priceRiskFactorId != null) {
                    totalPriceCount++;

                    PriceRiskFactorSnapshot factorSnapshot = valuationIndexManager.getPriceRiskFactor(priceRiskFactorId);
                    Price unconvertedPrice = new Price(
                            factorSnapshot.getDetail().getValue(),
                            indexSnapshot.getDetail().getCurrencyCode(),
                            indexSnapshot.getDetail().getUnitOfMeasureCode());
                    if (fxRate != null)
                        unconvertedPrice = unconvertedPrice.apply(fxRate);

                    if (conversion != null)
                        unconvertedPrice = unconvertedPrice.apply(conversion);

                    if (hasVariableQuantity) {
                        BigDecimal value = quantitiesView.getHourFixedValueDayDetail().getHourFixedValue(i);
                        Quantity quantity = new Quantity(
                                value,
                                dealPositionView.getDetail().getVolumeUnitOfMeasure());
                        totalQuantiy = totalQuantiy.add(quantity);
                        Amount amount = quantity.multiply(unconvertedPrice);
                        totalAmount = totalAmount.add(amount);
                    } else {
                        totalPrice = totalPrice.add(unconvertedPrice);
                    }

                    hourlyPositionView.getHourFixedValueDayDetail().setHourFixedValue(i, unconvertedPrice.roundPrice().getValue());
                }
            }

            valuationResult.addHourlyPositionResult(
                    new HourlyPositionValuationResult(
                            hourlyPositionView.getId(),
                            hourlyPositionView.getHourFixedValueDayDetail(),
                            currentDateTime));
        }

        Price marketPrice;
        if (totalPrice.isInError())
            logger.error("market price is in error.");

        if (totalPriceCount == 0)
            logger.error("count is 0.");

        if (hasVariableQuantity) {
            marketPrice = totalAmount.divide(totalQuantiy);
        } else {
            marketPrice = totalPrice.divide(BigDecimal.valueOf(totalPriceCount));
        }
        return marketPrice.roundPrice();
    }


    private Price getDealIndexPrice() {
        Price dealPrice = dealPositionView.getDealPrice(valuationIndexManager);

        if (dealPrice.getCurrency() != dealPositionView.getDetail().getCurrencyCode()) {
            FxRate  fxRate = dealPositionView.getDealPriceFxRate(valuationIndexManager);

            if (fxRate == null) {
                logger.error("DealPrice Fx rate is missing and is required.");
                logger.error("DealPrice Currency: " + dealPrice.getCurrency().getCode()
                        + " vs position view: " + dealPositionView.getDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            dealPrice = dealPrice.apply(fxRate);
        }

        if (dealPrice.isInError()) {
          return dealPrice;
        }

        if (dealPositionView.getDetail().getVolumeUnitOfMeasure() != dealPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    dealPositionView.getDetail().getVolumeUnitOfMeasure(),
                    dealPrice.getUnitOfMeasure());
            dealPrice = dealPrice.apply(conversion);
        }


        return dealPrice;
    }



    private HourFixedValueDayDetail calculateHourlyIndexPrices(
            FxRate fxRate,
            DealHourlyPositionView hourlyPositionView) {

        CurrencyCode targetCurrencyCode = dealPositionView.getDetail().getCurrencyCode();
        UnitOfMeasureCode targetUnitOfMeasure = dealPositionView.getDetail().getVolumeUnitOfMeasure();

        if (hourlyPositionView == null)
            return null;

        HourFixedValueDayDetail dealPriceHourlyDetail = new HourFixedValueDayDetail();
        for (int i = 1; i < 25;i++) {
            Price price = hourlyPositionView.getPrice(i, valuationIndexManager);
            if (price != null) {
                if (price.getCurrency() != targetCurrencyCode) {
                    price = price.apply(fxRate);
                }

                if (price.getUnitOfMeasure() != targetUnitOfMeasure) {
                    Conversion conversion = UnitOfMeasureConverter.findConversion(
                            targetUnitOfMeasure,
                            price.getUnitOfMeasure());
                    price = price.apply(conversion);
                }

                dealPriceHourlyDetail.setHourFixedValue(i, price.roundPrice().getValue());
            }
        }

        return dealPriceHourlyDetail;
    }


    private Price getFixedPrice() {
        Price fixedPrice = dealPositionView.getFixedPrice();

        if (fixedPrice.getCurrency() != dealPositionView.getDetail().getCurrencyCode()) {
            FxRate fxRate = dealPositionView.getFixedFxRate(valuationIndexManager);

            if (fxRate == null) {
                logger.error("Fixed Price Fx rate is missing and is required.");
                logger.error("Fixed Price Currency: " + fixedPrice.getCurrency().getCode()
                        + " vs position view: " + dealPositionView.getDetail().getCurrencyCodeValue());

                return new Price(CalculatedErrorType.ERROR_INCOMPAT_CURRENCY);
            }
            fixedPrice = fixedPrice.apply(fxRate);
        }

        if (fixedPrice.isInError())
            return fixedPrice;

        if (dealPositionView.getDetail().getVolumeUnitOfMeasure() != fixedPrice.getUnitOfMeasure()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    dealPositionView.getDetail().getVolumeUnitOfMeasure(),
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

    public PowerProfilePositionView findPowerProfilePositionViewById(Integer powerProfilePositionId) {
        return powerProfilePositionViews
                .stream()
                .filter(c -> c.getId().equals(powerProfilePositionId))
                .findFirst().orElse(null);
    }

    public void withPowerProfilePositions(List<PowerProfilePositionView> viewList) {
        this.powerProfilePositionViews = viewList;
    }


    private Price calculateWeightedAveragePrice(
            HourFixedValueDayDetail quantities,
            HourFixedValueDayDetail prices) {


        Quantity totalQuantity = new Quantity(
                BigDecimal.ZERO,
                dealPositionView.getDetail().getVolumeUnitOfMeasure());

        Amount totalAmount = new Amount(
                BigDecimal.ZERO,
                dealPositionView.getDetail().getCurrencyCode());

        for (int i=1; i < 25; i++) {
            BigDecimal value = quantities.getHourFixedValue(i);
            if (value == null)
                continue;
            Quantity quantity = new Quantity(
                    value,
                    dealPositionView.getDetail().getVolumeUnitOfMeasure());

            totalQuantity = totalQuantity.add(quantity);

            Price price = new Price(
                    prices.getHourFixedValue(i),
                    dealPositionView.getDetail().getCurrencyCode(),
                    dealPositionView.getDetail().getVolumeUnitOfMeasure());

            Amount amount = quantity.multiply(price);
            totalAmount = totalAmount.add(amount);
        }

        // convert unit of measure

        if (totalQuantity.isZero() == false) {
            Price averagePrice = totalAmount.divide(totalQuantity);
            return averagePrice.roundPrice();
        } else {
            return new Price(
                    BigDecimal.ZERO,
                    dealPositionView.getDetail().getCurrencyCode(),
                    dealPositionView.getDetail().getVolumeUnitOfMeasure());
        }

    }


    private Price calculateSimpleAveragePrice(HourFixedValueDayDetail prices) {

        Price totalPrice = new Price(
                BigDecimal.ZERO,
                dealPositionView.getDetail().getCurrencyCode(),
                dealPositionView.getDetail().getVolumeUnitOfMeasure());

        int count = 0;
        for (int i=1; i < 25; i++) {

            BigDecimal value = prices.getHourFixedValue(i);
            if (value == null)
                continue;
            count++;

            Price price = new Price(
                    value,
                    dealPositionView.getDetail().getCurrencyCode(),
                    dealPositionView.getDetail().getVolumeUnitOfMeasure());
            totalPrice = totalPrice.add(price);
        }

        if (count != 0) {
            Price averagePrice = totalPrice.divide(BigDecimal.valueOf(count));
            return averagePrice.roundPrice();
        } else {
            return new Price(
                    BigDecimal.ZERO,
                    dealPositionView.getDetail().getCurrencyCode(),
                    dealPositionView.getDetail().getVolumeUnitOfMeasure());
        }

    }


}
