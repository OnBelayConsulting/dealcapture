package com.onbelay.dealcapture.formulas.parser;

import com.onbelay.dealcapture.formulas.tokens.CurrencyNameToken;
import com.onbelay.dealcapture.formulas.tokens.PriceIndexNameToken;
import com.onbelay.dealcapture.formulas.tokens.UnitOfMeasureNameToken;

public class FormulaReservedWordManager extends BaseReservedWordManager {

    public FormulaReservedWordManager() {
        initialize();
    }

    private void initialize() {
        formulaWordMap.put("CAD", c ->  new CurrencyNameToken(c.getWord()));
        formulaWordMap.put("USD", c ->  new CurrencyNameToken(c.getWord()));
        formulaWordMap.put("AECO", c ->  new PriceIndexNameToken(c.getWord()));
        formulaWordMap.put("GJ", c ->  new UnitOfMeasureNameToken(c.getWord()));
        formulaWordMap.put("MMBTU", c ->  new UnitOfMeasureNameToken(c.getWord()));

    }
}
