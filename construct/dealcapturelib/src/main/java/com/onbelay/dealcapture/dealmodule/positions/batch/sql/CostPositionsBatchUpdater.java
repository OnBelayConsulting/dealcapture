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
import com.onbelay.dealcapture.dealmodule.positions.model.CostPositionValuationResult;
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
import java.util.List;

@Component(value="costPositionsBatchUpdater")
@Transactional
public class CostPositionsBatchUpdater {
	private static final Logger logger = LogManager.getLogger(CostPositionsBatchUpdater.class);
	@Value("${positionBatchSize:20}")
	private int batchSize;

	public CostPositionsBatchUpdater() {
	}
	
	
	
	public void updatePositions(List<CostPositionValuationResult> positionValuationResults) {
		
		
		EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();
		
		Session session = entityManager.unwrap(Session.class);
		
		try {
			session.doWork(
					new BatchDealPositionInsertWorker(
							positionValuationResults,
							batchSize));
		} catch (RuntimeException t) {
			logger.error("batch insert failed", t);
		}
		
		
	}
	
	
	public static class BatchDealPositionInsertWorker implements Work {
		
		private List<CostPositionValuationResult> valuationResults;
		private int batchSize;
		
		public BatchDealPositionInsertWorker(
				List<CostPositionValuationResult> valuationResults,
				int batchSize) {
			
			super();
			this.valuationResults = valuationResults;
			this.batchSize = batchSize;
		}

		@Override
		public void execute(Connection connection) throws SQLException {

			String sqlInsert = "UPDATE COST_POSITION "+
					 "SET VALUED_DATETIME = ?, " +
					"     ERROR_CODE = ?," +
					"     COST_AMOUNT = ? " +
					" WHERE ENTITY_ID = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {

				SubLister<CostPositionValuationResult> subLister = new SubLister<>(valuationResults, batchSize);
				while (subLister.moreElements()) {
					List<CostPositionValuationResult> myList = subLister.nextList();

					for (CostPositionValuationResult valuation : myList) {
						preparedStatement.setTimestamp(1, Timestamp.valueOf(valuation.getCurrentDateTime()));
						preparedStatement.setString(2, valuation.getCompleteErrorCodeMessage());

						if (valuation.getCostAmount() != null)
							preparedStatement.setBigDecimal(3, valuation.getCostAmount());
						else
							preparedStatement.setNull(3, Types.DECIMAL);


						preparedStatement.setInt(4, valuation.getPositionId());
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
