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
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PositionRiskFactorMappingSqlMapper extends BaseSqlMapper {

	public List<String> getColumnNames() {
		List<String> columnNames = new ArrayList<String>();
		columnNames.add("DEAL_POSITION_ID");
		columnNames.add("PRICE_RISK_FACTOR_ID");
		columnNames.add("FX_RISK_FACTOR_ID");
		columnNames.add("PRICE_TYPE_CODE");
		return columnNames;
	}

	public PositionRiskFactorMappingSqlMapper(boolean isAddPrimaryKey) {
		super(isAddPrimaryKey);
	}

	@Override
	public String getTableName() {
		return "POSITION_RISK_FACTOR_MAP";
	}

	public void setValuesOnPreparedStatement(
			AbstractSnapshot snapshot,
			PreparedStatement preparedStatement) throws SQLException {
		PositionRiskFactorMappingSnapshot mapping = (PositionRiskFactorMappingSnapshot) snapshot;

		preparedStatement.setInt(1, mapping.getDealPositionId().getId());

		preparedStatement.setInt(2, mapping.getPriceRiskFactorId().getId());

		if (mapping.getFxRiskFactorId() != null)
			preparedStatement.setInt(3, mapping.getFxRiskFactorId().getId());
		else
			preparedStatement.setNull(3, Types.INTEGER);

		preparedStatement.setString(4, mapping.getDetail().getPriceTypeCodeValue());

	}

	
}
