package com.onbelay.dealcapture.parsing.model;

import java.math.BigDecimal;

public class NumberToken extends AbstractToken implements IsToken {
    private BigDecimal value;

    public NumberToken(BigDecimal value) {

        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Object getOperand() {
        return value;
    }

    public String toString() {
        return value.toPlainString();
    }

    @Override
    public IsToken createCopy() {
        NumberToken newToken = new NumberToken(value);
        return newToken;
    }
}
