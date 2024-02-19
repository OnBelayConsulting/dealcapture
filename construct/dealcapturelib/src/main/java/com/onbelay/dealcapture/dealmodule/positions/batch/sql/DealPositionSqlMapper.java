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

import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DealPositionSqlMapper {

	private final  boolean isAddPrimaryKey;

	public DealPositionSqlMapper(Boolean isAddPrimaryKey) {
		this.isAddPrimaryKey = isAddPrimaryKey;
	}


	protected int getStartingPoint() {
		if (isAddPrimaryKey == false)
			return 11;
		else
			return 12;
	}

	public String getTableName() {
		return "DEAL_POSITION";
	}
	
	public List<String> getColumnNames() {
		ArrayList<String> list = new ArrayList<>();
		if (isAddPrimaryKey)
			list.add("ENTITY_ID");

		list.add("DEAL_ID");
		list.add("DEAL_TYPE_CODE");
		list.add("START_DATE");
		list.add("END_DATE");
		list.add("CREATE_UPDATE_DATETIME");
		list.add("VOLUME_QUANTITY");
		list.add("CURRENCY_CODE");
		list.add("VOLUME_UOM_CODE");
		list.add("FREQUENCY_CODE");
		list.add("MTM_VALUATION");
		list.add("ERROR_CODE");

		return list;
	}
	
	
	public void setValuesOnPreparedStatement(
			DealPositionSnapshot position,
			PreparedStatement preparedStatement) throws SQLException {
		int n;
		if (isAddPrimaryKey) {
			n= 1;
			preparedStatement.setInt(1, position.getEntityId().getId());
		} else {
			n = 0;
		}
		preparedStatement.setInt(n + 1, position.getDealId().getId());
		preparedStatement.setString(n + 2, position.getDealTypeValue());

		preparedStatement.setDate(n + 3, Date.valueOf(position.getDealPositionDetail().getStartDate()));
		preparedStatement.setDate(n + 4, Date.valueOf(position.getDealPositionDetail().getEndDate()));
		preparedStatement.setTimestamp(n + 5, Timestamp.valueOf(position.getDealPositionDetail().getCreateUpdateDateTime()));

		preparedStatement.setBigDecimal(n + 6, position.getDealPositionDetail().getVolumeQuantityValue());
		preparedStatement.setString(n + 7, position.getDealPositionDetail().getCurrencyCodeValue());
		preparedStatement.setString(n + 8, position.getDealPositionDetail().getVolumeUnitOfMeasureValue());
		preparedStatement.setString(n + 9, position.getDealPositionDetail().getFrequencyCodeValue());

		if (position.getDealPositionDetail().getMarkToMarketValuation() != null)
			preparedStatement.setBigDecimal(n + 10, position.getDealPositionDetail().getMarkToMarketValuation());
		else
			preparedStatement.setNull(n + 10, Types.DECIMAL);

		if (position.getDealPositionDetail().getErrorCode() != null)
			preparedStatement.setString(n + 11, position.getDealPositionDetail().getErrorCode());
		else
			preparedStatement.setNull(n + 11, Types.VARCHAR);
		

	}

	public boolean isAddPrimaryKey() {
		return isAddPrimaryKey;
	}

	public String createPlaceHolders() {
		if (getColumnNames().size() < 1)
			return "()";
		
		StringBuffer buffer = new StringBuffer("(?");
		
		for (int i=1; i < getColumnNames().size(); i++) {
			buffer.append(",?");
		}
		buffer.append(")");
		return buffer.toString();
	}

}
