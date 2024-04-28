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
package com.onbelay.dealcapture.riskfactor.batch.sql;

import com.onbelay.core.entity.component.ApplicationContextFactory;
import com.onbelay.dealcapture.batch.BatchDefaultInsertWorker;
import com.onbelay.dealcapture.riskfactor.snapshot.FxRiskFactorSnapshot;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class FxRiskFactorDefaultBatchInserter implements FxRiskFactorBatchInserter {
	private static final Logger logger = LogManager.getLogger(FxRiskFactorDefaultBatchInserter.class);

	@Value("${positionBatchSize:20}")
	protected int batchSize;


	public FxRiskFactorDefaultBatchInserter() {
	}
	
	
	@Override
	public void saveRiskFactors(List<FxRiskFactorSnapshot> riskFactors) {
		
		
		EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();


		FxRiskFactorSqlMapper sqlMapper = new FxRiskFactorSqlMapper(false);

		Session session = entityManager.unwrap(Session.class);
		
		try {
			session.doWork(
					new BatchDefaultInsertWorker(
							sqlMapper,
							riskFactors,
							batchSize));
		} catch (RuntimeException t) {
			logger.error("batch insert failed", t);
		}
		
		
	}
}
