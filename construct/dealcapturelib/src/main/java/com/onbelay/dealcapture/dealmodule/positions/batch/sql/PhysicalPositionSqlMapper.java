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
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PhysicalPositionSqlMapper extends DealPositionSqlMapper {

	public PhysicalPositionSqlMapper(Boolean isAddPrimaryKey) {
		super(isAddPrimaryKey);
	}

	public List<String> getColumnNames() {
		List<String> columnNames = new ArrayList<>(super.getColumnNames());
		columnNames.add("DEAL_PRICE_VALUATION_CODE");
		columnNames.add("FIXED_PRICE");
		columnNames.add("FIXED_PRICE_CURRENCY_CODE");
		columnNames.add("FIXED_PRICE_UOM_CODE");
		columnNames.add("FIXED_PRICE_FX_RISK_FACTOR_ID");
		columnNames.add("DEAL_PRICE_RISK_FACTOR_ID");
		columnNames.add("DEAL_PRICE_FX_RISK_FACTOR_ID");
		columnNames.add("MARKET_VALUATION_CODE");
		columnNames.add("MKT_PRICE_RISK_FACTOR_ID");
		columnNames.add("MKT_PRICE_FX_RISK_FACTOR_ID");
		return columnNames;
	}

	public void setValuesOnPreparedStatement(
			DealPositionSnapshot position,
			PreparedStatement preparedStatement) throws SQLException {

		super.setValuesOnPreparedStatement(position, preparedStatement);

		PhysicalPositionSnapshot physicalPosition = (PhysicalPositionSnapshot) position;
		int n = getStartingPoint();

		// 12
		preparedStatement.setString(n + 1, physicalPosition.getDetail().getDealPriceValuationValue());

		if (physicalPosition.getDetail().getFixedPriceValue() != null)
			preparedStatement.setBigDecimal(n +2, physicalPosition.getDetail().getFixedPriceValue());
		else
			preparedStatement.setNull(n + 2, Types.DECIMAL);

		if (physicalPosition.getDetail().getFixedPriceCurrencyCodeValue() != null)
			preparedStatement.setString(n + 3, physicalPosition.getDetail().getFixedPriceCurrencyCodeValue());
		else
			preparedStatement.setNull(n + 3, Types.VARCHAR);

		if (physicalPosition.getDetail().getFixedPriceUnitOfMeasureCodeValue() != null)
			preparedStatement.setString(n + 4, physicalPosition.getDetail().getFixedPriceUnitOfMeasureCodeValue());
		else
			preparedStatement.setNull(n + 4, Types.VARCHAR);

		if (physicalPosition.getFixedPriceFxRiskFactorId() != null)
			preparedStatement.setInt(n + 5, physicalPosition.getFixedPriceFxRiskFactorId().getId());
		else
			preparedStatement.setNull(n+ 5, Types.INTEGER);

		if (physicalPosition.getDealPriceRiskFactorId() != null)
			preparedStatement.setInt(n+ 6, physicalPosition.getDealPriceRiskFactorId().getId());
		else
			preparedStatement.setNull(n+ 6, Types.INTEGER);

		if (physicalPosition.getDealPriceFxRiskFactorId() != null)
			preparedStatement.setInt(n+7, physicalPosition.getDealPriceFxRiskFactorId().getId());
		else
			preparedStatement.setNull(n +7, Types.INTEGER);

		preparedStatement.setString(n+8, physicalPosition.getDetail().getMarketPriceValuationValue());

		if (physicalPosition.getMarketPriceRiskFactorId() != null)
			preparedStatement.setInt(n + 9, physicalPosition.getMarketPriceRiskFactorId().getId());
		else
			preparedStatement.setNull(n + 9, Types.INTEGER);

		if (physicalPosition.getMarketPriceFxRiskFactorId() != null)
			preparedStatement.setInt(n +10, physicalPosition.getMarketPriceFxRiskFactorId().getId());
		else
			preparedStatement.setNull(n + 10, Types.INTEGER);

	}

	
}
