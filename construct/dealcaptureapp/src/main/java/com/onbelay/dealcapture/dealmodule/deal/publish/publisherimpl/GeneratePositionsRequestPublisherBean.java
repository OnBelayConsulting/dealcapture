package com.onbelay.dealcapture.dealmodule.deal.publish.publisherimpl;

import com.onbelay.dealcapture.dealmodule.deal.publish.publisher.GeneratePositionsRequestPublisher;
import com.onbelay.dealcapture.dealmodule.deal.publish.snapshot.GeneratePositionsRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;

public class GeneratePositionsRequestPublisherBean implements GeneratePositionsRequestPublisher {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private StreamBridge streamBridge;

    @Value( "${deal.generate.positions.request.queue.name:deal.generate.positions.request}")
    private String queueName;

    @Override
    public void publish(GeneratePositionsRequest request) {

        streamBridge.send(queueName, request);
    }


}
