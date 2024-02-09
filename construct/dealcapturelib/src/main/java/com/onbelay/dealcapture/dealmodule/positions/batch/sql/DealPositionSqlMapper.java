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

	protected static List<String> COL_NAMES = new ArrayList<String>(); 
	
	static {
		COL_NAMES.add("DEAL_ID");
		COL_NAMES.add("DEAL_TYPE_CODE");
		COL_NAMES.add("START_DATE");
		COL_NAMES.add("END_DATE");
		COL_NAMES.add("CREATE_UPDATE_DATETIME");
		COL_NAMES.add("VOLUME_QUANTITY");
		COL_NAMES.add("CURRENCY_CODE");
		COL_NAMES.add("VOLUME_UOM_CODE");
		COL_NAMES.add("FREQUENCY_CODE");
		COL_NAMES.add("MTM_VALUATION");
		COL_NAMES.add("ERROR_CODE");
	}


	public String getTableName() {
		return "DEAL_POSITION";
	}
	
	public abstract List<String> getColumnNames();
	
	
	public void setValuesOnPreparedStatement(
			DealPositionSnapshot position,
			PreparedStatement preparedStatement) throws SQLException {
		
		preparedStatement.setInt(1, position.getDealId().getId());
		preparedStatement.setString(2, position.getDealTypeValue());

		preparedStatement.setDate(3, Date.valueOf(position.getDealPositionDetail().getStartDate()));
		preparedStatement.setDate(4, Date.valueOf(position.getDealPositionDetail().getEndDate()));
		preparedStatement.setTimestamp(5, Timestamp.valueOf(position.getDealPositionDetail().getCreateUpdateDateTime()));

		preparedStatement.setBigDecimal(6, position.getDealPositionDetail().getVolumeQuantityValue());
		preparedStatement.setString(7, position.getDealPositionDetail().getCurrencyCodeValue());
		preparedStatement.setString(8, position.getDealPositionDetail().getVolumeUnitOfMeasureValue());
		preparedStatement.setString(9, position.getDealPositionDetail().getFrequencyCodeValue());

		if (position.getDealPositionDetail().getMarkToMarketValuation() != null)
			preparedStatement.setBigDecimal(10, position.getDealPositionDetail().getMarkToMarketValuation());
		else
			preparedStatement.setNull(10, Types.DECIMAL);

		if (position.getDealPositionDetail().getErrorCode() != null)
			preparedStatement.setString(11, position.getDealPositionDetail().getErrorCode());
		else
			preparedStatement.setNull(11, Types.VARCHAR);
		

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
