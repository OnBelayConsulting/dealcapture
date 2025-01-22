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
import com.onbelay.dealcapture.dealmodule.positions.model.VanillaOptionPositionValuationResult;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UpdateVanillaOptionPositionSqlMapper extends UpdateDealPositionSqlMapper {

	public List<String> getColumnNames() {
		List<String> list = new ArrayList<>(super.getColumnNames());

		list.add("UNDERLYING_PRICE");
		list.add("STRIKE_PRICE");

		return list;
	}

	public UpdateVanillaOptionPositionSqlMapper() {
	}

	public void setValuesOnPreparedStatement(
			PositionValuationResult valuation,
			PreparedStatement preparedStatement) throws SQLException {

		super.setValuesOnPreparedStatement(
				valuation,
				preparedStatement);

		VanillaOptionPositionValuationResult physicalPositionValuationResult = (VanillaOptionPositionValuationResult) valuation;

		int n =  getStartingPoint();

		if (physicalPositionValuationResult.getPriceDetail().getUnderlyingPriceValue() != null)
			preparedStatement.setBigDecimal(n + 1, physicalPositionValuationResult.getPriceDetail().getUnderlyingPriceValue());
		else
			preparedStatement.setNull(n + 1, Types.DECIMAL);

		if (physicalPositionValuationResult.getPriceDetail().getStrikePriceValue() != null)
			preparedStatement.setBigDecimal(n + 2, physicalPositionValuationResult.getPriceDetail().getStrikePriceValue());
		else
			preparedStatement.setNull(n + 2, Types.DECIMAL);

	}

}
