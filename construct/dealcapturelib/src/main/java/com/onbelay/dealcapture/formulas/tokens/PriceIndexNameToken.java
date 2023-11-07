package com.onbelay.dealcapture.formulas.tokens;

import com.onbelay.dealcapture.parsing.model.IsToken;

public class PriceIndexNameToken extends FormulaWordToken {

    public PriceIndexNameToken(String name) {
        super(name);
    }

    @Override
    public IsToken createCopy() {
        return new PriceIndexNameToken(getWord());
    }
}
