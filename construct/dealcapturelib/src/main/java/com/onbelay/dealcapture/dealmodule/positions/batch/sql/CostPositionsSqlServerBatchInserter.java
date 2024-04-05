package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.repository.CostPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Transactional
public class CostPositionsSqlServerBatchInserter implements CostPositionsBatchInserter{
    private static final Logger logger = LogManager.getLogger();

    @Value("${positionBatchSize:20}")
    protected int batchSize;


    @Autowired
    private CostPositionRepository costPositionRepository;

    @Override
    public void savePositions(List<CostPositionSnapshot> positions) {


        Long startId = costPositionRepository.reserveSequenceRange("COST_POSITION_SEQ", positions.size());
        for (CostPositionSnapshot snapshot : positions) {
            snapshot.setEntityId(new EntityId(startId.intValue()));
            startId++;
        }

        EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();

        Session session = entityManager.unwrap(Session.class);

        CostPositionSqlMapper sqlMapper = new CostPositionSqlMapper(true);

        try {
            session.doWork(
                    new BatchCostPositionSqlServerInsertWorker(
                            sqlMapper,
                            positions,
                            batchSize));
        } catch (RuntimeException t) {
            logger.error("batch insert failed", t);
            throw t;
        }


    }


    protected static class BatchCostPositionSqlServerInsertWorker implements Work {

        private CostPositionSqlMapper sqlMapper;
        private List<CostPositionSnapshot> positions;
        private int batchSize;

        public BatchCostPositionSqlServerInsertWorker(
                CostPositionSqlMapper sqlMapper,
                List<CostPositionSnapshot> positions,
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

                SubLister<CostPositionSnapshot> subLister = new SubLister<>(positions, batchSize);
                while (subLister.moreElements()) {
                    List<CostPositionSnapshot> myList = subLister.nextList();
                    for (CostPositionSnapshot position : myList) {
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
