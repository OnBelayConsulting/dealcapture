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
import com.onbelay.dealcapture.batch.BatchDefaultInsertWorker;
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class DealPositionsDefaultBatchInserter extends DealPositionsBaseBatchInserter implements DealPositionsBatchInserter {
	private static final Logger logger = LogManager.getLogger(DealPositionsDefaultBatchInserter.class);

	public DealPositionsDefaultBatchInserter() {
	}
	
	
	@Override
	public void savePositions(
			DealTypeCode dealTypeCode,
			List<DealPositionSnapshot> positions) {
		
		
		EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();

		DealPositionSqlMapper sqlMapper = sqlMappers.get(dealTypeCode).apply(false);

		Session session = entityManager.unwrap(Session.class);
		
		try {
			session.doWork(
					new BatchDefaultInsertWorker(
							sqlMapper,
							positions,
							batchSize));
		} catch (RuntimeException t) {
			logger.error("batch insert failed", t);
			throw t;
		}
		
		
	}


}
