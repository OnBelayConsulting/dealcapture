package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.positions.model.PositionValuationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.jdbc.Work;

import java.sql.*;
import java.util.List;

public class BatchDealPositionUpdateWorker implements Work {
    private static final Logger logger = LogManager.getLogger();


    private List<PositionValuationResult> valuationResults;
    private int batchSize;
    private UpdateSqlMapper updateSqlMapper;

    public BatchDealPositionUpdateWorker(
            UpdateSqlMapper updateSqlMapper,
            List<PositionValuationResult> valuationResults,
            int batchSize) {

        super();
        this.updateSqlMapper = updateSqlMapper;
        this.valuationResults = valuationResults;
        this.batchSize = batchSize;
    }

    @Override
    public void execute(Connection connection) throws SQLException {

        List<String> colummnNames = updateSqlMapper.getColumnNames();
        StringBuilder builder = new StringBuilder("UPDATE DEAL_POSITION ");

        builder.append("SET  ");
        builder.append(colummnNames.get(0));
        builder.append(" = ?, ");

        for (int i = 1; i < colummnNames.size() - 1; i++) {
            builder.append(colummnNames.get(i));
            builder.append(" = ?, ");
        }
        builder.append(colummnNames.get(colummnNames.size() - 1));
        builder.append(" = ? WHERE ENTITY_ID = ? ");

        String sqlUpdate = builder.toString();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {

            SubLister<PositionValuationResult> subLister = new SubLister<>(valuationResults, batchSize);
            while (subLister.moreElements()) {
                List<PositionValuationResult> myList = subLister.nextList();

                for (PositionValuationResult valuation : myList) {
                    updateSqlMapper.setValuesOnPreparedStatement(
                            valuation,
                            preparedStatement);

                    preparedStatement.setInt(updateSqlMapper.lastColumnIndex() + 1, valuation.getDomainId());
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
