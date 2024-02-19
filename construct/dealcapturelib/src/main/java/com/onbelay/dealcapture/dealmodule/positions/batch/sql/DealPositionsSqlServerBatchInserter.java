package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Transactional
public class DealPositionsSqlServerBatchInserter extends DealPositionsBaseBatchInserter implements DealPositionsBatchInserter{
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private DealPositionRepository dealPositionRepository;

    @Override
    public void savePositions(
            DealTypeCode dealTypeCode,
            List<DealPositionSnapshot> positions) {

        DealPositionSqlMapper sqlMapper = sqlMappers.get(DealTypeCode.PHYSICAL_DEAL).apply(true);

        Long startId = dealPositionRepository.reserveSequenceRange("DEAL_POSITION_SEQ", positions.size());
        for (DealPositionSnapshot snapshot : positions) {
            snapshot.setEntityId(new EntityId(startId.intValue()));
            startId++;
        }

        EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();

        Session session = entityManager.unwrap(Session.class);

        try {
            session.doWork(
                    new BatchDealPositionSqlServerInsertWorker(
                            sqlMapper,
                            positions,
                            batchSize));
        } catch (RuntimeException t) {
            logger.error("batch insert failed", t);
            throw t;
        }


    }


    protected static class BatchDealPositionSqlServerInsertWorker implements Work {

        private DealPositionSqlMapper sqlMapper;
        private List<DealPositionSnapshot> positions;
        private int batchSize;

        public BatchDealPositionSqlServerInsertWorker(
                DealPositionSqlMapper sqlMapper,
                List<DealPositionSnapshot> positions,
                int batchSize) {

            super();
            this.sqlMapper = sqlMapper;
            this.positions = positions;
            this.batchSize = batchSize;
        }

        @Override
        public void execute(Connection connection) throws SQLException {

            String sqlInsert = "INSERT into " +
                    sqlMapper.getTableName() +
                    " (" + String.join(",", sqlMapper.getColumnNames()) + ")" +
                    " VALUES " + sqlMapper.createPlaceHolders();

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

                SubLister<DealPositionSnapshot> subLister = new SubLister<>(positions, batchSize);
                while (subLister.moreElements()) {
                    List<DealPositionSnapshot> myList = subLister.nextList();
                    for (DealPositionSnapshot position : myList) {
                        sqlMapper.setValuesOnPreparedStatement(
                                position,
                                preparedStatement);
                        preparedStatement.addBatch();
                    }

                    preparedStatement.executeBatch();
                }

            } catch (RuntimeException e) {
                logger.error(e.getMessage());
                throw e;
            }


        }

    }

}
