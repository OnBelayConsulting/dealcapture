package com.onbelay.dealcapture.riskfactor.batch.sql;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.repository.EntityRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.batch.BatchSqlServerInsertWorker;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class FxRiskFactorSqlServerBatchInserter implements FxRiskFactorBatchInserter {
    private static final Logger logger = LogManager.getLogger();

    @Value("${positionBatchSize:20}")
    protected int batchSize;


    @Autowired
    private EntityRepository entityRepository;

    @Override
    public void saveRiskFactors(List<FxRiskFactorSnapshot> positions) {


        Long startId = entityRepository.reserveSequenceRange("FX_RISK_FACTOR_SEQ", positions.size());
        for (FxRiskFactorSnapshot snapshot : positions) {
            snapshot.setEntityId(new EntityId(startId.intValue()));
            startId++;
        }

        EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();

        Session session = entityManager.unwrap(Session.class);

        FxRiskFactorSqlMapper sqlMapper = new FxRiskFactorSqlMapper(true);

        try {
            session.doWork(
                    new BatchSqlServerInsertWorker(
                            sqlMapper,
                            positions,
                            batchSize));
        } catch (RuntimeException t) {
            logger.error("batch insert failed", t);
            throw t;
        }


    }

}
