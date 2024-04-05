package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.CostNameCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.CostTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSnapshot;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.math.BigDecimal;

public class DealCostFixture {

    public static DealCost createFixedCost(
            BaseDeal  deal,
            CurrencyCode currencyCode,
            CostNameCode costNameCode,
            BigDecimal value) {

        DealCostSnapshot snapshot = new DealCostSnapshot();
        snapshot.getDetail().setCostName(costNameCode);
        snapshot.getDetail().setCostValue(value);
        snapshot.getDetail().setCurrencyCode(currencyCode);

        return DealCost.create(deal, snapshot);
    }

    public static DealCost createPerUnitCost(
            BaseDeal  deal,
            CurrencyCode currencyCode,
            UnitOfMeasureCode unitOfMeasureCode,
            CostNameCode costNameCode,
            BigDecimal value) {

        DealCostSnapshot snapshot = new DealCostSnapshot();
        snapshot.getDetail().setCostName(costNameCode);
        snapshot.getDetail().setCostValue(value);
        snapshot.getDetail().setCurrencyCode(currencyCode);
        snapshot.getDetail().setUnitOfMeasureCode(unitOfMeasureCode);

        return DealCost.create(deal, snapshot);
    }


}
