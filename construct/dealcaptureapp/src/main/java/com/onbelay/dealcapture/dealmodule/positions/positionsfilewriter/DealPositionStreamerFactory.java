package com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter;

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DealPositionStreamerFactory {

    private static Map<DealTypeCode, Supplier<DealPositionStreamer>> mappersMap = new HashMap<>();

    static {

        mappersMap.put(DealTypeCode.PHYSICAL_DEAL, PhysicalPositionStreamer::new);
        mappersMap.put(DealTypeCode.FINANCIAL_SWAP, FinancialSwapPositionStreamer::new);
        mappersMap.put(DealTypeCode.VANILLA_OPTION, VanillaOptionPositionStreamer::new);

    }


    public DealPositionStreamer newDealPositionStreamer(DealTypeCode dealTypeCode) {
        return mappersMap.get(dealTypeCode).get();
    }
}
