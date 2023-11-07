package com.onbelay.dealcapture.parsing.parser;


import com.onbelay.dealcapture.parsing.model.IsToken;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TemplateParser {

    private String expressionText;
    private List<IsToken> tokenList = new ArrayList<>();


    public TemplateParser() {}

    public TemplateParser with(String text) {
        this.expressionText = text;
        return this;
    }


    public TemplateParser read() {
        TokenParser parser = new TokenParser(expressionText);
        tokenList = parser.parse();
        return this;
    }

    public TemplateParser map(Function<List<IsToken>, List<IsToken>> mapper) {
        this.tokenList = mapper.apply(tokenList);
        return this;
    }

    public List<IsToken> get() {
        return tokenList;
    }
}
