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
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class PositionRiskFactorMappingDefaultBatchInserter implements  PositionRiskFactorMappingBatchInserter {
	private static final Logger logger = LogManager.getLogger(PositionRiskFactorMappingDefaultBatchInserter.class);
	
	public PositionRiskFactorMappingDefaultBatchInserter() {
	}
	
	
	
	public void savePositionRiskFactorMappings(List<PositionRiskFactorMappingSnapshot> mappings) {
		
		
		EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();
		
		Session session = entityManager.unwrap(Session.class);
		PositionRiskFactorMappingSqlMapper sqlMapper = new PositionRiskFactorMappingSqlMapper(false);
		try {
			session.doWork(
					new BatchDefaultInsertWorker(
							sqlMapper,
							mappings,
							10));
		} catch (RuntimeException t) {
			logger.error("batch insert failed", t);
		}
		
		
	}
	

}
