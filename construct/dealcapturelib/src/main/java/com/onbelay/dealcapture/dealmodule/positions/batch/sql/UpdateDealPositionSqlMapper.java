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

import com.onbelay.dealcapture.dealmodule.positions.model.PositionValuationResult;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public abstract class UpdateDealPositionSqlMapper implements UpdateSqlMapper {


	public String getTableName() {
		return "DEAL_POSITION";
	}
	
	public List<String> getColumnNames() {
		ArrayList<String> list = new ArrayList<>();

		list.add("VALUED_DATETIME");
		list.add("ERROR_CODE");
		list.add("ERROR_MSG");
		list.add("MTM_VALUATION");
		list.add("COST_SETTLEMENT_AMOUNT");
		list.add("SETTLEMENT_AMOUNT");
		list.add("TOTAL_SETTLEMENT_AMOUNT");

		return list;
	}

	@Override
	public int lastColumnIndex() {
		return getColumnNames().size();
	}

	public int getStartingPoint() {
		return 7;
	}

	public void setValuesOnPreparedStatement(
			PositionValuationResult valuation,
			PreparedStatement preparedStatement) throws SQLException {

		preparedStatement.setTimestamp(1, Timestamp.valueOf(valuation.getCurrentDateTime()));

		preparedStatement.setString(2, valuation.getErrorCode().getCode());

		if (valuation.hasErrors())
			preparedStatement.setString(3, valuation.getCompleteErrorCodeMessage());
		else
			preparedStatement.setNull(3, Types.VARCHAR);

		if (valuation.getSettlementDetail().getMarkToMarketValuation() != null)
			preparedStatement.setBigDecimal(4, valuation.getSettlementDetail().getMarkToMarketValuation());
		else
			preparedStatement.setNull(4, Types.DECIMAL);


		if (valuation.getSettlementDetail().getCostSettlementAmount() != null)
			preparedStatement.setBigDecimal(5, valuation.getSettlementDetail().getCostSettlementAmount());
		else
			preparedStatement.setNull(5, Types.DECIMAL);

		if (valuation.getSettlementDetail().getSettlementAmount() != null)
			preparedStatement.setBigDecimal(6, valuation.getSettlementDetail().getSettlementAmount());
		else
			preparedStatement.setNull(6, Types.DECIMAL);

		if (valuation.getSettlementDetail().getTotalSettlementAmount() != null)
			preparedStatement.setBigDecimal(7, valuation.getSettlementDetail().getTotalSettlementAmount());
		else
			preparedStatement.setNull(7, Types.DECIMAL);

	}

}
