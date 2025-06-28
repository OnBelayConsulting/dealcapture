package com.onbelay.dealcapture.job.publish.publisher;

import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;

public interface DealJobRequestPublisher {

    public void publish(DealJobRequestPublication dealJobRequestPublication);

}
