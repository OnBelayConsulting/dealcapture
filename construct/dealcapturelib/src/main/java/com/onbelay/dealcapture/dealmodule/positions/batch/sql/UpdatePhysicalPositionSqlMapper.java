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

import com.onbelay.dealcapture.dealmodule.positions.model.PhysicalPositionValuationResult;
import com.onbelay.dealcapture.dealmodule.positions.model.PositionValuationResult;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UpdatePhysicalPositionSqlMapper extends UpdateDealPositionSqlMapper {

	public List<String> getColumnNames() {
		List<String> list = new ArrayList<>(super.getColumnNames());

		list.add("DEAL_PRICE");
		list.add("DEAL_INDEX_PRICE");
		list.add("TOTAL_DEAL_PRICE");
		list.add("MARKET_PRICE");

		return list;
	}

	public UpdatePhysicalPositionSqlMapper() {
	}

	public void setValuesOnPreparedStatement(
			PositionValuationResult valuation,
			PreparedStatement preparedStatement) throws SQLException {

		super.setValuesOnPreparedStatement(
				valuation,
				preparedStatement);

		PhysicalPositionValuationResult physicalPositionValuationResult = (PhysicalPositionValuationResult) valuation;

		int n =  getStartingPoint();

		if (physicalPositionValuationResult.getPriceDetail().getDealPriceValue() != null)
			preparedStatement.setBigDecimal(n + 1, physicalPositionValuationResult.getPriceDetail().getDealPriceValue());
		else
			preparedStatement.setNull(n + 1, Types.DECIMAL);

		if (physicalPositionValuationResult.getPriceDetail().getDealIndexPriceValue() != null)
			preparedStatement.setBigDecimal(n + 2, physicalPositionValuationResult.getPriceDetail().getDealIndexPriceValue());
		else
			preparedStatement.setNull(n + 2, Types.DECIMAL);


		if (physicalPositionValuationResult.getPriceDetail().getTotalDealPriceValue() != null)
			preparedStatement.setBigDecimal(n + 3, physicalPositionValuationResult.getPriceDetail().getTotalDealPriceValue());
		else
			preparedStatement.setNull(n + 3, Types.DECIMAL);

		if (physicalPositionValuationResult.getPriceDetail().getMarketPriceValue() != null)
			preparedStatement.setBigDecimal(n + 4, physicalPositionValuationResult.getPriceDetail().getMarketPriceValue());
		else
			preparedStatement.setNull(n + 4, Types.DECIMAL);

	}

}
