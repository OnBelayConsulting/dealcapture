package com.onbelay.dealcapture.formulas.model;

import com.onbelay.dealcapture.parsing.model.AbstractToken;

public abstract class BaseElement extends AbstractToken implements FormulaElement {
    public static final String SUCCESS = "Success";
    private boolean isInError = false;
    private String errorCode = SUCCESS;

    public BaseElement(boolean isInError, String errorCode) {
        this.isInError = isInError;
        this.errorCode = errorCode;
    }

    protected BaseElement() {

    }

    @Override
    public FormulaElement duplicate() {
        return (FormulaElement) createCopy();
    }

    public boolean isInError() {
        return isInError;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
