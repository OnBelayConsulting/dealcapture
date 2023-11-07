package com.onbelay.dealcapture.dealmodule.positions.subscribe.subscriber;

import com.onbelay.dealcapture.dealmodule.deal.publish.snapshot.GeneratePositionsRequest;

public interface GeneratePositionsRequestProcessor {

    public void processRequest(GeneratePositionsRequest request);

}
