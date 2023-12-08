package com.onbelay.dealcapture.pricing.model;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.shared.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FxIndexFixture {


    public static FxIndex createDailyFxIndex(CurrencyCode from, CurrencyCode to) {
        String name = from.getCode()
                + "_"
                + to.getCode()
                + ":"
                + FrequencyCode.DAILY.getCode();
        FxIndexSnapshot snapshot = new FxIndexSnapshot();
        snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
        snapshot.getDetail().setFromCurrencyCode(from);
        snapshot.getDetail().setToCurrencyCode(to);
        snapshot.getDetail().setDescription( "From: "
                + from.getCode()
                + " To:"
                + to.getCode()
                + ":"
                + snapshot.getDetail().getFrequencyCode().getCode() );
        snapshot.getDetail().setName(name);

        FxIndex fxIndex = new FxIndex();
        fxIndex.createWith( snapshot);
        return fxIndex;
    }


    public static FxIndex createFxIndex(
            FrequencyCode frequencyCode,
            CurrencyCode from,
            CurrencyCode to) {
        String name = from.getCode()
                + "_"
                + to.getCode()
                + ":"
                + frequencyCode.getCode();

        FxIndexSnapshot snapshot = new FxIndexSnapshot();
        snapshot.getDetail().setFrequencyCode(frequencyCode);
        snapshot.getDetail().setFromCurrencyCode(from);
        snapshot.getDetail().setToCurrencyCode(to);
        snapshot.getDetail().setDescription( "From: "
                + from.getCode()
                + " To:"
                + to.getCode()
                + ":"
                + frequencyCode.getCode() );
        snapshot.getDetail().setName(name);

        FxIndex fxIndex = new FxIndex();
        fxIndex.createWith( snapshot);
        return fxIndex;
    }


    public static List<EntityId> generateDailyFxCurves(
            FxIndex fxIndex,
            LocalDate startMarketDate,
            LocalDate endMarketDate,
            LocalDateTime observedDateTime) {

        LocalDate currentDate = startMarketDate;
        ArrayList<FxCurveSnapshot> fxs = new ArrayList<>();
        while (currentDate.isAfter(endMarketDate) == false) {
            FxCurveSnapshot curveSnapshot = new FxCurveSnapshot();
            curveSnapshot.getDetail().setCurveDate(currentDate);
            curveSnapshot.getDetail().setCurveValue(BigDecimal.ONE);
            curveSnapshot.getDetail().setObservedDateTime(observedDateTime);
            curveSnapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);
            fxs.add(curveSnapshot);

            currentDate = currentDate.plusDays(1);
        }
        return fxIndex.saveFxCurves(fxs);
    }

}
