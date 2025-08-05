package com.onbelay.dealcapture.dealmodule.deal.dealfilereader;

public interface SourceFileFormat<T> {

    T get(int index);

    public String[] getAsArray();

    public int length();

}
