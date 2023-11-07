package com.onbelay.dealcapture.dealmodule.deal.publish.publisherimpl;

import com.onbelay.dealcapture.dealmodule.deal.publish.publisher.GeneratePositionsRequestPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class GeneratePositionsRequestPublisherConfig {

    @Bean
    @Profile("messaging")
    public GeneratePositionsRequestPublisher GeneratePositionsRequestPublisher() {
        return new GeneratePositionsRequestPublisherBean();
    }

    @Bean
    @Profile("!messaging")
    public GeneratePositionsRequestPublisher mockGeneratePositionsRequestPublisher() {
        return new GeneratePositionsRequestPublisherStub();
    }
}
