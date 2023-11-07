package com.onbelay.dealcapture.parsing.model;

public interface IsToken {

    public boolean isProcessed();

    public void setProcessed(boolean isProcessed);

    public IsToken createCopy();

}
