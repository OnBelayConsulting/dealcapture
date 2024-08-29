package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.dealcapture.dealmodule.positions.model.PositionValuationResult;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface UpdateSqlMapper {

    public List<String> getColumnNames();

    public int lastColumnIndex();

    public void setValuesOnPreparedStatement(
            PositionValuationResult valuation,
            PreparedStatement preparedStatement) throws SQLException;


    }
