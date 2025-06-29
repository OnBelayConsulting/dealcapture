package com.onbelay.dealcapture.dealmodule.positions.positionsfilewriter;

import com.onbelay.dealcapture.dealmodule.positions.model.DealPositionView;

import java.util.List;

public interface DealPositionStreamer {

    public String[] getHeader();


    public List<String> asAList(DealPositionView view);
}
