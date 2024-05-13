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

@Component(value="dealPositionsBatchUpdater")
@Transactional
public class DealPositionsBatchUpdater {
	private static final Logger logger = LogManager.getLogger(DealPositionsBatchUpdater.class);
	@Value("${positionBatchSize:20}")
	private int batchSize;

	public DealPositionsBatchUpdater() {
	}
	
	
	
	public void updatePositions(List<PositionValuationResult> positionValuationResults) {
		
		
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
		
		private List<PositionValuationResult> valuationResults;
		private int batchSize;
		
		public BatchDealPositionInsertWorker(
				List<PositionValuationResult> valuationResults,
				int batchSize) {
			
			super();
			this.valuationResults = valuationResults;
			this.batchSize = batchSize;
		}

		@Override
		public void execute(Connection connection) throws SQLException {

			String sqlInsert = "UPDATE DEAL_POSITION "+
					 "SET VALUED_DATETIME = ?, " +
					"     ERROR_CODE = ?," +
					"     ERROR_MSG = ?, " +
					"     MTM_VALUATION = ?, " +
					"     DEAL_PRICE = ?, " +
					"     DEAL_INDEX_PRICE = ?, " +
					"     TOTAL_DEAL_PRICE = ?, " +
					"     MARKET_PRICE = ?, " +
					"     COST_SETTLEMENT_AMOUNT = ?, " +
					"     SETTLEMENT_AMOUNT = ?, " +
					"     TOTAL_SETTLEMENT_AMOUNT = ? " +
					" WHERE ENTITY_ID = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {

				SubLister<PositionValuationResult> subLister = new SubLister<>(valuationResults, batchSize);
				while (subLister.moreElements()) {
					List<PositionValuationResult> myList = subLister.nextList();

					for (PositionValuationResult valuation : myList) {
						preparedStatement.setTimestamp(1, Timestamp.valueOf(valuation.getCurrentDateTime()));
						preparedStatement.setString(2, valuation.getErrorCode().getCode());
						if (valuation.hasErrors())
							preparedStatement.setString(3, valuation.getCompleteErrorCodeMessage());
						else
							preparedStatement.setNull(3, Types.VARCHAR);

						if (valuation.getSettlementDetail().getMarkToMarketValuation() != null)
							preparedStatement.setBigDecimal(4, valuation.getSettlementDetail().getMarkToMarketValuation());
						else
							preparedStatement.setNull(4, Types.DECIMAL);

						if (valuation.getSettlementDetail().getDealPriceValue() != null)
							preparedStatement.setBigDecimal(5, valuation.getSettlementDetail().getDealPriceValue());
						else
							preparedStatement.setNull(5, Types.DECIMAL);

						if (valuation.getSettlementDetail().getDealIndexPriceValue() != null)
							preparedStatement.setBigDecimal(6, valuation.getSettlementDetail().getDealIndexPriceValue());
						else
							preparedStatement.setNull(6, Types.DECIMAL);

						if (valuation.getSettlementDetail().getTotalDealPriceValue() != null)
							preparedStatement.setBigDecimal(7, valuation.getSettlementDetail().getTotalDealPriceValue());
						else
							preparedStatement.setNull(7, Types.DECIMAL);

						if (valuation.getSettlementDetail().getMarketPriceValue() != null)
							preparedStatement.setBigDecimal(8, valuation.getSettlementDetail().getMarketPriceValue());
						else
							preparedStatement.setNull(8, Types.DECIMAL);

						if (valuation.getSettlementDetail().getCostSettlementAmount() != null)
							preparedStatement.setBigDecimal(9, valuation.getSettlementDetail().getCostSettlementAmount());
						else
							preparedStatement.setNull(9, Types.DECIMAL);

						if (valuation.getSettlementDetail().getSettlementAmount() != null)
							preparedStatement.setBigDecimal(10, valuation.getSettlementDetail().getSettlementAmount());
						else
							preparedStatement.setNull(10, Types.DECIMAL);

						if (valuation.getSettlementDetail().getTotalSettlementAmount() != null)
							preparedStatement.setBigDecimal(11, valuation.getSettlementDetail().getTotalSettlementAmount());
						else
							preparedStatement.setNull(11, Types.DECIMAL);

						preparedStatement.setInt(12, valuation.getDomainId());
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
