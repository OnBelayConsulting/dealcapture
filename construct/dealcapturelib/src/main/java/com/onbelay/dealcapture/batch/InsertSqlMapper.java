package com.onbelay.dealcapture.batch;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface InsertSqlMapper {
    String getTableName();

    List<String> getColumnNames();

    public void setValuesOnPreparedStatement(
            AbstractSnapshot snapshot,
            PreparedStatement preparedStatement) throws SQLException;

    String createPlaceHolders();

    public boolean isAddPrimaryKey();
}