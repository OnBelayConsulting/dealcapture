package com.onbelay.dealcapture.parsing.model;

public class OpenBracketToken extends AbstractToken implements IsToken {

    private char value = '(';

    public char getValue() {
        return value;
    }

    public String toString() {
        return "" + value;
    }

    @Override
    public IsToken createCopy() {
        return new OpenBracketToken();
    }
}
