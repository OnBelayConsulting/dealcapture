package com.onbelay.dealcapture.parsing.model;

public class ReservedWordToken extends AbstractToken implements  IsToken {

    private String word;
    private boolean enclosedWithQuotes = false;

    public ReservedWordToken(String word) {
        this.word = word;
    }

    public ReservedWordToken(String word, boolean enclosedWithQuotes) {
        this.word = word;
        this.enclosedWithQuotes = enclosedWithQuotes;
    }

    public String getWord() {
        return word;
    }

    public boolean isEnclosedWithQuotes() {
        return enclosedWithQuotes;
    }

    @Override
    public IsToken createCopy() {
        return new ReservedWordToken(word, enclosedWithQuotes);
    }
}
