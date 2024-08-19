package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.ValuationCode;
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

            snapshot.getDetail().setCurrencyCode(currencyCode);
            snapshot.getDetail().setCreatedDateTime(createdDateTime);

            snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
            snapshot.getDetail().setStartDate(current);
            snapshot.getDetail().setEndDate(current);
            snapshot.getDetail().setCurrencyCode(deal.getDealDetail().getReportingCurrencyCode());
            snapshot.getSettlementDetail().setIsSettlementPosition(true);
            snapshot.getSettlementDetail().setSettlementCurrencyCode(CurrencyCode.CAD);

            snapshot.getDetail().setVolumeQuantityValue(deal.getDealDetail().getVolumeQuantity());
            snapshot.getDetail().setVolumeUnitOfMeasure(UnitOfMeasureCode.GJ);
            snapshot.getDetail().setFixedPriceValue(deal.getDetail().getFixedPriceValue());
            snapshots.add(snapshot);
            current = current.plusDays(1);
        }

        return snapshots;
    }

}
