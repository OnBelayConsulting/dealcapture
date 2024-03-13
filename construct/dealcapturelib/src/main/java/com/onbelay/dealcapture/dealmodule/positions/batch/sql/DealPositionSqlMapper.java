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
			return 26;
		else
			return 27;
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
		list.add("SETTLEMENT_AMOUNT");
		list.add("COST_SETTLEMENT_AMOUNT");
		list.add("TOTAL_SETTLEMENT_AMOUNT");
		list.add("SETTLEMENT_CURRENCY");
		list.add("SETTLEMENT_REFERENCE");
		list.add("COST_1_NAME");
		list.add("COST_1_AMOUNT");
		list.add("COST_2_NAME");
		list.add("COST_2_AMOUNT");
		list.add("COST_3_NAME");
		list.add("COST_3_AMOUNT");
		list.add("COST_4_NAME");
		list.add("COST_4_AMOUNT");
		list.add("COST_5_NAME");
		list.add("COST_5_AMOUNT");
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

		// Settlement
		if (position.getSettlementDetail().getSettlementAmount() != null)
			preparedStatement.setBigDecimal(n + 11, position.getSettlementDetail().getSettlementAmount());
		else
			preparedStatement.setNull(n + 11, Types.DECIMAL);

		if (position.getSettlementDetail().getCostSettlementAmount() != null)
			preparedStatement.setBigDecimal(n + 12, position.getSettlementDetail().getCostSettlementAmount());
		else
			preparedStatement.setNull(n + 12, Types.DECIMAL);

		if (position.getSettlementDetail().getTotalSettlementAmount() != null)
			preparedStatement.setBigDecimal(n + 13, position.getSettlementDetail().getTotalSettlementAmount());
		else
			preparedStatement.setNull(n + 13, Types.DECIMAL);

		if (position.getSettlementDetail().getSettlementCurrencyCodeValue() != null)
			preparedStatement.setString(n + 14, position.getSettlementDetail().getSettlementCurrencyCodeValue());
		else
			preparedStatement.setNull(n + 14, Types.VARCHAR);

		if (position.getSettlementDetail().getSettlementReference() != null)
			preparedStatement.setString(n + 15, position.getSettlementDetail().getSettlementReference());
		else
			preparedStatement.setNull(n + 15, Types.VARCHAR);

		// Costs
		if (position.getCostDetail().getCost1Name() != null)
			preparedStatement.setString(n + 16, position.getCostDetail().getCost1Name());
		else
			preparedStatement.setNull(n + 16, Types.VARCHAR);
		if (position.getCostDetail().getCost1Amount() != null)
			preparedStatement.setBigDecimal(n + 17, position.getCostDetail().getCost1Amount());
		else
			preparedStatement.setNull(n + 17, Types.DECIMAL);

		if (position.getCostDetail().getCost2Name() != null)
			preparedStatement.setString(n + 18, position.getCostDetail().getCost2Name());
		else
			preparedStatement.setNull(n + 18, Types.VARCHAR);
		if (position.getCostDetail().getCost2Amount() != null)
			preparedStatement.setBigDecimal(n + 19, position.getCostDetail().getCost2Amount());
		else
			preparedStatement.setNull(n + 19, Types.DECIMAL);

		if (position.getCostDetail().getCost3Name() != null)
			preparedStatement.setString(n + 20, position.getCostDetail().getCost3Name());
		else
			preparedStatement.setNull(n + 20, Types.VARCHAR);
		if (position.getCostDetail().getCost3Amount() != null)
			preparedStatement.setBigDecimal(n + 21, position.getCostDetail().getCost3Amount());
		else
			preparedStatement.setNull(n + 21, Types.DECIMAL);

		if (position.getCostDetail().getCost4Name() != null)
			preparedStatement.setString(n + 22, position.getCostDetail().getCost4Name());
		else
			preparedStatement.setNull(n + 22, Types.VARCHAR);
		if (position.getCostDetail().getCost4Amount() != null)
			preparedStatement.setBigDecimal(n + 23, position.getCostDetail().getCost4Amount());
		else
			preparedStatement.setNull(n + 23, Types.DECIMAL);

		if (position.getCostDetail().getCost5Name() != null)
			preparedStatement.setString(n + 24, position.getCostDetail().getCost5Name());
		else
			preparedStatement.setNull(n + 24, Types.VARCHAR);
		if (position.getCostDetail().getCost5Amount() != null)
			preparedStatement.setBigDecimal(n + 25, position.getCostDetail().getCost5Amount());
		else
			preparedStatement.setNull(n + 25, Types.DECIMAL);

		// Error Code
		if (position.getDealPositionDetail().getErrorCode() != null)
			preparedStatement.setString(n + 26, position.getDealPositionDetail().getErrorCode());
		else
			preparedStatement.setNull(n + 26, Types.VARCHAR);



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
