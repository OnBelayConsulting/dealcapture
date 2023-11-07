package com.onbelay.dealcapture.formulas.model;

import com.onbelay.dealcapture.parsing.model.IsToken;

public interface FormulaElement extends IsToken {

    public boolean isInError();

    public FormulaElement duplicate();
}
