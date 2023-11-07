package com.onbelay.dealcapture.dealmodule.positions.subscribe.subscriber;

import com.onbelay.core.entity.model.AuditManager;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.dealmodule.deal.publish.snapshot.GeneratePositionsRequest;
import com.onbelay.dealcapture.organization.subscribe.snapshot.SubOrganizationSnapshot;
import com.onbelay.dealcapture.organization.subscribe.subscriber.OrganizationUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.messaging.Message;

import java.util.List;
import java.util.function.Consumer;


@Configuration
public class GeneratePositionsRequestSubscriberConfig {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private AuditManager auditManager;

    @Autowired
    private GeneratePositionsRequestProcessor generatePositionsRequestProcessor;

    @Bean
    @Profile("messaging")
    public Consumer<Message<GeneratePositionsRequest>> generatePositionsRequestConsumer() {

        return msg -> {
            logger.info("consumer: msg = {}", msg);

            auditManager.setCurrentAuditUserName("obupdate");
            auditManager.setCurrentAuditComments("via messaging");

            GeneratePositionsRequest externalSnapshot = msg.getPayload();
            generatePositionsRequestProcessor.processRequest(externalSnapshot);
            try {
            } catch (OBRuntimeException e) {
                logger.error("Processing GeneratePositionsRequest failed");
                throw new NonTransientDataAccessResourceException("GeneratePositionsRequest save failed.");
            }

        };
    }



}
