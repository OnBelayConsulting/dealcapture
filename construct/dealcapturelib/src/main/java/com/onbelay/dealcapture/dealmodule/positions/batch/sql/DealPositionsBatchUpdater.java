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

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.model.PositionValuationResult;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Component(value="dealPositionsBatchUpdater")
@Transactional
public class DealPositionsBatchUpdater {
	private static final Logger logger = LogManager.getLogger(DealPositionsBatchUpdater.class);
	@Value("${positionBatchSize:20}")
	private int batchSize;


	protected Map<DealTypeCode, Supplier<UpdateDealPositionSqlMapper>> sqlMappers = new HashMap<>();

	public DealPositionsBatchUpdater() {
		sqlMappers.put(DealTypeCode.PHYSICAL_DEAL, UpdatePhysicalPositionSqlMapper::new);
		sqlMappers.put(DealTypeCode.FINANCIAL_SWAP, UpdateFinancialSwapPositionSqlMapper::new);
	}


	public void updatePositions(
			DealTypeCode dealTypeCode,
			List<PositionValuationResult> positionValuationResults) {
		
		
		EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();
		
		Session session = entityManager.unwrap(Session.class);

		UpdateSqlMapper updateSqlMapper = sqlMappers.get(dealTypeCode).get();

		try {
			session.doWork(
					new BatchDealPositionUpdateWorker(
							updateSqlMapper,
							positionValuationResults,
							batchSize));
		} catch (RuntimeException t) {
			logger.error("batch update failed", t);
		}
		
		
	}
	

}
