package com.onbelay.dealcapture.job.subscribe.subscriber;

import com.onbelay.core.entity.model.AuditManager;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.dealcapture.job.enums.JobStatusCode;
import com.onbelay.dealcapture.job.publish.snapshot.DealJobRequestPublication;
import com.onbelay.dealcapture.job.service.DealJobService;
import com.onbelay.dealcapture.job.snapshot.DealJobSnapshot;
import com.onbelay.dealcapture.job.subscribe.runner.DealJobRunner;
import com.onbelay.dealcapture.job.subscribe.runner.DealJobRunnerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.messaging.Message;

import java.util.function.Consumer;


@Configuration
public class JobSubscriberConfig {
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private AuditManager auditManager;

    @Autowired
    private DealJobRunnerFactory dealJobRunnerFactory;


    @Autowired
    private DealJobService dealJobService;

    @Bean
    @Profile("messaging")
    public Consumer<Message<DealJobRequestPublication>> dealJobRunnerConsumer() {

        return msg -> {
            logger.info("consumer: msg = {}", msg);

            auditManager.setCurrentAuditUserName("obupdate");
            auditManager.setCurrentAuditComments("via messaging");

            DealJobRequestPublication publication = msg.getPayload();
            try {
                DealJobSnapshot snapshot = dealJobService.load(new EntityId(publication.getJobId()));
                DealJobRunner runner = dealJobRunnerFactory.getRunner(snapshot.getDetail().getJobTypeCode());
                if (snapshot.getDependsOnId() != null) {
                    DealJobSnapshot predecessor = dealJobService.load(snapshot.getDependsOnId());
                    if (predecessor.getDetail().getJobStatusCode() != JobStatusCode.COMPLETED) {
                        logger.error("Predecessor Job did not complete. Aborting job: " + snapshot.getEntityId().getId() );
                        return;
                    }
                }
                runner.execute(publication);
            } catch (OBRuntimeException e) {
                logger.error("Deal Job run failed");
                throw new NonTransientDataAccessResourceException("Deal Job run failed.");
            }

        };
    }



}
