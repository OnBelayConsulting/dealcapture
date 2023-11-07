package com.onbelay.dealcapture.dealmodule.deal.publish.publisher;

import com.onbelay.dealcapture.dealmodule.deal.publish.snapshot.GeneratePositionsRequest;

import java.util.List;

public interface GeneratePositionsRequestPublisher {

    public void publish(GeneratePositionsRequest request);

}
