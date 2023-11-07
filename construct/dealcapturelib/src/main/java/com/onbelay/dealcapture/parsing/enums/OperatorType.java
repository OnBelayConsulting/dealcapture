package com.onbelay.dealcapture.parsing.enums;

import java.util.HashMap;

public enum OperatorType {
    MULTIPLY ('*', 4),
    DIVIDE ('/', 3),
    ADD ('+', 2),
    SUBTRACT('-', 1);

    private int precedence;
    private char op;

    private static HashMap<Character, OperatorType> typeMap = new HashMap<>();

    static {
        for (OperatorType type : OperatorType.values()) {
            typeMap.put(type.op, type);
        }
    }

    OperatorType(char op, int precedence) {
        this.op = op;
        this.precedence = precedence;
    }

    public char getOp() {
        return op;
    }

    public boolean isHigherPrecedenceThan(OperatorType operatorTypeIn) {
        return precedence < operatorTypeIn.precedence;
    }

    public int getPrecedence() {
        return precedence;
    }

    public static OperatorType lookup(char op) {
        return typeMap.get(op);
    }

    public String toString() {
        return String.valueOf(op);
    }
}
