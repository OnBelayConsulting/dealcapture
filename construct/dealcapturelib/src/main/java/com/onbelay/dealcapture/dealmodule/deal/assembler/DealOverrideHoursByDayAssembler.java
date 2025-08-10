package com.onbelay.dealcapture.dealmodule.deal.assembler;

import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.model.HourlyOverrideLattice;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealHourByDaySnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideHourSnapshot;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealOverrideHoursForDaySnapshot;

import java.time.LocalDate;

public class DealOverrideHoursByDayAssembler {


    public DealOverrideHoursForDaySnapshot assemble(
            BaseDeal deal,
            LocalDate dayDate) {

        DealOverrideHoursForDaySnapshot snapshot = new DealOverrideHoursForDaySnapshot();
        snapshot.setEntityId(deal.generateEntityId());
        snapshot.addPriceHeading();
        snapshot.addQuantityHeading();
        snapshot.addCostHeadings(deal.fetchCostNames());
        snapshot.setDayDate(dayDate);
        snapshot.createHourOverrides();
        HourlyOverrideLattice lattice = deal.buildHourlyOverrideLattice(dayDate);
        if (lattice.getPriceDealHourByDaySnapshot() != null) {
            for (int i=0; i < 24;i++) {
                DealOverrideHourSnapshot hourSnapshot = snapshot.getOverrideHours().get(i);
                hourSnapshot.setValueAt(
                        0,
                        lattice.getPriceDealHourByDaySnapshot().getDetail().getHourValue(i+1));
            }
        }
        if (lattice.getQuantityDealHourByDaySnapshot() != null) {
            for (int i=0; i < 24;i++) {
                DealOverrideHourSnapshot hourSnapshot = snapshot.getOverrideHours().get(i);
                hourSnapshot.setValueAt(
                        1,
                        lattice.getQuantityDealHourByDaySnapshot().getDetail().getHourValue(i+1));
            }
        }
        for (int j = 0; j < snapshot.getCostHeadings().size(); j++) {
            String costHeading = snapshot.getCostHeadings().get(j);
            DealHourByDaySnapshot costHourByDay = lattice.getDealHourCosts().get(costHeading);
            if (costHourByDay != null) {
                for (int i = 0; i < 24; i++) {
                    DealOverrideHourSnapshot hourSnapshot = snapshot.getOverrideHours().get(i);
                    hourSnapshot.setValueAt(
                            j + 2,
                            costHourByDay.getDetail().getHourValue(i + 1));
                }
            }

        }

        return snapshot;
    }

}
