package com.onbelay.dealcapture.formulas.tokens;

import com.onbelay.dealcapture.parsing.model.AbstractToken;

public abstract class FormulaWordToken extends AbstractToken {

    private String word;

    public FormulaWordToken(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
