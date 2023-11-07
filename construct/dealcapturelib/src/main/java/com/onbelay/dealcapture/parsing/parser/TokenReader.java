package com.onbelay.dealcapture.parsing.parser;

import com.onbelay.dealcapture.parsing.enums.OperatorType;
import com.onbelay.dealcapture.parsing.enums.TokenType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.function.Supplier;

public class TokenReader {

    private char SINGLE_QUOTE = '\'';

    private ArrayList<Supplier<TokenType>> tokenIdentierList = new ArrayList<>();

    private StringBuilder buffer;
    private int currentPosition = 0;

    public TokenReader(String text) {
        this.buffer  = new StringBuilder(text.trim());
        registerTypeIdentifiers();
    }

    public boolean hasMore() {
        return currentPosition < buffer.length();
    }

    public Character read() {
        if (hasMore())
            return buffer.charAt(currentPosition++);
        else
            return null;
    }

    public TokenType getNextType() {

        if (hasMore() == false)
            return TokenType.NONE;

        for (Supplier<TokenType> identifier : tokenIdentierList) {
            TokenType type = identifier.get();
            if (type != null)
                return type;
        }

        return TokenType.UNKNOWN;
    }

    private void registerTypeIdentifiers() {
        tokenIdentierList.add(
                () -> {
                    if (buffer.charAt(currentPosition) == '(') return TokenType.OPEN_BRACKET;
                    else return null;
                });
        tokenIdentierList.add(
                () -> {
                    OperatorType operatorType = OperatorType.lookup(buffer.charAt(currentPosition));
                    if (operatorType != null)
                        return TokenType.OPERATOR;
                    else return null;
                });
        tokenIdentierList.add(
                () -> {
                    if (buffer.charAt(currentPosition) == ')') return TokenType.CLOSE_BRACKET;
                    else return null;
                });
        tokenIdentierList.add(
                () -> {
                    if (Character.isAlphabetic(buffer.charAt(currentPosition))) return TokenType.LETTER;
                    else return null;
                });
        tokenIdentierList.add(
                () -> {
                    if (buffer.charAt(currentPosition) == SINGLE_QUOTE) return TokenType.SINGLE_QUOTE;
                    else return null;
                });
        tokenIdentierList.add(
                () -> {
                    if (buffer.charAt(currentPosition) == ',') return TokenType.COMMA;
                    else return null;
                });
        tokenIdentierList.add(
                () -> {
                    if (Character.isWhitespace(buffer.charAt(currentPosition))) return TokenType.WHITE_SPACE;
                    else return null;
                });
        tokenIdentierList.add(
                () -> {
                    if (Character.isDigit(buffer.charAt(currentPosition))) return TokenType.NUMBER;
                    else return null;
                });
    }

    /**
     * Read one or more alphanumeric characters enclosed within single quotes.
     * @return
     */
    public String readText() {
        if (hasMore() == false)
            return null;

        read();

        StringBuilder builder = new StringBuilder();

        while (hasMore()) {
            if (buffer.charAt(currentPosition) == SINGLE_QUOTE) {
                read();
                break;

            }
            builder.append(read());
        }
        return builder.toString();
    }

    public String readReservedWord() {
        if (hasMore() == false)
            return null;

        StringBuilder builder = new StringBuilder();

        while (hasMore()) {
            Character currentChar = buffer.charAt(currentPosition);

            if (Character.isWhitespace(currentChar))
                break;
            if (Character.isAlphabetic(currentChar) == false)
                break;

            builder.append(read());
        }
        return builder.toString();
    }

    public BigDecimal readNumber() {
        if (hasMore() == false)
            return null;
        StringBuilder builder = new StringBuilder();
        while (hasMore()) {
            if (buffer.charAt(currentPosition) == '.')
                builder.append(read());
            else if (Character.isDigit(buffer.charAt(currentPosition)) == false)
                break;
            builder.append(read());
        }
        return new BigDecimal(builder.toString());
    }

    public OperatorType readOperator() {
        if (hasMore() == false)
            return null;
        return OperatorType.lookup(read());
    }



}
