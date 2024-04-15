package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.entity.repository.EntityRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;
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
public class PowerProfilePositionsSqlServerBatchInserter implements PowerProfilePositionsBatchInserter{
    private static final Logger logger = LogManager.getLogger();

    @Value("${positionBatchSize:20}")
    protected int batchSize;


    @Autowired
    private EntityRepository entityRepository;

    @Override
    public void savePositions(List<PowerProfilePositionSnapshot> positions) {


        Long startId = entityRepository.reserveSequenceRange("POWER_PROFILE_POSITION_SEQ", positions.size());
        for (PowerProfilePositionSnapshot snapshot : positions) {
            snapshot.setEntityId(new EntityId(startId.intValue()));
            startId++;
        }

        EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();

        Session session = entityManager.unwrap(Session.class);

        PowerProfilePositionSqlMapper sqlMapper = new PowerProfilePositionSqlMapper(true);

        try {
            session.doWork(
                    new BatchPowerProfilePositionSqlServerInsertWorker(
                            sqlMapper,
                            positions,
                            batchSize));
        } catch (RuntimeException t) {
            logger.error("batch insert failed", t);
            throw t;
        }


    }


    protected static class BatchPowerProfilePositionSqlServerInsertWorker implements Work {

        private PowerProfilePositionSqlMapper sqlMapper;
        private List<PowerProfilePositionSnapshot> positions;
        private int batchSize;

        public BatchPowerProfilePositionSqlServerInsertWorker(
                PowerProfilePositionSqlMapper sqlMapper,
                List<PowerProfilePositionSnapshot> positions,
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

                SubLister<PowerProfilePositionSnapshot> subLister = new SubLister<>(positions, batchSize);
                while (subLister.moreElements()) {
                    List<PowerProfilePositionSnapshot> myList = subLister.nextList();
                    for (PowerProfilePositionSnapshot position : myList) {
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
