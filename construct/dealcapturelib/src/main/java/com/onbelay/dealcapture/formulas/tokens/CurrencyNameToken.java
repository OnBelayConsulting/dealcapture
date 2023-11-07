package com.onbelay.dealcapture.formulas.tokens;

import com.onbelay.dealcapture.parsing.model.IsToken;
import com.onbelay.shared.enums.CurrencyCode;

public class CurrencyNameToken extends FormulaWordToken{
    public CurrencyNameToken(String word) {
        super(word);
    }

    public CurrencyCode getCurrencyType() {
        return CurrencyCode.lookUp(getWord());
    }

    @Override
    public IsToken createCopy() {
        return new CurrencyNameToken(getWord());
    }
}
