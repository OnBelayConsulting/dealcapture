package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.PowerFlowCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.positions.enums.PriceTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.HourFixedValueDayDetail;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;
import com.onbelay.dealcapture.riskfactor.components.PriceIndexPositionDateContainer;
import com.onbelay.dealcapture.riskfactor.components.PriceRiskFactorHolder;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDealPositionGenerator implements DealPositionGenerator {
    private static final Logger logger = LogManager.getLogger();

    protected DealSummary dealSummary;

    protected DealPositionsEvaluationContext context;

    protected RiskFactorManager riskFactorManager;

    protected List<DealCostSummary> costSummaries = new ArrayList<>();

    protected Map<LocalDate, List<PowerProfilePositionView>> powerProfilePositionMap = new HashMap<>();

    protected DealDaysContainer dealDaysContainer;

    protected List<CostPositionHolder> costPositionHolders = new ArrayList<>();

    protected List<BasePositionHolder> positionHolders = new ArrayList<>();

    public BaseDealPositionGenerator(
            DealSummary dealSummary,
            RiskFactorManager riskFactorManager) {
        
        this.dealSummary = dealSummary;
        this.riskFactorManager = riskFactorManager;
    }

    public void setEvaluationContext(DealPositionsEvaluationContext context) {
        this.context = modifyEvaluationContextForDeal(context);
    }

    protected List<PowerFlowCode> findUniquePowerCodesByDate(LocalDate currentDate) {
        List<PowerProfilePositionView> viewsByDate = powerProfilePositionMap.get(currentDate);
        if (viewsByDate == null)
            return null;

        return viewsByDate.stream().map( c-> c.getDetail().getPowerFlowCode()).distinct().toList();
    }

    protected List<PowerProfilePositionView> getPowerProfilePositionViewsBy(
            LocalDate currentDate,
            PowerFlowCode powerFlowCode) {
        List<PowerProfilePositionView> viewsByDate = powerProfilePositionMap.get(currentDate);
        if (viewsByDate == null)
            return null;

        return viewsByDate.stream().filter( c-> c.getDetail().getPowerFlowCode() == powerFlowCode).toList();
    }

    protected DealPositionsEvaluationContext modifyEvaluationContextForDeal(DealPositionsEvaluationContext context) {
        final LocalDate startDate;
        final LocalDate endDate;

        if (context.getStartPositionDate() != null) {
            if (dealSummary.getDealDetail().getStartDate().isAfter(context.getStartPositionDate()))
                startDate = dealSummary.getDealDetail().getStartDate();
            else
                startDate = context.getStartPositionDate();
        } else {
            startDate = dealSummary.getDealDetail().getStartDate();
        }

        if (context.getEndPositionDate() != null) {
            if (dealSummary.getDealDetail().getEndDate().isBefore(context.getEndPositionDate()))
                endDate = dealSummary.getDealDetail().getEndDate();
            else
                endDate = context.getEndPositionDate();
        } else {
            endDate = dealSummary.getDealDetail().getEndDate();
        }

        CurrencyCode targetCurrencyCode;
        if (context.getCurrencyCode() != null)
            targetCurrencyCode = context.getCurrencyCode();
        else
            targetCurrencyCode = dealSummary.getDealDetail().getReportingCurrencyCode();

        UnitOfMeasureCode targetUnitOfMeasureCode;
        if (context.getUnitOfMeasureCode() != null)
            targetUnitOfMeasureCode = context.getUnitOfMeasureCode();
        else
            targetUnitOfMeasureCode = dealSummary.getDealDetail().getVolumeUnitOfMeasureCode();

        return new DealPositionsEvaluationContext(
                    targetCurrencyCode,
                    context.getCreatedDateTime(),
                    startDate,
                    endDate)
                .withUnitOfMeasureCode(targetUnitOfMeasureCode);
    }

    protected void setBasePositionHolderAttributes(
            BasePositionHolder basePositionHolder,
            LocalDate currentDate) {

        basePositionHolder.getDetail().setCreatedDateTime(context.getCreatedDateTime());

        basePositionHolder.getDetail().setFrequencyCode(FrequencyCode.DAILY);

        basePositionHolder.getDetail().setStartDate(currentDate);
        basePositionHolder.getDetail().setEndDate(currentDate);

        basePositionHolder.getDetail().setVolumeUnitOfMeasure(context.getUnitOfMeasureCode());
        basePositionHolder.getDetail().setCurrencyCode(context.getCurrencyCode());



        if (context.getCurrencyCode() == dealSummary.getDealDetail().getSettlementCurrencyCode()) {
            basePositionHolder.getSettlementDetail().setIsSettlementPosition(true);
            basePositionHolder.getSettlementDetail().setSettlementCurrencyCode(context.getCurrencyCode());
        } else {
            basePositionHolder.getSettlementDetail().setIsSettlementPosition(false);
        }


    }


    protected void determinePositionQuantity(BasePositionHolder positionHolder) {

        LocalDate currentDate = positionHolder.getDetail().getStartDate();

        Quantity defaultDailyQuantity;
        Quantity defaultHourlyQuantity;

        if (dealSummary.getDealDetail().getVolumeFrequencyCode() == FrequencyCode.HOURLY) {
            defaultHourlyQuantity = new Quantity(
                    dealSummary.getDealDetail().getVolumeQuantity(),
                    dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());
            defaultDailyQuantity = defaultHourlyQuantity.multiply(24);
        } else {
            defaultDailyQuantity = new Quantity(
                    dealSummary.getDealDetail().getVolumeQuantity(),
                    dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());
            defaultHourlyQuantity = defaultDailyQuantity.divide(24);
        }

        Quantity dailyQuantity = null;
        if (hasDealDayByMonthQuantities(currentDate))
            dailyQuantity = new Quantity(
                    getDayQuantity(currentDate),
                    dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());

        if (dailyQuantity == null)
            dailyQuantity = defaultDailyQuantity;


        Quantity totalQuantity = new Quantity(
                BigDecimal.ZERO,
                context.getUnitOfMeasureCode());

        Conversion conversion = null;
        if (context.getUnitOfMeasureCode() != dealSummary.getDealDetail().getVolumeUnitOfMeasureCode()) {
            conversion = UnitOfMeasureConverter.findConversion(
                    context.getUnitOfMeasureCode(),
                    dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());
            defaultDailyQuantity = defaultDailyQuantity.apply(conversion);
            defaultHourlyQuantity = defaultHourlyQuantity.apply(conversion);
        }


       boolean calculateDailyQuantity = false;
        if (hasDealHourByDayQuantities(currentDate)) {
            calculateDailyQuantity = true;
            for (int i=1; i < 25; i++) {
                BigDecimal quantityValue = getHourQuantity(currentDate, i);

                Quantity hourQuantity;
                if (quantityValue != null) {
                    hourQuantity = new Quantity(
                            quantityValue,
                            dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());
                    if (conversion != null)
                        hourQuantity = hourQuantity.apply(conversion);

                } else {
                    hourQuantity = defaultHourlyQuantity;
                }
                positionHolder.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
                positionHolder.getDealHourByDayQuantity().setHourValue(
                        i,
                        hourQuantity.getValue());

                totalQuantity = totalQuantity.add(hourQuantity);
            }
        }


        Quantity calculatedDailyQuantity;
        if (calculateDailyQuantity)
            calculatedDailyQuantity = totalQuantity;
        else
            calculatedDailyQuantity = dailyQuantity;


        positionHolder.getDetail().setVolumeQuantityValue(calculatedDailyQuantity.round().getValue());
    }


    protected void determinePositionQuantityFromPowerProfile(BasePositionHolder positionHolder) {

        LocalDate currentDate = positionHolder.getDetail().getStartDate();

        Quantity defaultDailyQuantity;
        Quantity defaultHourlyQuantity;

        if (dealSummary.getDealDetail().getVolumeFrequencyCode() == FrequencyCode.HOURLY) {
            defaultHourlyQuantity = new Quantity(
                    dealSummary.getDealDetail().getVolumeQuantity(),
                    dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());
            defaultDailyQuantity = defaultHourlyQuantity.multiply(24);
        } else {
            defaultDailyQuantity = new Quantity(
                    dealSummary.getDealDetail().getVolumeQuantity(),
                    dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());
            defaultHourlyQuantity = defaultDailyQuantity.divide(24);
        }

        Quantity dailyQuantity = null;
        if (hasDealDayByMonthQuantities(currentDate))
            dailyQuantity = new Quantity(
                    getDayQuantity(currentDate),
                    dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());

        if (dailyQuantity == null)
            dailyQuantity = defaultDailyQuantity;


        Quantity totalQuantity = new Quantity(
                BigDecimal.ZERO,
                dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());

        boolean calculateDailyQuantity = false;
        HourFixedValueDayDetail hourSlots = positionHolder.getHourSlotsForPowerProfile();

        if (hasDealHourByDayQuantities(currentDate)) {
            calculateDailyQuantity = true;
            for (int i=1; i < 25; i++) {
                if (hourSlots.getHourFixedValue(i) != null) {
                    BigDecimal quantityValue = getHourQuantity(currentDate, i);
                    if (quantityValue != null) {

                        positionHolder.getDealHourByDayQuantity().setHourValue(
                                i,
                                quantityValue);
                    } else {
                        positionHolder.getDealHourByDayQuantity().setHourValue(
                                i,
                                defaultHourlyQuantity.getValue());
                    }
                    Quantity hourlyQuantity = new Quantity(
                            positionHolder.getDealHourByDayQuantity().getHourValue(i),
                            dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());

                    totalQuantity = totalQuantity.add(hourlyQuantity);
                }
            }
        }


        Quantity unconvertedDailyQuantity;
        if (calculateDailyQuantity)
            unconvertedDailyQuantity = totalQuantity;
        else
            unconvertedDailyQuantity = dailyQuantity;

        if (context.getUnitOfMeasureCode() != dealSummary.getDealDetail().getVolumeUnitOfMeasureCode()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    context.getUnitOfMeasureCode(),
                    dealSummary.getDealDetail().getVolumeUnitOfMeasureCode());

            unconvertedDailyQuantity = unconvertedDailyQuantity.apply(conversion);
        }

        positionHolder.getDetail().setVolumeQuantityValue(unconvertedDailyQuantity.round().getValue());
    }



    protected void generateDealHourlyPositionHoldersFromPowerProfile(
            BasePositionHolder basePositionHolder,
            List<PowerProfilePositionView> views,
            PriceTypeCode priceTypeCode) {

        for (PowerProfilePositionView view : views) {
            PriceIndexSnapshot priceIndexSnapshot = this.riskFactorManager.findPriceIndex(view.getPriceIndexId());

            DealHourlyPositionHolder holder = new DealHourlyPositionHolder();
            holder.setPowerProfilePositionId(new EntityId(view.getId()));
            holder.setDealId(new EntityId(dealSummary.getId()));
            holder.getDetail().setPriceTypeCode(priceTypeCode);
            holder.setPriceIndexId(new EntityId(view.getPriceIndexId()));
            holder.getDetail().setBasisNo(view.getDetail().getBasisNo());
            holder.getDetail().setIndexTypeCodeValue(view.getDetail().getIndexTypeCodeValue());

            holder.getDetail().setCreatedDateTime(context.getCreatedDateTime());
            holder.getDetail().setCurrencyCode(context.getCurrencyCode());
            holder.getDetail().setPowerFlowCode(view.getDetail().getPowerFlowCode());
            holder.getDetail().setStartDate(basePositionHolder.getDetail().getStartDate());
            holder.getDetail().setEndDate(basePositionHolder.getDetail().getEndDate());
            holder.getDetail().setIsSettlementPosition(basePositionHolder.getSettlementDetail().getIsSettlementPosition());
            holder.getDetail().setUnitOfMeasure(context.getUnitOfMeasureCode());

            if (context.getCurrencyCode() != priceIndexSnapshot.getDetail().getCurrencyCode()) {
                holder.setFxRiskFactorHolder(
                        riskFactorManager.determineFxRiskFactor(
                                priceIndexSnapshot.getDetail().getCurrencyCode(),
                                context.getCurrencyCode(),
                                holder.getDetail().getStartDate()));
            }
            basePositionHolder.getHourlyPositionHolders().add(holder);
        }
    }


    public boolean hasDealDayByMonthCosts(LocalDate positionDate) {
        if (dealDaysContainer == null)
            return false;
        return (dealDaysContainer.hasDayByMonthCosts(positionDate));
    }

    public boolean hasDealDayByMonthQuantities(LocalDate positionDate) {
        if (dealDaysContainer == null)
            return false;
        return (dealDaysContainer.hasDayByMonthQuantity(positionDate));
    }


    public boolean hasDealDayByMonthPrices(LocalDate positionDate) {
        if (dealDaysContainer == null)
            return false;
        return (dealDaysContainer.hasDayByMonthPrice(positionDate));
    }

    public boolean hasDealHourByDayPrices(LocalDate positionDate) {
        if (dealDaysContainer == null)
            return false;
       return (dealDaysContainer.hasHourByDayPrice(positionDate));
    }

    public boolean hasDealHourByDayQuantities(LocalDate positionDate) {
        if (dealDaysContainer == null)
            return false;
        return (dealDaysContainer.hasHourByDayQuantity(positionDate));
    }

    protected BigDecimal getHourPrice(LocalDate positionDate, int hourEnding) {
        if (hasDealHourByDayPrices(positionDate)) {
            return dealDaysContainer.getHourPrice(positionDate, hourEnding);
        } else {
            return null;
        }
    }

    protected BigDecimal getHourQuantity(LocalDate positionDate, int hourEnding) {
        if (hasDealHourByDayQuantities(positionDate)) {
            return dealDaysContainer.getHourQuantity(positionDate, hourEnding);
        } else {
            return null;
        }
    }

    protected BigDecimal getDayQuantity(LocalDate positionDate) {
        if (hasDealDayByMonthQuantities(positionDate)) {
            return dealDaysContainer.getDayQuantity(positionDate);
        } else {
            return null;
        }
    }


    protected BigDecimal getDayPrice(LocalDate positionDate) {
        if (hasDealDayByMonthPrices(positionDate)) {
            return dealDaysContainer.getDayPrice(positionDate);
        } else {
            return null;
        }
    }

    protected void generateCostPositionHolders(BasePositionHolder basePositionHolder) {

        for (DealCostSummary summary : costSummaries) {
            CostPositionHolder holder = new CostPositionHolder();
            holder.setDealCostSummary(summary);
            holder.getSnapshot().getDetail().setStartDate(basePositionHolder.getDetail().getStartDate());
            holder.getSnapshot().getDetail().setEndDate(basePositionHolder.getDetail().getEndDate());
            holder.getSnapshot().getDetail().setFrequencyCode(FrequencyCode.DAILY);
            holder.getSnapshot().getDetail().setCreatedDateTime(context.getCreatedDateTime());

            holder.getSnapshot().getDetail().setVolumeQuantityValue(basePositionHolder.getDetail().getVolumeQuantityValue());

            holder.getSnapshot().getDetail().setCurrencyCodeValue(context.getCurrencyCode().getCode());
            holder.getSnapshot().getDetail().setUnitOfMeasureValue(context.getUnitOfMeasureCode().getCode());

            LocalDate currentDate = basePositionHolder.getDetail().getStartDate();

            BigDecimal cost = null;
            if (hasDealDayByMonthCosts(currentDate))
                cost = dealDaysContainer.getDayCost(currentDate, summary.getCostNameCode().getCode());

            if (cost != null)
                holder.getSnapshot().getDetail().setCostValue(cost);
            else
                holder.getSnapshot().getDetail().setCostValue(summary.getCostValue());

            holder.getSnapshot().getDetail().setCostNameCodeValue(summary.getCostNameCode().getCode());
            holder.getSnapshot().getDetail().setIsSettlementPosition(basePositionHolder.getSettlementDetail().getIsSettlementPosition());

            if (holder.getDealCostSummary().getCurrencyCode() != context.getCurrencyCode()) {
                holder.getSnapshot().getDetail().setIsFixedValued(false);
                holder.setCostFxHolder(riskFactorManager.determineFxRiskFactor(
                        holder.getDealCostSummary().getCurrencyCode(),
                        context.getCurrencyCode(),
                       currentDate));
            } else {
                holder.getSnapshot().getDetail().setIsFixedValued(true);
            }

            costPositionHolders.add(holder);
        }

    }


    public List<CostPositionSnapshot> generateCostPositionSnapshots() {

        List<CostPositionSnapshot> costPositionSnapshots = new ArrayList<>();

        for (CostPositionHolder holder : costPositionHolders) {
            CostPositionSnapshot costPositionSnapshot = holder.getSnapshot();
            costPositionSnapshot.getDetail().setCreatedDateTime(context.getCreatedDateTime());

            costPositionSnapshot.setDealId(new EntityId(dealSummary.getId()));
            costPositionSnapshot.setDealCostId(new EntityId(holder.getDealCostSummary().getId()));

            costPositionSnapshot.getDetail().setErrorCode("0");

            if (holder.getCostFxHolder() != null) {
                costPositionSnapshot.setCostFxRiskFactorId(holder.getCostFxHolder().getRiskFactor().getEntityId());
            }

            if (holder.getSnapshot().getDetail().getIsFixedValued()) {
                holder.getSnapshot().getDetail().setValuedDateTime(context.getCreatedDateTime());

                if (costPositionSnapshot.getDetail().getCostNameCode().getCostTypeCode() == CostTypeCode.FIXED) {
                    costPositionSnapshot.getDetail().setCostAmount(
                            costPositionSnapshot.getDetail().getCostValue());
                } else {
                    Price cost = costPositionSnapshot.fetchCostPrice(holder.getDealCostSummary().getUnitOfMeasureCode());
                    if (cost.getUnitOfMeasure() != costPositionSnapshot.getDetail().getUnitOfMeasure()) {
                        Conversion conversion = UnitOfMeasureConverter.findConversion(
                                costPositionSnapshot.getDetail().getUnitOfMeasure(),
                                cost.getUnitOfMeasure());
                        cost = cost.apply(conversion);
                    }
                    Quantity quantity = costPositionSnapshot.getQuantity();
                    Amount amount = quantity.multiply(cost);
                    amount = amount.round();
                    costPositionSnapshot.getDetail().setCostAmount(amount.getValue());
                }
            }

            costPositionSnapshots.add(costPositionSnapshot);
        }
        return costPositionSnapshots;
    }

    protected HourFixedValueDayDetail calculateHourlyQuantitySlots(PowerProfilePositionView powerProfilePositionView ) {
        HourFixedValueDayDetail detail = new HourFixedValueDayDetail();
        for (int i = 1; i < 25; i++) {
            if (powerProfilePositionView.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i) != null)
                detail.setHourFixedValue(i, BigDecimal.ONE);
        }
        return detail;
    }

    protected HourFixedValueDayDetail calculate24HourSlots() {
        HourFixedValueDayDetail detail = new HourFixedValueDayDetail();
        for (int i = 1; i < 25; i++) {
            detail.setHourFixedValue(i, BigDecimal.ONE);
        }
        return detail;
    }

    @Override
    public DealSummary getDealSummary() {
        return dealSummary;
    }

    @Override
    public void withCosts(List<DealCostSummary> summaries) {
        this.costSummaries = summaries;
    }

    @Override
    public void withDealDays(DealDaysContainer container) {
        this.dealDaysContainer = container;
    }

    @Override
    public void withPowerProfilePositionViews(Map<LocalDate, List<PowerProfilePositionView>> positionMap) {
        this.powerProfilePositionMap = positionMap;
    }

    protected void determineFixedPrice(BasePositionHolder positionHolder) {

        LocalDate currentDate = positionHolder.getDetail().getStartDate();

        BigDecimal fixedPriceValue;
        if (hasDealDayByMonthPrices(currentDate))
            fixedPriceValue = getDayPrice(currentDate);
        else
            fixedPriceValue = dealSummary.getDealDetail().getFixedPriceValue();

        Quantity totalQuantity = new Quantity(
                BigDecimal.ZERO,
                context.getUnitOfMeasureCode());

        Amount totalAmount = new Amount(
                BigDecimal.ZERO,
                dealSummary.getDealDetail().getFixedPriceCurrencyCode());

        Price totalPrice = new Price(
                BigDecimal.ZERO,
                dealSummary.getDealDetail().getFixedPriceCurrencyCode(),
                context.getUnitOfMeasureCode());

        boolean needWeightedPrice = positionHolder.getDealHourByDayQuantity().isNotEmpty();

        HourFixedValueDayDetail hourSlots = positionHolder.getHourSlotsForPowerProfile();

        boolean calculateFixedPrice = false;
        if (hasDealHourByDayPrices(currentDate)) {
            calculateFixedPrice = true;
            for (int i=1; i < 25; i++) {
                if (hourSlots.getHourFixedValue(i) == null)
                    continue;;

                BigDecimal priceValue = getHourPrice(currentDate, i);
                if (priceValue != null) {
                    positionHolder.getDealHourByDayPrice().setHourValue(
                            i,
                            priceValue);
                } else {
                    positionHolder.getDealHourByDayPrice().setHourValue(
                            i,
                            fixedPriceValue);
                }
                Price hourlyPrice = new Price(
                        positionHolder.getDealHourByDayPrice().getHourValue(i),
                        dealSummary.getDealDetail().getFixedPriceCurrencyCode(),
                        dealSummary.getDealDetail().getFixedPriceUnitOfMeasureCode());

                if (needWeightedPrice) {
                    Quantity hourlyQuantity = new Quantity(
                            positionHolder.getDealHourByDayQuantity().getHourValue(i),
                            context.getUnitOfMeasureCode());

                    if (dealSummary.getDealDetail().getFixedPriceUnitOfMeasureCode() != context.getUnitOfMeasureCode()) {
                        Conversion conversion = UnitOfMeasureConverter.findConversion(
                                context.getUnitOfMeasureCode(),
                                dealSummary.getDealDetail().getFixedPriceUnitOfMeasureCode());
                        hourlyPrice = hourlyPrice.apply(conversion);
                    }
                    totalQuantity = totalQuantity.add(hourlyQuantity);


                    Amount hourlyAmount = hourlyQuantity.multiply(hourlyPrice);
                    totalAmount = totalAmount.add(hourlyAmount);
                } else {
                    totalPrice = totalPrice.add(hourlyPrice);
                }
            }

        }

        if (calculateFixedPrice) {

            positionHolder.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
            if (needWeightedPrice) {
                Price weightedAvgPrice = totalAmount.divide(totalQuantity);
                positionHolder.getDetail().setFixedPriceValue(weightedAvgPrice.roundPrice().getValue());
            } else {
                Price avgPrice = totalPrice.divide(BigDecimal.valueOf(24));
                positionHolder.getDetail().setFixedPriceValue(avgPrice.roundPrice().getValue());
            }

        } else {
            positionHolder.getDetail().setFixedPriceValue(fixedPriceValue);
        }

    }

    protected void determineFixedPriceRiskFactors(BasePositionHolder positionHolder) {


        if (dealSummary.getDealDetail().getFixedPriceCurrencyCode() != context.getCurrencyCode()) {
            positionHolder.setFixedPriceFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            dealSummary.getDealDetail().getFixedPriceCurrencyCode(),
                            context.getCurrencyCode(),
                            positionHolder.getDetail().getStartDate()));
        }
    }

    protected void generateHourlyPositionHolder(
            BasePositionHolder basePositionHolder,
            PriceTypeCode priceTypeCode,
            Integer priceIndexId) {
        DealHourlyPositionHolder dealHourlyPositionHolder = new DealHourlyPositionHolder();
        dealHourlyPositionHolder.setDealId(new EntityId(dealSummary.getId()));
        dealHourlyPositionHolder.setPriceIndexId(new EntityId(priceIndexId));
        dealHourlyPositionHolder.getDetail().setStartDate(basePositionHolder.getDetail().getStartDate());
        dealHourlyPositionHolder.getDetail().setEndDate(basePositionHolder.getDetail().getEndDate());
        dealHourlyPositionHolder.getDetail().setCreatedDateTime(basePositionHolder.getDetail().getCreatedDateTime());
        dealHourlyPositionHolder.getDetail().setPowerFlowCode(PowerFlowCode.HOURLY);
        dealHourlyPositionHolder.getDetail().setPriceTypeCode(priceTypeCode);
        dealHourlyPositionHolder.getDetail().setCurrencyCode(context.getCurrencyCode());
        dealHourlyPositionHolder.getDetail().setUnitOfMeasure(context.getUnitOfMeasureCode());
        dealHourlyPositionHolder.getDetail().setIsSettlementPosition(basePositionHolder.getSettlementDetail().getIsSettlementPosition());

        for (int i=1; i < 25; i++) {
            PriceRiskFactorHolder holder = riskFactorManager.determinePriceRiskFactor(
                    priceIndexId,
                    basePositionHolder.getDetail().getStartDate(),
                    i);
            dealHourlyPositionHolder.getPriceHourHolderMap().setHourPriceHolder(
                    i,
                    holder);
        }
        basePositionHolder.getHourlyPositionHolders().add(dealHourlyPositionHolder);
    }

    protected List<PriceRiskFactorHolder> determineBasisPriceRiskFactors(
            LocalDate factorDate,
            Integer priceIndexId) {

        ArrayList<PriceRiskFactorHolder> priceRiskFactorHolders = new ArrayList<>();

        PriceIndexPositionDateContainer priceIndexContainer = riskFactorManager
                .findPriceIndexContainer(priceIndexId);

        // Collect basis containers
        if (priceIndexContainer.isBasis()) {
            PriceIndexPositionDateContainer currentContainer = priceIndexContainer;

            while (currentContainer.isBasis()) {
                PriceIndexPositionDateContainer nextContainer = currentContainer.getBasisToHubContainer();

                PriceRiskFactorHolder nextPriceRiskFactorHolder = riskFactorManager.determinePriceRiskFactor(
                        nextContainer,
                        factorDate);

                if (nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode() != context.getCurrencyCode()) {
                    nextPriceRiskFactorHolder.setFxRiskFactorHolder(
                            riskFactorManager.determineFxRiskFactor(
                                    nextPriceRiskFactorHolder.getPriceIndex().getDetail().getCurrencyCode(),
                                    context.getCurrencyCode(),
                                    factorDate));
                }

                priceRiskFactorHolders.add(nextPriceRiskFactorHolder);
                currentContainer = nextContainer;

            }
        }
        return priceRiskFactorHolders;
    }
}
