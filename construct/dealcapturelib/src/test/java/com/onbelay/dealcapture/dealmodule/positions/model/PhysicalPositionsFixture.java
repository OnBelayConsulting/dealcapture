package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.model.PhysicalDeal;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;
import com.onbelay.dealcapture.riskfactor.model.FxRiskFactor;
import com.onbelay.dealcapture.riskfactor.model.PriceRiskFactor;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import com.onbelay.shared.enums.UnitOfMeasureCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PhysicalPositionsFixture {

    public static List<DealPositionSnapshot> createPositions(
            PhysicalDeal deal,
            CurrencyCode currencyCode,
            LocalDateTime createdDateTime,
            PriceRiskFactor marketPriceRiskFactor,
            FxRiskFactor fxRiskFactor) {

        LocalDate current = deal.getDealDetail().getStartDate();
        ArrayList<DealPositionSnapshot> snapshots = new ArrayList<>();
        while (current.isAfter(deal.getDealDetail().getEndDate()) == false) {
            PhysicalPositionSnapshot snapshot = new PhysicalPositionSnapshot();
            snapshot.setDealId(deal.generateEntityId());
            snapshot.setDealPriceFxRiskFactorId(fxRiskFactor.generateEntityId());
            snapshot.setMarketPriceRiskFactorId(marketPriceRiskFactor.generateEntityId());

            snapshot.getPositionDetail().setCurrencyCode(currencyCode);
            snapshot.getPositionDetail().setCreatedDateTime(createdDateTime);

            snapshot.getPositionDetail().setFrequencyCode(FrequencyCode.DAILY);
            snapshot.getPositionDetail().setStartDate(current);
            snapshot.getPositionDetail().setEndDate(current);
            snapshot.getPositionDetail().setCurrencyCode(deal.getDealDetail().getReportingCurrencyCode());
            snapshot.getSettlementDetail().setIsSettlementPosition(true);
            snapshot.getSettlementDetail().setSettlementCurrencyCode(CurrencyCode.CAD);

            snapshot.getPositionDetail().setVolumeQuantityValue(deal.getDealDetail().getVolumeQuantity());
            snapshot.getPositionDetail().setVolumeUnitOfMeasure(UnitOfMeasureCode.GJ);
            snapshot.getPositionDetail().setFixedPriceValue(deal.getDealDetail().getFixedPriceValue());
            snapshots.add(snapshot);
            current = current.plusDays(1);
        }

        return snapshots;
    }

}
