package com.onbelay.dealcapture.dealmodule.positions.optionvaluation;

import com.onbelay.dealcapture.dealmodule.deal.enums.OptionTypeCode;

public interface OptionEvaluator {

    OptionResult evaluate(
             OptionTypeCode callPutType,
             Double underlyingPrice,
             Double strikePrice,
             Double timeToExpire,
             Double interestRate,
             Double volatility);
}
