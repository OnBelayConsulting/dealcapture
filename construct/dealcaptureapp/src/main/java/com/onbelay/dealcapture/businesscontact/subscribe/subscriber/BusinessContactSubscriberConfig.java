package com.onbelay.dealcapture.businesscontact.subscribe.subscriber;

import com.onbelay.core.entity.model.AuditManager;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.businesscontact.subscribe.snapshot.BusinessContactSubscriptionSnapshot;
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
public class BusinessContactSubscriberConfig {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private AuditManager auditManager;

    @Autowired
    private BusinessContactUpdater businessContactUpdater;

    @Bean
    @Profile("messaging")
    public Consumer<Message<List<BusinessContactSubscriptionSnapshot>>> businessContactConsumer() {

        return msg -> {
            logger.info("consumer: msg = {}", msg);

            auditManager.setCurrentAuditUserName("obupdate");
            auditManager.setCurrentAuditComments("via messaging");

            List<BusinessContactSubscriptionSnapshot> externalSnapshots = msg.getPayload();
            try {
                businessContactUpdater.updateBusinessContacts(externalSnapshots);
            } catch (OBRuntimeException e) {
                logger.error("BusinessContact save failed");
                throw new NonTransientDataAccessResourceException("BusinessContact save failed.");
            }

        };
    }



}
