package com.onbelay.dealcapture.batch;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.utils.SubLister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.jdbc.Work;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class BatchDefaultInsertWorker implements Work {
    private static final Logger logger = LogManager.getLogger();


    private OBSqlMapper sqlMapper;
    private List<? extends AbstractSnapshot> snapshots;
    private int batchSize;

    public BatchDefaultInsertWorker(
            OBSqlMapper sqlMapper,
            List<? extends AbstractSnapshot> snapshots,
            int batchSize) {

        super();
        this.sqlMapper = sqlMapper;
        this.snapshots = snapshots;
        this.batchSize = batchSize;
    }

    @Override
    public void execute(Connection connection) throws SQLException {

        String sqlInsert = "INSERT into " +
                sqlMapper.getTableName() +
                " (" + String.join(",", sqlMapper.getColumnNames()) + ")" +
                " VALUES " + sqlMapper.createPlaceHolders();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            SubLister<? extends AbstractSnapshot> subLister = new SubLister<>(snapshots, batchSize);
            while (subLister.moreElements()) {
                List<? extends AbstractSnapshot> myList = subLister.nextList();
                for (AbstractSnapshot snapshot : myList) {
                    sqlMapper.setValuesOnPreparedStatement(
                            snapshot,
                            preparedStatement);
                    preparedStatement.addBatch();
                }

                preparedStatement.executeBatch();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                int j = 0;
                while (resultSet.next()) {
                    BigDecimal id = resultSet.getBigDecimal(1);
                    myList.get(j).setEntityId(new EntityId(id.intValue()));
                    j++;
                }
            }

        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw e;
        }


    }

}
