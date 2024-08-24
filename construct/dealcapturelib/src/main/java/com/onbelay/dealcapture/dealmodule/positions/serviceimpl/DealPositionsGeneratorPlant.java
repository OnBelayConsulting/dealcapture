package com.onbelay.dealcapture.dealmodule.positions.serviceimpl;

import com.onbelay.dealcapture.dealmodule.deal.model.DealDayByMonthView;
import com.onbelay.dealcapture.dealmodule.deal.model.DealHourByDayView;
import com.onbelay.dealcapture.dealmodule.deal.model.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionGenerator;
import com.onbelay.dealcapture.dealmodule.positions.service.DealPositionsEvaluationContext;
import com.onbelay.dealcapture.riskfactor.components.RiskFactorManager;

import java.util.List;

public interface DealPositionsGeneratorPlant {

    List<DealPositionGenerator> createDealPositionGenerators(
            DealPositionsEvaluationContext context,
            RiskFactorManager riskFactorManager,
            List<DealSummary> dealSummaries,
            List<DealCostSummary> dealCostSummaries,
            List<DealDayByMonthView> dealDayByMonthViews,
            List<DealHourByDayView> dealHourByDayViews);
}
