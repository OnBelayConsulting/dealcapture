package com.onbelay.dealcapture.riskfactor.model;

import com.onbelay.dealcapture.pricing.model.PriceIndex;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceRiskFactorFixture {

    public static PriceRiskFactor createPriceRiskFactor(
            PriceIndex index,
            LocalDate marketDate) {

        PriceRiskFactorSnapshot snapshot = new PriceRiskFactorSnapshot();
        snapshot.getDetail().setMarketDate(marketDate);
        return PriceRiskFactor.create(
                index,
                snapshot);
    }


    public static List<PriceRiskFactor> createPriceRiskFactors(
            PriceIndex index,
            LocalDate fromMarketDate,
            LocalDate toMarketDate) {

        ArrayList<PriceRiskFactor> factors = new ArrayList<>();

        switch (index.getDetail().getFrequencyCode()) {

            case HOURLY -> {
                LocalDate current = fromMarketDate;
                while (current.isAfter(toMarketDate) == false) {
                    for (int i=1; i < 25; i++){
                        PriceRiskFactorSnapshot snapshot = new PriceRiskFactorSnapshot();
                        snapshot.getDetail().setMarketDate(current);
                        snapshot.getDetail().setHourEnding(i);
                        factors.add(
                                PriceRiskFactor.create(
                                        index,
                                        snapshot));
                    }
                    current = current.plusDays(1);
                }
            }

            case DAILY -> {
                LocalDate current = fromMarketDate;
                while (current.isAfter(toMarketDate) == false) {
                    PriceRiskFactorSnapshot snapshot = new PriceRiskFactorSnapshot();
                    snapshot.getDetail().setMarketDate(current);
                    factors.add(
                            PriceRiskFactor.create(
                                    index,
                                    snapshot));
                    current = current.plusDays(1);
                }
            }

            case MONTHLY -> {
                LocalDate current = fromMarketDate.withDayOfMonth(1);
                while (current.isAfter(toMarketDate) == false) {
                    PriceRiskFactorSnapshot snapshot = new PriceRiskFactorSnapshot();
                    snapshot.getDetail().setMarketDate(current);
                    factors.add(
                            PriceRiskFactor.create(
                                    index,
                                    snapshot));
                    current = current.plusMonths(1);
                }

            }

        }
        return factors;
    }


}
