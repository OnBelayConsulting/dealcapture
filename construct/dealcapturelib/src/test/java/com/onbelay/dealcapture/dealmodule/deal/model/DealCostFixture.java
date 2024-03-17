package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;

public class DealCostFixture {

    public static DealCost createCost(
            BaseDeal  deal,
            CostTypeCode costTypeCode,
            CostNameCode costNameCode,
            BigDecimal value) {

        DealCostSnapshot snapshot = new DealCostSnapshot();
        snapshot.getDetail().setCostName(costNameCode);
        snapshot.getDetail().setCostValue(value);

        return DealCost.create(deal, snapshot);
    }

}
