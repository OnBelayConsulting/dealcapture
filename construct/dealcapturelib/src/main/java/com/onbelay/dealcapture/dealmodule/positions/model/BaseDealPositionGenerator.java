package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionDetail;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDealPositionGenerator implements DealPositionGenerator {
    protected DealSummary dealSummary;

    protected RiskFactorManager riskFactorManager;

    protected List<DealCostSummary> costSummaries = new ArrayList<>();

    protected DealDaysContainer dealDaysContainer;


    protected List<PositionHolder> positionHolders = new ArrayList<>();

    public BaseDealPositionGenerator(DealSummary dealSummary, RiskFactorManager riskFactorManager) {
        this.dealSummary = dealSummary;
        this.riskFactorManager = riskFactorManager;
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

    protected void setCosts(
            CostPositionDetail costPositionDetail,
            LocalDate positionDate,
            BigDecimal dayQuantity) {

        if (costSummaries.isEmpty())
            return;

        for (int i=0 ; i < 5; i++) {
            DealCostSummary summary = costSummaries.get(i);
            int costOffset = i+1;
            costPositionDetail.setCostName(
                    costOffset,
                    summary.getDetail().getCostNameCodeValue());

            BigDecimal costAmount;
            if (hasDealDaysContainerForCosts(positionDate)) {
                costAmount = dealDaysContainer.getDayCost(
                        positionDate,
                        summary.getDetail().getCostNameCodeValue());
            } else {
                costAmount = summary.getDetail().getCostValue();
            }

            if (summary.getDetail().getCostType() == CostTypeCode.PER_UNIT) {
                    costAmount
                        .multiply(
                           dayQuantity, MathContext.DECIMAL128);
            }

                costAmount = costAmount.setScale(3, RoundingMode.HALF_UP);

            costPositionDetail.setCostAmount(
                    costOffset,
                    costAmount);
        }
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
