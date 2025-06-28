package com.onbelay.dealcapture.job.publishimpl;

import com.onbelay.dealcapture.job.publish.publisher.DealJobRequestPublisher;
import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;

public class DealJobRequestPublisherBean implements DealJobRequestPublisher {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private StreamBridge streamBridge;

    private String queueName = "dealcapture.job.queue";


    @Override
    public void publish(DealJobRequestPublication dealJobRequestPublication) {
        logger.debug("Publishing dealJobRequestPublication {}", dealJobRequestPublication);
        streamBridge.send(queueName, dealJobRequestPublication);

    }
}
