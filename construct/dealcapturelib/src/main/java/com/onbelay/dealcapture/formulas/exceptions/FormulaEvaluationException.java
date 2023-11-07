package com.onbelay.dealcapture.formulas.exceptions;

public class FormulaEvaluationException extends RuntimeException {

    public FormulaEvaluationException() {
    }

    public FormulaEvaluationException(final String message) {
        super(message);
    }

    public FormulaEvaluationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FormulaEvaluationException(final Throwable cause) {
        super(cause);
    }
}
