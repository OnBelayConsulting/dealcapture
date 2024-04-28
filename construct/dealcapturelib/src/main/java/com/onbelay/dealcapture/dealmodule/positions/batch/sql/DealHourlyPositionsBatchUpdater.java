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
import com.onbelay.dealcapture.dealmodule.positions.model.HourlyPositionValuationResult;
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

@Component(value="dealHourlyPositionsBatchUpdater")
@Transactional
public class DealHourlyPositionsBatchUpdater {
	private static final Logger logger = LogManager.getLogger(DealHourlyPositionsBatchUpdater.class);
	@Value("${positionBatchSize:20}")
	private int batchSize;

	public DealHourlyPositionsBatchUpdater() {
	}
	
	
	
	public void updatePositions(List<HourlyPositionValuationResult> positionValuationResults) {
		
		
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
		
		private List<HourlyPositionValuationResult> valuationResults;
		private int batchSize;
		
		public BatchDealPositionInsertWorker(
				List<HourlyPositionValuationResult> valuationResults,
				int batchSize) {
			
			super();
			this.valuationResults = valuationResults;
			this.batchSize = batchSize;
		}

		@Override
		public void execute(Connection connection) throws SQLException {

			String sqlInsert = createSQLUpdateString();

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert)) {

				SubLister<HourlyPositionValuationResult> subLister = new SubLister<>(valuationResults, batchSize);
				while (subLister.moreElements()) {
					List<HourlyPositionValuationResult> myList = subLister.nextList();

					for (HourlyPositionValuationResult valuation : myList) {
						preparedStatement.setTimestamp(1, Timestamp.valueOf(valuation.getCurrentDateTime()));
						preparedStatement.setString(2, valuation.getErrorCode().getCode());

						if (valuation.hasErrors())
							preparedStatement.setString(3, valuation.getCompleteErrorCodeMessage());
						else
							preparedStatement.setNull(3, Types.VARCHAR);

						int start = 4;
						for (int i=1; i<25; i++) {
							if (valuation.getPrices().getHourFixedValue(i) != null)
								preparedStatement.setBigDecimal(start, valuation.getPrices().getHourFixedValue(i));
							else
								preparedStatement.setNull(start, Types.DECIMAL);
							start++;
						}


						preparedStatement.setInt(start, valuation.getDomainId());
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

	private static String createSQLUpdateString() {
		StringBuilder builder = new StringBuilder("UPDATE DEAL_HOURLY_POSITION ");
		builder.append("SET VALUED_DATETIME = ?, ");
		builder.append(" ERROR_CODE = ?, ");
		builder.append(" ERROR_MSG = ?, ");

		for (int i=1; i< 24; i++) {
			builder.append(" HOUR_" );
			builder.append(i);
			builder.append("_VALUE " );
			builder.append(" = ?, ");
		}
		builder.append(" HOUR_24_VALUE" );
		builder.append(" = ? ");


		builder.append(" WHERE ENTITY_ID = ?");
		return builder.toString();
	}


}
