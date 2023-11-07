package com.onbelay.dealcapture.parsing.model;

import java.util.List;
import java.util.stream.Collectors;

public class ListToken extends AbstractToken {
    private List<IsToken> tokens;

    public ListToken(List<IsToken> tokens) {
        this.tokens = tokens;
    }

    public List<IsToken> getTokens() {
        return tokens;
    }

    @Override
    public IsToken createCopy() {
        return new ListToken(
            tokens
                    .stream()
                    .map(c->c.createCopy())
                    .collect(Collectors.toList()));
    }
}
