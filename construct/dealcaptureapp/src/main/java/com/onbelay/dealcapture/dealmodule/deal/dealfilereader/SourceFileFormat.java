package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

public interface SourceFileFormat {

    DealColumnType get(int index);

    public String[] getAsArray();

    public int length();

}
