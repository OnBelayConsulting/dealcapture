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

import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class DealPositionsBaseBatchInserter implements DealPositionsBatchInserter {
	private static final Logger logger = LogManager.getLogger(DealPositionsBaseBatchInserter.class);

	@Value("${positionBatchSize:20}")
	protected int batchSize;

	protected Map<DealTypeCode, Function<Boolean, DealPositionSqlMapper>> sqlMappers = new HashMap<>();

	public DealPositionsBaseBatchInserter() {
		sqlMappers.put(DealTypeCode.PHYSICAL_DEAL, PhysicalPositionSqlMapper::new);
		sqlMappers.put(DealTypeCode.FINANCIAL_SWAP, FinancialSwapPositionSqlMapper::new);
	}
	

}
