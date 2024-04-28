/*
 Copyright 2019, OnBelay Consulting Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.  
 */
package com.onbelay.dealcapture.dealmodule.positions.batch.sql;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.dealcapture.batch.BaseSqlMapper;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DealHourlyPositionSqlMapper extends BaseSqlMapper {


	public DealHourlyPositionSqlMapper(boolean isAddPrimaryKey) {
		super(isAddPrimaryKey);
	}

	public String getTableName() {
		return "DEAL_HOURLY_POSITION";
	}
	
	public List<String> getColumnNames() {
		ArrayList<String> list = new ArrayList<>();
		if (isAddPrimaryKey)
			list.add("ENTITY_ID");

		list.add("DEAL_ID");
		list.add("POWER_PROFILE_POSITION_ID");
		list.add("PRICE_INDEX_ID");
		list.add("FX_RISK_FACTOR_ID");
		list.add("START_DATE");
		list.add("END_DATE");
		list.add("POWER_FLOW_CODE");
		list.add("PRICE_TYPE_CODE");
		list.add("CREATE_UPDATE_DATETIME");
		list.add("CURRENCY_CODE");
		list.add("UNIT_OF_MEASURE_CODE");
		list.add("IS_SETTLEMENT_POSITION");
		list.add("ERROR_CODE");
		list.add("ERROR_MSG");

		for (int i=1; i< 25; i++) {
			list.add("HOUR_" + i + "_VALUE");
		}
		for (int i=1; i< 25; i++) {
			list.add("HOUR_" + i + "_RF_ID");
		}
		return list;
	}
	
	
	public void setValuesOnPreparedStatement(
			AbstractSnapshot snapshot,
			PreparedStatement preparedStatement) throws SQLException {
		DealHourlyPositionSnapshot position = (DealHourlyPositionSnapshot) snapshot;
		int n;
		if (isAddPrimaryKey) {
			n= 1;
			preparedStatement.setInt(1, position.getEntityId().getId());
		} else {
			n = 0;
		}
		preparedStatement.setInt(n + 1, position.getDealId().getId());

		if (position.getPowerProfilePositionId() != null)
			preparedStatement.setInt(n + 2, position.getPowerProfilePositionId().getId());
		else
			preparedStatement.setNull(n + 2, Types.INTEGER);


		if (position.getPriceIndexId() != null)
			preparedStatement.setInt(n + 3, position.getPriceIndexId().getId());
		else
			preparedStatement.setNull(n + 3, Types.INTEGER);


		if (position.getFxRiskFactorId() != null)
			preparedStatement.setInt(n + 4, position.getFxRiskFactorId().getId());
		else
			preparedStatement.setNull(n + 4, Types.INTEGER);

		preparedStatement.setDate(n + 5, Date.valueOf(position.getDetail().getStartDate()));
		preparedStatement.setDate(n + 6, Date.valueOf(position.getDetail().getEndDate()));

		preparedStatement.setString(n + 7, position.getDetail().getPowerFlowCodeValue());
		preparedStatement.setString(n + 8, position.getDetail().getPriceTypeCodeValue());

		preparedStatement.setTimestamp(n + 9, Timestamp.valueOf(position.getDetail().getCreatedDateTime()));

		preparedStatement.setString(n + 10, position.getDetail().getCurrencyCodeValue());
		preparedStatement.setString(n + 11, position.getDetail().getUnitOfMeasureValue());


		if (position.getDetail().getIsSettlementPosition() != null) {
			String value;
			if (position.getDetail().getIsSettlementPosition())
				value = "Y";
			else
				value = "N";
			preparedStatement.setString(n + 12, value);
		} else {
			preparedStatement.setNull(n + 12, Types.CHAR);
		}

		// Error Code
		if (position.getDetail().getErrorCode() != null)
			preparedStatement.setString(n + 13, position.getDetail().getErrorCode());
		else
			preparedStatement.setNull(n + 13, Types.VARCHAR);

		if (position.getDetail().getErrorMessage() != null)
			preparedStatement.setString(n + 14, position.getDetail().getErrorMessage());
		else
			preparedStatement.setNull(n + 14, Types.VARCHAR);

		int offset = 15;
		for (int i=1;i < 25; i++) {
			if (position.getHourFixedValueDetail().getHourFixedValue(i) != null)
				preparedStatement.setBigDecimal(n + offset, position.getHourFixedValueDetail().getHourFixedValue(i));
			else
				preparedStatement.setNull(n + offset, Types.DECIMAL);
			offset++;
		}
		for (int i=1;i < 25; i++) {
			if (position.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i) != null)
				preparedStatement.setInt(n + offset, position.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i));
			else
				preparedStatement.setNull(n + offset, Types.INTEGER);
			offset++;
		}
	}

	public boolean isAddPrimaryKey() {
		return isAddPrimaryKey;
	}

}
