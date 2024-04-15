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
import com.onbelay.dealcapture.dealmodule.positions.model.PowerProfilePositionValuationResult;
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

@Component(value="powerProfilePositionsBatchUpdater")
@Transactional
public class PowerProfilePositionsBatchUpdater {
	private static final Logger logger = LogManager.getLogger(PowerProfilePositionsBatchUpdater.class);
	@Value("${positionBatchSize:20}")
	private int batchSize;

	public PowerProfilePositionsBatchUpdater() {
	}
	
	
	
	public void updatePositions(List<PowerProfilePositionValuationResult> positionValuationResults) {
		
		
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
		
		private List<PowerProfilePositionValuationResult> valuationResults;
		private int batchSize;
		
		public BatchDealPositionInsertWorker(
				List<PowerProfilePositionValuationResult> valuationResults,
				int batchSize) {
			
			super();
			this.valuationResults = valuationResults;
			this.batchSize = batchSize;
		}

		@Override
		public void execute(Connection connection) throws SQLException {


			try (PreparedStatement preparedStatement = connection.prepareStatement(createSQLUpdateString())) {

				SubLister<PowerProfilePositionValuationResult> subLister = new SubLister<>(valuationResults, batchSize);
				while (subLister.moreElements()) {
					List<PowerProfilePositionValuationResult> myList = subLister.nextList();

					for (PowerProfilePositionValuationResult valuation : myList) {
						preparedStatement.setTimestamp(1, Timestamp.valueOf(valuation.getCurrentDateTime()));
						preparedStatement.setString(2, valuation.getErrorCode().getCode());

						if (valuation.hasErrors())
							preparedStatement.setString(3, valuation.getCompleteErrorCodeMessage());
						else
							preparedStatement.setNull(3, Types.VARCHAR);

						int start = 4;
						for (int i=1; i<25; i++) {
							if (valuation.getHourPriceDayDetail().getHourPrice(i) != null)
								preparedStatement.setBigDecimal(start, valuation.getHourPriceDayDetail().getHourPrice(i));
							else
								preparedStatement.setNull(start, Types.DECIMAL);
							start++;
						}


						preparedStatement.setInt(start, valuation.getPositionId());
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
		StringBuilder builder = new StringBuilder("UPDATE POWER_PROFILE_POSITION ");
		builder.append("SET VALUED_DATETIME = ?, ");
		builder.append(" ERROR_CODE = ?, ");
		builder.append(" ERROR_MSG = ?, ");

		for (int i=1; i< 24; i++) {
			builder.append(" HOUR_" );
			builder.append(i);
			builder.append("_PRICE " );
			builder.append(" = ?, ");
		}
		builder.append(" HOUR_24_PRICE" );
		builder.append(" = ? ");


		builder.append(" WHERE ENTITY_ID = ?");
		return builder.toString();
	}
	
}
