package com.onbelay.dealcapture.parsing.parser;

import com.onbelay.dealcapture.parsing.model.*;

import java.util.ArrayList;
import java.util.List;

public class TokenParser {

    private TokenReader reader;

    public TokenParser(String text) {
        reader = new TokenReader(text);
    }

    public List<IsToken> parse() {

        ArrayList<IsToken> tokens = new ArrayList<>();

        while (reader.hasMore()) {
            switch (reader.getNextType()) {

                case LETTER -> tokens.add(new ReservedWordToken(reader.readReservedWord()));

                case SINGLE_QUOTE -> tokens.add(new ReservedWordToken(reader.readText(), true));

                case NUMBER -> tokens.add(new NumberToken(reader.readNumber()));

                case OPERATOR -> tokens.add(new OperatorToken(reader.readOperator()));

                case OPEN_BRACKET -> {
                    tokens.add(new OpenBracketToken());
                    reader.read();
                }

                case CLOSE_BRACKET -> {
                    tokens.add(new CloseBracketToken());
                    reader.read();
                }

                case UNKNOWN, WHITE_SPACE -> reader.read();

            }


        }

        return tokens;
    }




}
