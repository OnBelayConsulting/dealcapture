package com.onbelay.dealcapture.parsing.model;

public class CloseBracketToken extends AbstractToken implements IsToken{

    private char value = ')';

    public char getValue() {
        return value;
    }

    public String toString() {
        return "" + value;
    }

    @Override
    public IsToken createCopy() {
        CloseBracketToken token = new CloseBracketToken();
        token.setProcessed(false);
        return token;
    }
}
