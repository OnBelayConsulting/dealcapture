package com.onbelay.dealcapture.pricing.model;

import com.onbelay.dealcapture.dealmodule.deal.enums.FrequencyCode;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.shared.enums.CurrencyCode;

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


}
