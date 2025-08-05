package com.onbelay.dealcapture.dealmodule.deal.assembler;

import com.onbelay.core.entity.assembler.EntityAssembler;
import com.onbelay.core.entity.enums.EntityState;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.OverrideLattice;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealDayByMonthSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideMonthSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideSnapshot;

public class DealOverridesAssembler extends EntityAssembler {


    public DealOverridesAssembler() {
    }

    public DealOverrideSnapshot assemble (BaseDeal deal) {

        DealOverrideSnapshot dealOverrideSnapshot = new DealOverrideSnapshot();
        dealOverrideSnapshot.addPriceHeading();
        dealOverrideSnapshot.addQuantityHeading();
        dealOverrideSnapshot.addCostHeadings(deal.fetchCostNames());
        dealOverrideSnapshot.setStartDate(deal.getDealDetail().getStartDate());
        dealOverrideSnapshot.setEndDate(deal.getDealDetail().getEndDate());

        int PRICE_IDX = 0;
        int QUANTITY_IDX = 1;
        dealOverrideSnapshot.setEntityId(deal.generateEntityId());

        for (OverrideLattice lattice : deal.fetchOverrideLattices()) {
            DealOverrideMonthSnapshot byMonthOverride = new DealOverrideMonthSnapshot();
            byMonthOverride.setHeadings(dealOverrideSnapshot.getHeadings());
            byMonthOverride.setEntityId(deal.generateEntityId());
            byMonthOverride.setEntityState(EntityState.UNMODIFIED);

            dealOverrideSnapshot.addOverrideMonth(byMonthOverride);
            byMonthOverride.setMonthDate(lattice.getMonthDate());
            byMonthOverride.setMonthStartDate(lattice.getMonthStartDate());
            byMonthOverride.setMonthEndDate(lattice.getMonthEndDate());

            byMonthOverride.createDayOverrides(dealOverrideSnapshot.getHeadings().size());

            for (DealOverrideDaySnapshot dayOverride : byMonthOverride.getOverrideDays()) {
                if (lattice.getPriceDealDayByMonthSnapshot() != null) {
                    dayOverride.setDayValue(
                            PRICE_IDX,
                            lattice.getPriceDealDayByMonthSnapshot().getDetail().getDayValue(dayOverride.getDayOfMonth()));
                }
                if (lattice.getQuantityDealDayByMonthSnapshot() != null) {
                    dayOverride.setDayValue(
                            QUANTITY_IDX,
                            lattice.getQuantityDealDayByMonthSnapshot().getDetail().getDayValue(dayOverride.getDayOfMonth()));
                }
                if (deal.fetchCostNames().size() > 0) {
                    for (DealDayByMonthSnapshot costOverride : lattice.getDealDayCosts().values()) {
                        int costIndex = dealOverrideSnapshot.indexOfCostHeading(costOverride.getDetail().getDaySubTypeCodeValue());
                        if (costIndex >= 0) {
                            dayOverride.setDayValue(
                                    costIndex,
                                    costOverride.getDetail().getDayValue(dayOverride.getDayOfMonth()));
                        }
                    }
                }
            }
        }
        return dealOverrideSnapshot;
    }

}
