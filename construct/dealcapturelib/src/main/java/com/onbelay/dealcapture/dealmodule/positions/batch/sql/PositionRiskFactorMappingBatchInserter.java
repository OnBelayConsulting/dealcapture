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
import com.onbelay.dealcapture.dealmodule.positions.snapshot.PositionRiskFactorMappingSnapshot;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component(value="positionRiskFactorMappingBatchInserter")
@Transactional
public class PositionRiskFactorMappingBatchInserter {
	private static final Logger logger = LogManager.getLogger(PositionRiskFactorMappingBatchInserter.class);
	
	public PositionRiskFactorMappingBatchInserter() {
	}
	
	
	
	public void savePositionRiskFactorMappings(
			List<PositionRiskFactorMappingSnapshot> mappings) {
		
		
		EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();
		
		Session session = entityManager.unwrap(Session.class);
		PositionRiskFactorMappingSqlMapper sqlMapper = new PositionRiskFactorMappingSqlMapper();
		try {
			session.doWork(
					new BatchPositionMappingInsertWorker(
							sqlMapper,
							mappings,
							10));
		} catch (RuntimeException t) {
			logger.error("batch insert failed", t);
		}
		
		
	}
	
	
	public static class BatchPositionMappingInsertWorker implements Work {
		
		private PositionRiskFactorMappingSqlMapper sqlMapper;
		private List<PositionRiskFactorMappingSnapshot> mappings;
		private int batchSize;
		
		public BatchPositionMappingInsertWorker(
				PositionRiskFactorMappingSqlMapper sqlMapper,
				List<PositionRiskFactorMappingSnapshot> mappings,
				int batchSize) {
			
			super();
			this.sqlMapper = sqlMapper;
			this.mappings = mappings;
			this.batchSize = batchSize;
		}

		@Override
		public void execute(Connection connection) throws SQLException {

			String sqlInsert = "INSERT into " +
					sqlMapper.getTableName() +
					" (" + String.join(",", sqlMapper.getColumnNames()) + ")" +
					" VALUES " + sqlMapper.createPlaceHolders();

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {

				SubLister<PositionRiskFactorMappingSnapshot> subLister = new SubLister<>(mappings, 10);
				while (subLister.moreElements()) {
					List<PositionRiskFactorMappingSnapshot> myList = subLister.nextList();
					for (PositionRiskFactorMappingSnapshot mapping : myList) {
						sqlMapper.setValuesOnPreparedStatement(
								mapping,
								preparedStatement);
						preparedStatement.addBatch();
					}

					preparedStatement.executeBatch();
				}

			} catch (RuntimeException e) {
				logger.error(e.getMessage());
				throw e;
			}
				
			
		}
		
	}
	
}
