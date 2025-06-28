package com.onbelay.dealcapture.organization.subscribe.subscriber;

import com.onbelay.core.entity.model.AuditManager;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.organization.subscribe.snapshot.OrganizationSubscriptionSnapshot;
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
public class OrganizationSubscriberConfig {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private AuditManager auditManager;

    @Autowired
    private OrganizationUpdater organizationUpdater;

    @Bean
    @Profile("messaging")
    public Consumer<Message<List<OrganizationSubscriptionSnapshot>>> organizationConsumer() {

        return msg -> {
            logger.info("consumer: msg = {}", msg);

            auditManager.setCurrentAuditUserName("obupdate");
            auditManager.setCurrentAuditComments("via messaging");

            List<OrganizationSubscriptionSnapshot> externalSnapshots = msg.getPayload();
            try {
                organizationUpdater.updateOrganizations(externalSnapshots);
            } catch (OBRuntimeException e) {
                logger.error("Organization save failed");
                throw new NonTransientDataAccessResourceException("Organization save failed.");
            }

        };
    }



}
