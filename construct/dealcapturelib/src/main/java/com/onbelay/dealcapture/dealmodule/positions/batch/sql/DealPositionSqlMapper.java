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
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DealPositionSqlMapper extends BaseSqlMapper {

	public DealPositionSqlMapper(boolean isAddPrimaryKey) {
		super(isAddPrimaryKey);
	}

	protected int getStartingPoint() {
		if (isAddPrimaryKey == false)
			return 20;
		else
			return 21;
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
		list.add("POWER_FLOW_CODE");
		list.add("MTM_VALUATION");
		list.add("SETTLEMENT_AMOUNT");
		list.add("COST_SETTLEMENT_AMOUNT");
		list.add("TOTAL_SETTLEMENT_AMOUNT");
		list.add("SETTLEMENT_CURRENCY");
		list.add("SETTLEMENT_REFERENCE");
		list.add("IS_SETTLEMENT_POSITION");
		list.add("FIXED_PRICE");
		list.add("FIXED_PRICE_FX_RISK_FACTOR_ID");

		list.add("ERROR_CODE");

		return list;
	}
	
	
	public void setValuesOnPreparedStatement(
			AbstractSnapshot snapshot,
			PreparedStatement preparedStatement) throws SQLException {
		DealPositionSnapshot position = (DealPositionSnapshot) snapshot;
		int n;
		if (isAddPrimaryKey) {
			n= 1;
			preparedStatement.setInt(1, position.getEntityId().getId());
		} else {
			n = 0;
		}
		preparedStatement.setInt(n + 1, position.getDealId().getId());
		preparedStatement.setString(n + 2, position.getDealTypeValue());

		preparedStatement.setDate(n + 3, Date.valueOf(position.getPositionDetail().getStartDate()));
		preparedStatement.setDate(n + 4, Date.valueOf(position.getPositionDetail().getEndDate()));
		preparedStatement.setTimestamp(n + 5, Timestamp.valueOf(position.getPositionDetail().getCreatedDateTime()));

		preparedStatement.setBigDecimal(n + 6, position.getPositionDetail().getVolumeQuantityValue());
		preparedStatement.setString(n + 7, position.getPositionDetail().getCurrencyCodeValue());
		preparedStatement.setString(n + 8, position.getPositionDetail().getVolumeUnitOfMeasureValue());
		preparedStatement.setString(n + 9, position.getPositionDetail().getFrequencyCodeValue());

		if (position.getPositionDetail().getPowerFlowCodeValue() != null)
			preparedStatement.setString(n + 10, position.getPositionDetail().getPowerFlowCodeValue());
		else
			preparedStatement.setNull(n + 10, Types.VARCHAR);

		if (position.getSettlementDetail().getMarkToMarketValuation() != null)
			preparedStatement.setBigDecimal(n + 11, position.getSettlementDetail().getMarkToMarketValuation());
		else
			preparedStatement.setNull(n + 11, Types.DECIMAL);

		// Settlement
		if (position.getSettlementDetail().getSettlementAmount() != null)
			preparedStatement.setBigDecimal(n + 12, position.getSettlementDetail().getSettlementAmount());
		else
			preparedStatement.setNull(n + 12, Types.DECIMAL);

		if (position.getSettlementDetail().getCostSettlementAmount() != null)
			preparedStatement.setBigDecimal(n + 13, position.getSettlementDetail().getCostSettlementAmount());
		else
			preparedStatement.setNull(n + 13, Types.DECIMAL);

		if (position.getSettlementDetail().getTotalSettlementAmount() != null)
			preparedStatement.setBigDecimal(n + 14, position.getSettlementDetail().getTotalSettlementAmount());
		else
			preparedStatement.setNull(n + 14, Types.DECIMAL);

		if (position.getSettlementDetail().getSettlementCurrencyCodeValue() != null)
			preparedStatement.setString(n + 15, position.getSettlementDetail().getSettlementCurrencyCodeValue());
		else
			preparedStatement.setNull(n + 15, Types.VARCHAR);

		if (position.getSettlementDetail().getSettlementReference() != null)
			preparedStatement.setString(n + 16, position.getSettlementDetail().getSettlementReference());
		else
			preparedStatement.setNull(n + 16, Types.VARCHAR);


		if (position.getSettlementDetail().getIsSettlementPosition() != null) {
			String value;
			if (position.getSettlementDetail().getIsSettlementPosition() == true)
				value = "Y";
			else
				value = "N";
			preparedStatement.setString(n + 17, value);
		} else {
			preparedStatement.setNull(n + 17, Types.CHAR);
		}
		if (position.getPositionDetail().getFixedPriceValue() != null)
			preparedStatement.setBigDecimal(n + 18, position.getPositionDetail().getFixedPriceValue());
		else
			preparedStatement.setNull(n+ 18, Types.DECIMAL);

		if (position.getFixedPriceFxRiskFactorId() != null)
			preparedStatement.setInt(n + 19, position.getFixedPriceFxRiskFactorId().getId());
		else
			preparedStatement.setNull(n+ 19, Types.INTEGER);


		// Error Code
		if (position.getPositionDetail().getErrorCode() != null)
			preparedStatement.setString(n + 20, position.getPositionDetail().getErrorCode());
		else
			preparedStatement.setNull(n + 20, Types.VARCHAR);



	}

	public boolean isAddPrimaryKey() {
		return isAddPrimaryKey;
	}

}
