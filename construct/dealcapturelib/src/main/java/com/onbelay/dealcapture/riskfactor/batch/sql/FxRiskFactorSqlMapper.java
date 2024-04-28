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
package com.onbelay.dealcapture.riskfactor.batch.sql;

import com.onbelay.core.entity.snapshot.AbstractSnapshot;
import com.onbelay.dealcapture.batch.BaseSqlMapper;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import com.onbelay.dealcapture.riskfactor.snapshot.PriceRiskFactorSnapshot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FxRiskFactorSqlMapper extends BaseSqlMapper {

	public FxRiskFactorSqlMapper(boolean isAddPrimaryKey) {
		super(isAddPrimaryKey);
	}

	public String getTableName() {
		return "FX_RISK_FACTOR";
	}
	
	public List<String> getColumnNames() {
		ArrayList<String> list = new ArrayList<>();
		if (isAddPrimaryKey)
			list.add("ENTITY_ID");

		list.add("FX_INDEX_ID");
		list.add("MARKET_DATE");
		list.add("HOUR_ENDING");
		list.add("CREATE_UPDATE_DATETIME");
		list.add("ERROR_CODE");

		return list;
	}
	
	
	public void setValuesOnPreparedStatement(
			AbstractSnapshot snapshot,
			PreparedStatement preparedStatement) throws SQLException {
		FxRiskFactorSnapshot riskFactor = (FxRiskFactorSnapshot) snapshot;
		int n;
		if (isAddPrimaryKey) {
			n= 1;
			preparedStatement.setInt(1, riskFactor.getEntityId().getId());
		} else {
			n = 0;
		}
		preparedStatement.setInt(n + 1, riskFactor.getFxIndexId().getId());

		preparedStatement.setDate(n + 2, Date.valueOf(riskFactor.getDetail().getMarketDate()));

		if (riskFactor.getDetail().getHourEnding() != null) {
			preparedStatement.setInt(n + 3, riskFactor.getDetail().getHourEnding());
		} else {
			preparedStatement.setNull(n + 3, Types.NUMERIC);
		}
		preparedStatement.setTimestamp(n + 4, Timestamp.valueOf(riskFactor.getDetail().getCreatedDateTime()));

		// Error Code
		if (riskFactor.getDetail().getErrorCode() != null)
			preparedStatement.setString(n + 5, riskFactor.getDetail().getErrorCode());
		else
			preparedStatement.setNull(n + 5, Types.VARCHAR);

	}

	public boolean isAddPrimaryKey() {
		return isAddPrimaryKey;
	}
}
