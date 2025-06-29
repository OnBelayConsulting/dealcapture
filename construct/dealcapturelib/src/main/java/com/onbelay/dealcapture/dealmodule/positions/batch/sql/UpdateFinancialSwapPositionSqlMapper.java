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

import com.onbelay.dealcapture.dealmodule.positions.model.FinancialSwapPositionValuationResult;
import com.onbelay.dealcapture.dealmodule.positions.model.PositionValuationResult;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UpdateFinancialSwapPositionSqlMapper extends UpdateDealPositionSqlMapper {

	public List<String> getColumnNames() {
		List<String> list = new ArrayList<>(super.getColumnNames());

		list.add("PAYS_PRICE");
		list.add("PAYS_INDEX_PRICE");
		list.add("TOTAL_PAYS_PRICE");
		list.add("RECEIVES_PRICE");

		return list;
	}

	public UpdateFinancialSwapPositionSqlMapper() {
	}

	public void setValuesOnPreparedStatement(
			PositionValuationResult valuation,
			PreparedStatement preparedStatement) throws SQLException {

		super.setValuesOnPreparedStatement(
				valuation,
				preparedStatement);

		FinancialSwapPositionValuationResult FinancialSwapPositionValuationResult = (FinancialSwapPositionValuationResult) valuation;

		int n =  getStartingPoint();

		if (FinancialSwapPositionValuationResult.getPriceDetail().getPaysPriceValue() != null)
			preparedStatement.setBigDecimal(n + 1, FinancialSwapPositionValuationResult.getPriceDetail().getPaysPriceValue());
		else
			preparedStatement.setNull(n + 1, Types.DECIMAL);

		if (FinancialSwapPositionValuationResult.getPriceDetail().getPaysIndexPriceValue() != null)
			preparedStatement.setBigDecimal(n + 2, FinancialSwapPositionValuationResult.getPriceDetail().getPaysIndexPriceValue());
		else
			preparedStatement.setNull(n + 2, Types.DECIMAL);


		if (FinancialSwapPositionValuationResult.getPriceDetail().getTotalPaysPriceValue() != null)
			preparedStatement.setBigDecimal(n + 3, FinancialSwapPositionValuationResult.getPriceDetail().getTotalPaysPriceValue());
		else
			preparedStatement.setNull(n + 3, Types.DECIMAL);

		if (FinancialSwapPositionValuationResult.getPriceDetail().getReceivesPriceValue() != null)
			preparedStatement.setBigDecimal(n + 4, FinancialSwapPositionValuationResult.getPriceDetail().getReceivesPriceValue());
		else
			preparedStatement.setNull(n + 4, Types.DECIMAL);

	}

}
