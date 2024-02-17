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
import com.onbelay.dealcapture.dealmodule.deal.enums.DealTypeCode;
import com.onbelay.dealcapture.dealmodule.positions.snapshot.DealPositionSnapshot;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
		
		Session session = entityManager.unwrap(Session.class);
		
		try {
			session.doWork(
					new BatchDealPositionDefaultInsertWorker(
							sqlMappers.get(dealTypeCode)
											.get(),
							positions,
							batchSize));
		} catch (RuntimeException t) {
			logger.error("batch insert failed", t);
		}
		
		
	}


	protected static class BatchDealPositionDefaultInsertWorker implements Work {

		private DealPositionSqlMapper sqlMapper;
		private List<DealPositionSnapshot> positions;
		private int batchSize;

		public BatchDealPositionDefaultInsertWorker(
				DealPositionSqlMapper sqlMapper,
				List<DealPositionSnapshot> positions,
				int batchSize) {

			super();
			this.sqlMapper = sqlMapper;
			this.positions = positions;
			this.batchSize = batchSize;
		}

		@Override
		public void execute(Connection connection) throws SQLException {

			String sqlInsert = "INSERT into " +
					sqlMapper.getTableName() +
					" (" + String.join(",", sqlMapper.getColumnNames()) + ")" +
					" VALUES " + sqlMapper.createPlaceHolders();

			try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

				SubLister<DealPositionSnapshot> subLister = new SubLister<>(positions, batchSize);
				while (subLister.moreElements()) {
					List<DealPositionSnapshot> myList = subLister.nextList();
					for (DealPositionSnapshot position : myList) {
						sqlMapper.setValuesOnPreparedStatement(
								position,
								preparedStatement);
						preparedStatement.addBatch();
					}

					preparedStatement.executeBatch();
					ResultSet resultSet = preparedStatement.getGeneratedKeys();
					int j = 0;
					while (resultSet.next()) {
						BigDecimal id = resultSet.getBigDecimal(1);
						myList.get(j).setEntityId(new EntityId(id.intValue()));
						j++;
					}
				}

			} catch (RuntimeException e) {
				logger.error(e.getMessage());
				throw e;
			}


		}

	}

}
