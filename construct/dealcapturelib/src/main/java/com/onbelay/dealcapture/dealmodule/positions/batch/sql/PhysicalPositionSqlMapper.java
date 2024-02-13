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

	public List<String> getColumnNames() {
		List<String> columnNames = new ArrayList<>(COL_NAMES);
		columnNames.add("DEAL_PRICE_VALUATION_CODE");
		columnNames.add("DEAL_PRICE");
		columnNames.add("DEAL_PRICE_CURRENCY_CODE");
		columnNames.add("DEAL_PRICE_UOM_CODE");
		columnNames.add("DEAL_PRICE_UOM_CONVERSION");
		columnNames.add("FIXED_PRICE_FX_RISK_FACTOR_ID");
		columnNames.add("DEAL_PRICE_RISK_FACTOR_ID");
		columnNames.add("DEAL_PRICE_FX_RISK_FACTOR_ID");
		columnNames.add("MARKET_VALUATION_CODE");
		columnNames.add("MKT_PRICE_RISK_FACTOR_ID");
		columnNames.add("MKT_PRICE_FX_RISK_FACTOR_ID");
		columnNames.add("MKT_PRICE_UOM_CONVERSION");
		return columnNames;
	}

	public void setValuesOnPreparedStatement(
			DealPositionSnapshot position,
			PreparedStatement preparedStatement) throws SQLException {

		super.setValuesOnPreparedStatement(position, preparedStatement);

		PhysicalPositionSnapshot physicalPosition = (PhysicalPositionSnapshot) position;

		preparedStatement.setString(12, physicalPosition.getDetail().getDealPriceValuationValue());

		if (physicalPosition.getDetail().getFixedPriceValue() != null)
			preparedStatement.setBigDecimal(13, physicalPosition.getDetail().getFixedPriceValue());
		else
			preparedStatement.setNull(13, Types.DECIMAL);

		if (physicalPosition.getDetail().getFixedPriceCurrencyCodeValue() != null)
			preparedStatement.setString(14, physicalPosition.getDetail().getFixedPriceCurrencyCodeValue());
		else
			preparedStatement.setNull(14, Types.VARCHAR);

		if (physicalPosition.getDetail().getFixedPriceUnitOfMeasureCodeValue() != null)
			preparedStatement.setString(15, physicalPosition.getDetail().getFixedPriceUnitOfMeasureCodeValue());
		else
			preparedStatement.setNull(15, Types.VARCHAR);

		if (physicalPosition.getDetail().getDealPriceUOMConversion() != null)
			preparedStatement.setBigDecimal(16, physicalPosition.getDetail().getDealPriceUOMConversion());
		else
			preparedStatement.setNull(16, Types.DECIMAL);

		if (physicalPosition.getFixedPriceFxRiskFactorId() != null)
			preparedStatement.setInt(17, physicalPosition.getFixedPriceFxRiskFactorId().getId());
		else
			preparedStatement.setNull(17, Types.INTEGER);

		if (physicalPosition.getDealPriceRiskFactorId() != null)
			preparedStatement.setInt(18, physicalPosition.getDealPriceRiskFactorId().getId());
		else
			preparedStatement.setNull(18, Types.INTEGER);

		if (physicalPosition.getDealPriceFxRiskFactorId() != null)
			preparedStatement.setInt(19, physicalPosition.getDealPriceFxRiskFactorId().getId());
		else
			preparedStatement.setNull(19, Types.INTEGER);

		preparedStatement.setString(20, physicalPosition.getDetail().getMarketPriceValuationValue());

		if (physicalPosition.getMarketPriceRiskFactorId() != null)
			preparedStatement.setInt(21, physicalPosition.getMarketPriceRiskFactorId().getId());
		else
			preparedStatement.setNull(21, Types.INTEGER);

		if (physicalPosition.getMarketPriceFxRiskFactorId() != null)
			preparedStatement.setInt(22, physicalPosition.getMarketPriceFxRiskFactorId().getId());
		else
			preparedStatement.setNull(22, Types.INTEGER);

		if (physicalPosition.getDetail().getMarketPriceUOMConversion() != null)
			preparedStatement.setBigDecimal(23, physicalPosition.getDetail().getMarketPriceUOMConversion());
		else
			preparedStatement.setNull(23, Types.DECIMAL);



	}

	
}
