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
import com.onbelay.dealcapture.dealmodule.positions.snapshot.CostPositionSnapshot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CostPositionSqlMapper extends BaseSqlMapper {

	public CostPositionSqlMapper(Boolean isAddPrimaryKey) {
		super(isAddPrimaryKey);
	}

	public String getTableName() {
		return "COST_POSITION";
	}
	
	public List<String> getColumnNames() {
		ArrayList<String> list = new ArrayList<>();
		if (isAddPrimaryKey)
			list.add("ENTITY_ID");

		list.add("DEAL_ID");
		list.add("DEAL_COST_ID");
		list.add("START_DATE");
		list.add("END_DATE");
		list.add("CREATE_UPDATE_DATETIME");
		list.add("COST_NAME_CODE");
		list.add("COST_VALUE");
		list.add("COST_AMOUNT");
		list.add("VOLUME_QUANTITY");
		list.add("CURRENCY_CODE");
		list.add("UNIT_OF_MEASURE_CODE");
		list.add("FREQUENCY_CODE");
		list.add("IS_SETTLEMENT_POSITION");
		list.add("IS_FIXED_VALUED");
		list.add("COST_FX_RISK_FACTOR_ID");
		list.add("ERROR_CODE");

		return list;
	}
	
	
	public void setValuesOnPreparedStatement(
			AbstractSnapshot snapshot,
			PreparedStatement preparedStatement) throws SQLException {
		CostPositionSnapshot position = (CostPositionSnapshot) snapshot;
		int n;
		if (isAddPrimaryKey) {
			n= 1;
			preparedStatement.setInt(1, position.getEntityId().getId());
		} else {
			n = 0;
		}
		preparedStatement.setInt(n + 1, position.getDealId().getId());
		preparedStatement.setInt(n + 2, position.getDealCostId().getId());

		preparedStatement.setDate(n + 3, Date.valueOf(position.getDetail().getStartDate()));
		preparedStatement.setDate(n + 4, Date.valueOf(position.getDetail().getEndDate()));
		preparedStatement.setTimestamp(n + 5, Timestamp.valueOf(position.getDetail().getCreatedDateTime()));

		preparedStatement.setString(n + 6, position.getDetail().getCostNameCodeValue());

		if (position.getDetail().getCostValue() != null)
			preparedStatement.setBigDecimal(n + 7, position.getDetail().getCostValue());
		else
			preparedStatement.setNull(n + 7, Types.DECIMAL);

		if (position.getDetail().getCostAmount() != null)
			preparedStatement.setBigDecimal(n + 8, position.getDetail().getCostAmount());
		else
			preparedStatement.setNull(n + 8, Types.DECIMAL);

		if (position.getDetail().getVolumeQuantityValue() != null)
			preparedStatement.setBigDecimal(n + 9, position.getDetail().getVolumeQuantityValue());
		else
			preparedStatement.setNull(n + 9, Types.DECIMAL);

		preparedStatement.setString(n + 10, position.getDetail().getCurrencyCodeValue());
		preparedStatement.setString(n + 11, position.getDetail().getUnitOfMeasureValue());
		preparedStatement.setString(n + 12, position.getDetail().getFrequencyCodeValue());

		if (position.getDetail().getIsSettlementPosition() != null) {
			String value;
			if (position.getDetail().getIsSettlementPosition() == true)
				value = "Y";
			else
				value = "N";
			preparedStatement.setString(n + 13, value);
		} else {
			preparedStatement.setNull(n + 13, Types.CHAR);
		}

		if (position.getDetail().getIsFixedValued() != null) {
			String value;
			if (position.getDetail().getIsFixedValued() == true)
				value = "Y";
			else
				value = "N";
			preparedStatement.setString(n + 14, value);
		} else {
			preparedStatement.setNull(n + 14, Types.CHAR);
		}

		if (position.getCostFxRiskFactorId() != null)
			preparedStatement.setInt(n+ 15, position.getCostFxRiskFactorId().getId());
		else
			preparedStatement.setNull(n+ 15, Types.INTEGER);

		// Error Code
		if (position.getDetail().getErrorCode() != null)
			preparedStatement.setString(n + 16, position.getDetail().getErrorCode());
		else
			preparedStatement.setNull(n + 16, Types.VARCHAR);

	}

	public boolean isAddPrimaryKey() {
		return isAddPrimaryKey;
	}
}
