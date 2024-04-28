package com.onbelay.dealcapture.dealmodule.deal.adapter;

import com.onbelay.dealcapture.dealmodule.deal.snapshot.MarkToMarketResult;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.EvaluationContextRequest;

public interface MarkToMarketRestAdapter {

    public MarkToMarketResult runMarkToMarket(EvaluationContextRequest request);

}
