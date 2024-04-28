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
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PowerProfilePositionSnapshot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PowerProfilePositionSqlMapper extends BaseSqlMapper {

	public PowerProfilePositionSqlMapper(boolean isAddPrimaryKey) {
		super(isAddPrimaryKey);
	}

	public String getTableName() {
		return "POWER_PROFILE_POSITION";
	}
	
	public List<String> getColumnNames() {
		ArrayList<String> list = new ArrayList<>();
		if (isAddPrimaryKey)
			list.add("ENTITY_ID");

		list.add("POWER_PROFILE_ID");
		list.add("PRICE_INDEX_ID");
		list.add("START_DATE");
		list.add("END_DATE");
		list.add("CREATE_UPDATE_DATETIME");
		list.add("POWER_FLOW_CODE");
		list.add("NUMBER_OF_HOURS");
		list.add("ERROR_CODE");
		list.add("ERROR_MSG");
		for (int i =1; i < 25; i++) {
			list.add("HOUR_" + i + "_" + "RF_ID");
		}

		return list;
	}
	
	
	public void setValuesOnPreparedStatement(
			AbstractSnapshot snapshot,
			PreparedStatement preparedStatement) throws SQLException {
		PowerProfilePositionSnapshot position = (PowerProfilePositionSnapshot) snapshot;
		int n;
		if (isAddPrimaryKey) {
			n= 1;
			preparedStatement.setInt(1, position.getEntityId().getId());
		} else {
			n = 0;
		}
		preparedStatement.setInt(n + 1, position.getPowerProfileId().getId());
		preparedStatement.setInt(n + 2, position.getPriceIndexId().getId());

		preparedStatement.setDate(n + 3, Date.valueOf(position.getDetail().getStartDate()));
		preparedStatement.setDate(n + 4, Date.valueOf(position.getDetail().getEndDate()));
		preparedStatement.setTimestamp(n + 5, Timestamp.valueOf(position.getDetail().getCreatedDateTime()));

		preparedStatement.setString(n + 6, position.getDetail().getPowerFlowCodeValue());

		preparedStatement.setInt(n + 7, position.getDetail().getNumberOfHours());

		// Error Code
		if (position.getDetail().getErrorCode() != null)
			preparedStatement.setString(n + 8, position.getDetail().getErrorCode());
		else
			preparedStatement.setNull(n + 8, Types.VARCHAR);

		if (position.getDetail().getErrorMessage() != null)
			preparedStatement.setString(n + 9, position.getDetail().getErrorMessage());
		else
			preparedStatement.setNull(n + 9, Types.VARCHAR);

		int start = n + 10;
		for (int i = 1; i < 25; i++) {
			if (position.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i) != null)
				preparedStatement.setInt(start, position.getHourPriceRiskFactorIdMap().getHourPriceRiskFactorId(i));
			else
				preparedStatement.setNull(start, Types.INTEGER);
			start++;
		}


	}

	public boolean isAddPrimaryKey() {
		return isAddPrimaryKey;
	}
}
