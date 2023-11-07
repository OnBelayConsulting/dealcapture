package com.onbelay.dealcapture.parsing.model;

import com.onbelay.dealcapture.parsing.enums.OperatorType;

public class OperatorToken extends AbstractToken {

    private OperatorType operatorType;

    public OperatorToken(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    @Override
    public IsToken createCopy() {
        return new OperatorToken(operatorType);
    }
}
