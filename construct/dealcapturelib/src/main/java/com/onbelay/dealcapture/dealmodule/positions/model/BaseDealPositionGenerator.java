package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.busmath.model.Amount;
import com.onbelay.dealcapture.busmath.model.Conversion;
import com.onbelay.dealcapture.busmath.model.Price;
import com.onbelay.dealcapture.busmath.model.Quantity;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.positions.service.EvaluationContext;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.dealcapture.unitofmeasure.UnitOfMeasureConverter;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PipedOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDealPositionGenerator implements DealPositionGenerator {
    private static final Logger logger = LogManager.getLogger();

    protected DealSummary dealSummary;

    protected RiskFactorManager riskFactorManager;

    protected List<DealCostSummary> costSummaries = new ArrayList<>();

    protected DealDaysContainer dealDaysContainer;

    protected List<CostPositionHolder> costPositionHolders = new ArrayList<>();

    protected List<PositionHolder> positionHolders = new ArrayList<>();

    public BaseDealPositionGenerator(DealSummary dealSummary, RiskFactorManager riskFactorManager) {
        this.dealSummary = dealSummary;
        this.riskFactorManager = riskFactorManager;
    }

    protected EvaluationContext modifyEvaluationContextForDeal(EvaluationContext context) {
        final LocalDate startDate;
        final LocalDate endDate;
        if (context.getStartPositionDate() != null) {
            if (context.getStartPositionDate().isAfter(dealSummary.getStartDate()))
                startDate = context.getStartPositionDate();
            else
                startDate = dealSummary.getStartDate();
        } else {
            startDate = dealSummary.getStartDate();
        }
        if (context.getEndPositionDate() != null) {
            if (context.getEndPositionDate().isBefore(dealSummary.getEndDate()))
                endDate = context.getEndPositionDate();
            else
                endDate = dealSummary.getEndDate();
        } else {
            endDate = dealSummary.getEndDate();
        }

        CurrencyCode targetCurrencyCode;
        if (context.getCurrencyCode() != null)
            targetCurrencyCode = context.getCurrencyCode();
        else
            targetCurrencyCode = dealSummary.getReportingCurrencyCode();

        UnitOfMeasureCode targetUnitOfMeasureCode;
        if (context.getUnitOfMeasureCode() != null)
            targetUnitOfMeasureCode = context.getUnitOfMeasureCode();
        else
            targetUnitOfMeasureCode = dealSummary.getVolumeUnitOfMeasureCode();

        return EvaluationContext
                .build()
                .withCreatedDateTime(context.getCreatedDateTime())
                .withCurrency(targetCurrencyCode)
                .withUnitOfMeasure(targetUnitOfMeasureCode)
                .withStartPositionDate(startDate)
                .withEndPositionDate(endDate);
    }

    protected void setBasePositionHolderAttributes(
            EvaluationContext context,
            PositionHolder positionHolder) {

        DealPositionSnapshot positionSnapshot = positionHolder.getDealPositionSnapshot();
        positionSnapshot.getDealPositionDetail().setCreateUpdateDateTime(context.getCreatedDateTime());

        BigDecimal dayQuantity = null;
        if (hasDealDaysContainerForQuantity(positionSnapshot.getDealPositionDetail().getStartDate()))
            dayQuantity = getDayQuantity(positionSnapshot.getDealPositionDetail().getStartDate());

        if (dayQuantity == null)
            dayQuantity = dealSummary.getVolumeQuantity();

        if (context.getUnitOfMeasureCode() != dealSummary.getVolumeUnitOfMeasureCode()) {
            Conversion conversion = UnitOfMeasureConverter.findConversion(
                    context.getUnitOfMeasureCode(),
                    dealSummary.getVolumeUnitOfMeasureCode());

            dayQuantity = dayQuantity.multiply(conversion.getValue(), MathContext.DECIMAL128);
        }

        positionSnapshot.getDealPositionDetail().setVolumeQuantityValue(dayQuantity);

        positionSnapshot.getDealPositionDetail().setVolumeUnitOfMeasure(context.getUnitOfMeasureCode());

        positionSnapshot.getDealPositionDetail().setCreateUpdateDateTime(context.getCreatedDateTime());

        positionSnapshot.getDealPositionDetail().setCurrencyCode(context.getCurrencyCode());

        if (context.getCurrencyCode() == dealSummary.getSettlementCurrencyCode()) {
            positionSnapshot.getSettlementDetail().setIsSettlementPosition(true);
            positionSnapshot.getSettlementDetail().setSettlementCurrencyCode(context.getCurrencyCode());
        } else {
            positionSnapshot.getSettlementDetail().setIsSettlementPosition(false);
        }

        generateCostPositionHolders(
                context,
                positionHolder.getDealPositionSnapshot().getDealPositionDetail().getVolumeQuantityValue(),
                positionHolder.getDealPositionSnapshot().getSettlementDetail().getIsSettlementPosition(),
                positionHolder.getDealPositionSnapshot().getDealPositionDetail().getStartDate());

    }

    public boolean hasDealDaysContainerForCosts(LocalDate positionDate) {
        if (dealDaysContainer == null)
            return false;
        return (dealDaysContainer.hasMonthCosts(positionDate));
    }

    public boolean hasDealDaysContainerForQuantity(LocalDate positionDate) {
        if (dealDaysContainer == null)
            return false;
        return (dealDaysContainer.hasMonthQuantity(positionDate));
    }


    public boolean hasDealDaysContainerForPrice(LocalDate positionDate) {
        if (dealDaysContainer == null)
            return false;
        return (dealDaysContainer.hasMonthPrice(positionDate));
    }

    protected BigDecimal getDayQuantity(LocalDate positionDate) {
        if (hasDealDaysContainerForQuantity(positionDate)) {
            return dealDaysContainer.getDayQuantity(positionDate);
        } else {
            return null;
        }
    }

    protected BigDecimal getDayPrice(LocalDate positionDate) {
        if (hasDealDaysContainerForPrice(positionDate)) {
            return dealDaysContainer.getDayPrice(positionDate);
        } else {
            return null;
        }
    }

    protected void generateCostPositionHolders(
            EvaluationContext context,
            BigDecimal volumeQuantityValue,
            boolean isSettlementPosition,
            LocalDate currentDate) {

        for (DealCostSummary summary : costSummaries) {
            CostPositionHolder holder = new CostPositionHolder();
            holder.setDealCostSummary(summary);
            holder.getSnapshot().getDetail().setStartDate(currentDate);
            holder.getSnapshot().getDetail().setEndDate(currentDate);
            holder.getSnapshot().getDetail().setFrequencyCode(FrequencyCode.DAILY);
            holder.getSnapshot().getDetail().setCreatedDateTime(context.getCreatedDateTime());

            holder.getSnapshot().getDetail().setVolumeQuantityValue(volumeQuantityValue);

            holder.getSnapshot().getDetail().setCurrencyCodeValue(context.getCurrencyCode().getCode());
            holder.getSnapshot().getDetail().setUnitOfMeasureValue(context.getUnitOfMeasureCode().getCode());

            BigDecimal cost = null;
            if (hasDealDaysContainerForCosts(currentDate))
                cost = dealDaysContainer.getDayCost(currentDate, summary.getCostNameCode().getCode());

            if (cost != null)
                holder.getSnapshot().getDetail().setCostValue(cost);
            else
                holder.getSnapshot().getDetail().setCostValue(summary.getCostValue());

            holder.getSnapshot().getDetail().setCostNameCodeValue(summary.getCostNameCode().getCode());
            holder.getSnapshot().getDetail().setIsSettlementPosition(isSettlementPosition);

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


    public List<CostPositionSnapshot> generateCostPositionSnapshots(LocalDateTime createdDateTime) {

        List<CostPositionSnapshot> costs = new ArrayList<>();

        for (CostPositionHolder holder : costPositionHolders) {
            CostPositionSnapshot costPositionSnapshot = holder.getSnapshot();
            costPositionSnapshot.getDetail().setCreatedDateTime(createdDateTime);

            costPositionSnapshot.setDealId(dealSummary.getDealId());
            costPositionSnapshot.setDealCostId(new EntityId(holder.getDealCostSummary().getId()));

            costPositionSnapshot.getDetail().setErrorCode("0");

            if (holder.getCostFxHolder() != null) {
                costPositionSnapshot.setCostFxRiskFactorId(holder.getSnapshot().getCostFxRiskFactorId());
            }

            if (holder.getSnapshot().getDetail().getIsFixedValued()) {
                holder.getSnapshot().getDetail().setValuedDateTime(createdDateTime);

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

            costs.add(costPositionSnapshot);
        }
        return costs;
    }


    @Override
    public DealSummary getDealSummary() {
        return dealSummary;
    }

    public RiskFactorManager getRiskFactorManager() {
        return riskFactorManager;
    }

    @Override
    public List<PositionHolder> getPositionHolders() {
        return positionHolders;
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
    public List<DealCostSummary> getCostSummaries() {
        return costSummaries;
    }

    @Override
    public DealDaysContainer getDealDayContainer() {
        return dealDaysContainer;
    }
}
