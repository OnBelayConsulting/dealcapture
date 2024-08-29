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
import com.onbelay.dealcapture.dealmodule.positions.snapshot.FinancialSwapPositionSnapshot;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PhysicalPositionSnapshot;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class FinancialSwapPositionSqlMapper extends DealPositionSqlMapper {

	public FinancialSwapPositionSqlMapper(Boolean isAddPrimaryKey) {
		super(isAddPrimaryKey);
	}

	public List<String> getColumnNames() {
		List<String> columnNames = new ArrayList<>(super.getColumnNames());
		columnNames.add("PAYS_PRICE_RISK_FACTOR_ID");
		columnNames.add("PAYS_PRICE_FX_RISK_FACTOR_ID");
		columnNames.add("RECEIVES_PRICE_RISK_FACTOR_ID");
		columnNames.add("RECEIVES_PRICE_FX_RISK_FACTOR_ID");
		return columnNames;
	}

	public void setValuesOnPreparedStatement(
			AbstractSnapshot snapshot,
			PreparedStatement preparedStatement) throws SQLException {
		super.setValuesOnPreparedStatement(snapshot, preparedStatement);

		FinancialSwapPositionSnapshot swapPosition = (FinancialSwapPositionSnapshot) snapshot;
		int n = getStartingPoint();

		if (swapPosition.getPaysPriceRiskFactorId() != null)
			preparedStatement.setInt(n+ 1, swapPosition.getPaysPriceRiskFactorId().getId());
		else
			preparedStatement.setNull(n+ 1, Types.INTEGER);

		if (swapPosition.getPaysFxRiskFactorId() != null)
			preparedStatement.setInt(n+2, swapPosition.getPaysFxRiskFactorId().getId());
		else
			preparedStatement.setNull(n +2, Types.INTEGER);


		if (swapPosition.getReceivesPriceRiskFactorId() != null)
			preparedStatement.setInt(n + 3, swapPosition.getReceivesPriceRiskFactorId().getId());
		else
			preparedStatement.setNull(n + 3, Types.INTEGER);

		if (swapPosition.getReceivesFxRiskFactorId() != null)
			preparedStatement.setInt(n +4, swapPosition.getReceivesFxRiskFactorId().getId());
		else
			preparedStatement.setNull(n +4, Types.INTEGER);

	}

	
}
