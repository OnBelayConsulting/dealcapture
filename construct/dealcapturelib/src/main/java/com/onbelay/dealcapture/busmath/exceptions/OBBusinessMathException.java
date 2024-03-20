package com.onbelay.dealcapture.busmath.exceptions;

public class OBBusinessMathException extends RuntimeException {

    public OBBusinessMathException() {
    }

    public OBBusinessMathException(String message) {
        super(message);
    }

    public OBBusinessMathException(String message, Throwable cause) {
        super(message, cause);
    }

    public OBBusinessMathException(Throwable cause) {
        super(cause);
    }
}
