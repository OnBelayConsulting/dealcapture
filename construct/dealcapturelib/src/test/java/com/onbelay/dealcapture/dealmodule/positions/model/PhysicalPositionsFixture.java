package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.UnitOfMeasureCode;
import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhysicalPositionsFixture {

    public static List<DealPositionSnapshot> createPositions(
            PhysicalDeal deal,
            PriceRiskFactor marketPriceRiskFactor,
            FxRiskFactor fxRiskFactor) {

        LocalDate current = deal.getDealDetail().getStartDate();
        ArrayList<DealPositionSnapshot> snapshots = new ArrayList<>();
        while (current.isAfter(deal.getDealDetail().getEndDate()) == false) {
            PhysicalPositionSnapshot snapshot = new PhysicalPositionSnapshot();

            snapshot.setDealPriceFxRiskFactorId(fxRiskFactor.generateEntityId());
            snapshot.setMarketPriceRiskFactorId(marketPriceRiskFactor.generateEntityId());

            snapshot.getDetail().setDealMarketValuationCode(ValuationCode.INDEX);
            snapshot.getDetail().setDealPriceValuationCode(ValuationCode.FIXED);

            snapshot.getDealPositionDetail().setFrequencyCode(FrequencyCode.DAILY);
            snapshot.getDealPositionDetail().setStartDate(current);
            snapshot.getDealPositionDetail().setEndDate(current);
            snapshot.getDealPositionDetail().setCurrencyCode(deal.getDealDetail().getReportingCurrencyCode());
            snapshot.getDealPositionDetail().setVolumeQuantity(deal.getDealDetail().getVolumeQuantity());
            snapshot.getDealPositionDetail().setVolumeUnitOfMeasure(UnitOfMeasureCode.GJ);
            snapshot.getDetail().setDealPrice(deal.getDetail().getDealPriceValue());
            snapshots.add(snapshot);
            current = current.plusDays(1);
        }

        return snapshots;
    }

}
