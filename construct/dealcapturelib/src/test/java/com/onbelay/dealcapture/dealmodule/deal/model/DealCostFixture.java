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
            BigDecimal value,
            CurrencyCode currency,
            UnitOfMeasureCode unitOfMeasureCode) {

        DealCostSnapshot snapshot = new DealCostSnapshot();
        snapshot.getDetail().setCostType(costTypeCode);
        snapshot.getDetail().setCostName(costNameCode);
        snapshot.getDetail().setCostValue(value);
        snapshot.getDetail().setCurrencyCode(currency);
        if (costTypeCode == CostTypeCode.PER_UNIT)
            snapshot.getDetail().setUnitOfMeasureCode(unitOfMeasureCode);

        return DealCost.create(deal, snapshot);
    }

}
