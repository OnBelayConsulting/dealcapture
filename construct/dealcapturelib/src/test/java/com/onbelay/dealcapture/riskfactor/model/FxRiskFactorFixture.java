package com.onbelay.dealcapture.riskfactor.model;

import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.shared.enums.FrequencyCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FxRiskFactorFixture {
    
    public static FxRiskFactor createFxRiskFactor(FxIndex index, LocalDate marketDate) {
        FxRiskFactorSnapshot snapshot = new FxRiskFactorSnapshot();
        snapshot.getDetail().setMarketDate(marketDate);
        return FxRiskFactor.create(
                index, 
                snapshot);
    }


    public static List<FxRiskFactor> createFxRiskFactors(
            FxIndex index,
            LocalDate fromMarketDate,
            LocalDate toMarketDate) {

        ArrayList<FxRiskFactor> factors = new ArrayList<>();

        if (index.getDetail().getFrequencyCode() == FrequencyCode.DAILY) {
            LocalDate current = fromMarketDate;
            while (current.isAfter(toMarketDate) == false) {
                FxRiskFactorSnapshot snapshot = new FxRiskFactorSnapshot();
                snapshot.getDetail().setMarketDate(current);
                snapshot.getDetail().setCreatedDateTime(LocalDateTime.of(2023, 1, 1, 23, 10));
                factors.add(
                        FxRiskFactor.create(
                                index,
                                snapshot));
                current = current.plusDays(1);
            }
        } else {
            LocalDate current = fromMarketDate.withDayOfMonth(1);
            while (current.isAfter(toMarketDate) == false) {
                FxRiskFactorSnapshot snapshot = new FxRiskFactorSnapshot();
                snapshot.getDetail().setMarketDate(current);
                snapshot.getDetail().setCreatedDateTime(LocalDateTime.of(2023, 1, 1, 23, 10));
                factors.add(
                        FxRiskFactor.create(
                                index,
                                snapshot));
                current = current.plusMonths(1);
            }
        }
        return  factors;
    }


}
