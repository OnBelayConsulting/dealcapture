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
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.batch.BatchDefaultInsertWorker;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealHourlyPositionSnapshot;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

@Transactional
public class DealHourlyPositionsDefaultBatchInserter implements DealHourlyPositionsBatchInserter {
	private static final Logger logger = LogManager.getLogger(DealHourlyPositionsDefaultBatchInserter.class);

	@Value("${positionBatchSize:20}")
	protected int batchSize;


	public DealHourlyPositionsDefaultBatchInserter() {
	}
	
	
	@Override
	public void savePositions(List<DealHourlyPositionSnapshot> positions) {
		
		
		EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();


		DealHourlyPositionSqlMapper sqlMapper = new DealHourlyPositionSqlMapper(false);

		Session session = entityManager.unwrap(Session.class);
		
		try {
			session.doWork(
					new BatchDefaultInsertWorker(
							sqlMapper,
							positions,
							batchSize));
		} catch (RuntimeException t) {
			logger.error("batch insert failed", t);
		}
		
		
	}


}
