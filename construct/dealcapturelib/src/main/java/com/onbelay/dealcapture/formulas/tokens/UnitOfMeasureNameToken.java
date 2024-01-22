package com.onbelay.dealcapture.formulas.tokens;

import com.onbelay.dealcapture.parsing.model.IsToken;
import com.onbelay.shared.enums.UnitOfMeasureCode;

public class UnitOfMeasureNameToken extends FormulaWordToken {
    public UnitOfMeasureNameToken(String word) {
        super(word);
    }

    public UnitOfMeasureCode getUnitOfMeasureType() {
        return UnitOfMeasureCode.lookUp(getWord());
    }

    @Override
    public IsToken createCopy() {
        return new UnitOfMeasureNameToken(getWord());
    }
}
