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
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.positions.model.CostPositionValuationResult;
import com.onbelay.dealcapture.riskfactor.valuatorimpl.PriceRiskFactorEvaluationResult;
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

@Component(value="priceRiskFactorBatchUpdater")
@Transactional
public class PriceRiskFactorBatchUpdater {
	private static final Logger logger = LogManager.getLogger(PriceRiskFactorBatchUpdater.class);
	@Value("${positionBatchSize:20}")
	private int batchSize;

	public PriceRiskFactorBatchUpdater() {
	}
	
	
	
	public void updatePositions(List<PriceRiskFactorEvaluationResult> valuationResults) {
		
		
		EntityManager entityManager = ApplicationContextFactory.getCurrentEntityManagerOnThread();
		
		Session session = entityManager.unwrap(Session.class);
		
		try {
			session.doWork(
					new BatchInsertWorker(
							valuationResults,
							batchSize));
		} catch (RuntimeException t) {
			logger.error("batch insert failed", t);
		}
		
		
	}
	
	
	public static class BatchInsertWorker implements Work {
		
		private List<PriceRiskFactorEvaluationResult> valuationResults;
		private int batchSize;
		
		public BatchInsertWorker(
				List<PriceRiskFactorEvaluationResult> valuationResults,
				int batchSize) {
			
			super();
			this.valuationResults = valuationResults;
			this.batchSize = batchSize;
		}

		@Override
		public void execute(Connection connection) throws SQLException {

			String sqlInsert = "UPDATE PRICE_RISK_FACTOR "+
					 "SET CREATE_UPDATE_DATETIME = ?, " +
					"     MARKET_VALUE = ? " +
					" WHERE ENTITY_ID = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {

				SubLister<PriceRiskFactorEvaluationResult> subLister = new SubLister<>(valuationResults, batchSize);
				while (subLister.moreElements()) {
					List<PriceRiskFactorEvaluationResult> myList = subLister.nextList();

					for (PriceRiskFactorEvaluationResult valuation : myList) {
						preparedStatement.setTimestamp(1, Timestamp.valueOf(valuation.getCurrentDateTime()));

						if (valuation.getPrice() != null || valuation.getPrice().isInError() == false)
							preparedStatement.setBigDecimal(2, valuation.getPrice().getValue());
						else
							preparedStatement.setNull(2, Types.DECIMAL);


						preparedStatement.setInt(3, valuation.getDomainId());
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
