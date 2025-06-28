package com.onbelay.dealcapture.job.publishimpl;

import com.onbelay.dealcapture.job.publish.publisher.DealJobRequestPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DealJobRequestPublisherConfig {

    @Bean
    @Profile("messaging")
    public DealJobRequestPublisher dealJobRequestPublisher() {
        return new DealJobRequestPublisherBean();
    }

    @Bean
    @Profile("!messaging")
    public DealJobRequestPublisher mockDealJobRequestPublisher() {
        return new DealJobRequestPublisherStub();
    }
}
