package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionDetail;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;
import com.onbelay.shared.enums.CurrencyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDealPositionGenerator implements DealPositionGenerator {
    private static final Logger logger = LogManager.getLogger();

    protected DealSummary dealSummary;

    protected RiskFactorManager riskFactorManager;

    protected List<DealCostSummary> costSummaries = new ArrayList<>();

    protected DealDaysContainer dealDaysContainer;


    protected List<PositionHolder> positionHolders = new ArrayList<>();

    public BaseDealPositionGenerator(DealSummary dealSummary, RiskFactorManager riskFactorManager) {
        this.dealSummary = dealSummary;
        this.riskFactorManager = riskFactorManager;
    }

    public void setPositionHolders(
            CurrencyCode targetCurrencyCode,
            LocalDate currentDate,
            PositionHolder holder) {

        if (dealSummary.getReportingCurrencyCode() != targetCurrencyCode) {
            holder.setCostFxHolder(
                    riskFactorManager.determineFxRiskFactor(
                            dealSummary.getReportingCurrencyCode(),
                            targetCurrencyCode,
                            currentDate));
        }

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
            LocalDate positionDate) {

        if (costSummaries.isEmpty())
            return;

        if (costSummaries.size() > 5) {
            logger.error("deal costs exceed position slot total of 5. Will consolidate  costs.");
            BigDecimal totalPerUnit = BigDecimal.ZERO;
            BigDecimal totalFixed = BigDecimal.ZERO;
            for (int j=0; j < costSummaries.size(); j++) {
                DealCostSummary summary = costSummaries.get(j);
                if (summary.getDetail().getCostType() == CostTypeCode.FIXED)
                    totalFixed = totalFixed.add(summary.getDetail().getCostValue());
                else
                    totalPerUnit = totalPerUnit.add(summary.getDetail().getCostValue());
            }
            costPositionDetail.setCost1Name(CostNameCode.TOTAL_FIXED_FEE.getCode());
            costPositionDetail.setCost1Amount(totalFixed.setScale(3, RoundingMode.HALF_UP));
            costPositionDetail.setCost2Name(CostNameCode.TOTAL_PER_UNIT_FEE.getCode());
            costPositionDetail.setCost2Amount(totalPerUnit.setScale(3, RoundingMode.HALF_UP));
        } else {

            for (int i = 0; i < costSummaries.size(); i++) {
                DealCostSummary summary = costSummaries.get(i);
                int costOffset = i + 1;
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

                costPositionDetail.setCostAmount(
                        costOffset,
                        costAmount);
            }
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
