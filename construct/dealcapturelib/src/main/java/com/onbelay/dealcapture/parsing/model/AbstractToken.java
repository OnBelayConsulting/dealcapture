package com.onbelay.dealcapture.parsing.model;

public abstract class AbstractToken implements  IsToken {

    private boolean isProcessed = false;

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }
}
