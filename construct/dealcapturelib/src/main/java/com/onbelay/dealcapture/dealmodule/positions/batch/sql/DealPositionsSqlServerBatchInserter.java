package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.repository.EntityRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.dealcapture.batch.BatchSqlServerInsertWorker;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class DealPositionsSqlServerBatchInserter extends DealPositionsBaseBatchInserter implements DealPositionsBatchInserter{
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public void savePositions(
            DealTypeCode dealTypeCode,
            List<DealPositionSnapshot> positions) {
        DealPositionSqlMapper sqlMapper = sqlMappers.get(dealTypeCode).apply(true);


        Long startId = entityRepository.reserveSequenceRange("DEAL_POSITION_SEQ", positions.size());
        for (DealPositionSnapshot snapshot : positions) {
            snapshot.setEntityId(new EntityId(startId.intValue()));
            startId++;
        }

        EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();

        Session session = entityManager.unwrap(Session.class);

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
