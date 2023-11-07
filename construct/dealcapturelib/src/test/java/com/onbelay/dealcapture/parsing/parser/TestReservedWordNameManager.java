package com.onbelay.dealcapture.parsing.parser;

import com.onbelay.dealcapture.formulas.parser.BaseReservedWordManager;
import com.onbelay.dealcapture.formulas.tokens.PriceIndexNameToken;

public class TestReservedWordNameManager extends BaseReservedWordManager {

    public TestReservedWordNameManager() {
        initialize();
    }

    private void initialize() {
        formulaWordMap.put("AECO", c ->  new PriceIndexNameToken(c.getWord()));

    }
}
