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
package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.positions.repository.DealPositionRepository;
import com.onbelay.shared.enums.CurrencyCode;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository (value="dealPositionsRepository")
@Transactional

public class DealPositionRepositoryBean extends BaseRepository<DealPosition> implements DealPositionRepository {
	private static final Logger logger = LogManager.getLogger();
	public static final String FIND_BY_DEAL = "DealPositionsRepository.FIND_BY_DEAL";
	public static final String FIND_IDS_BY_DEAL = "DealPositionsRepository.FIND_IDS_BY_DEAL";
    public static final String FIND_DEAL_POSITION_VIEWS_BY_DEAL ="DealPositionsRepository.FIND_DEAL_POSITION_VIEWS_BY_DEAL" ;
	public static final String FIND_DEAL_POSITION_VIEWS ="DealPositionsRepository.FIND_DEAL_POSITION_VIEWS" ;

	@Autowired
	JdbcTemplate jdbcTemplate;

    @Autowired
	private DealPositionColumnDefinitions dealPositionColumnDefinitions;

	@Override
	public DealPosition load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return find(DealPosition.class, entityId.getId());
		else
			return null;
	}

	@Override
	public long reserveSequenceRange(String sequenceName, int rangeSize) {
		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withProcedureName("sp_sequence_get_range")
				.declareParameters(new SqlParameter("sequence_name", Types.VARCHAR),
									new SqlParameter("range_size", Types.INTEGER),
									new SqlOutParameter("range_first_value", microsoft.sql.Types.SQL_VARIANT),
						new SqlOutParameter("range_last_value", microsoft.sql.Types.SQL_VARIANT),
						new SqlOutParameter("range_cycle_count", Types.INTEGER),
						new SqlOutParameter("sequence_increment", microsoft.sql.Types.SQL_VARIANT),
						new SqlOutParameter("sequence_min_value", microsoft.sql.Types.SQL_VARIANT),
						new SqlOutParameter("sequence_max_value", microsoft.sql.Types.SQL_VARIANT)
						);

		MapSqlParameterSource parmSource = new MapSqlParameterSource();
		parmSource.addValue("sequence_name", sequenceName);
		parmSource.addValue("range_size", rangeSize);
		Map<String, Object> results = simpleJdbcCall.execute(parmSource);
		return (long) results.get("range_first_value");
	}

	@Override
	public List<DealPosition> findByDeal(EntityId dealEntityId) {

		return executeQuery(
				FIND_BY_DEAL,
				"dealId",
				dealEntityId.getId());
	}

	@Override
	public List<DealPositionView> findDealPositionViews(
			List<Integer> dealIds,
			CurrencyCode currencyCode,
			LocalDateTime createdDateTime) {

		String[] names = {"dealIds", "currencyCode", "createdDateTime"};

		if (dealIds.size() < 2000) {
			Object[] parms = {dealIds, currencyCode.getCode(), createdDateTime};

			return (List<DealPositionView>) executeReportQuery(
					FIND_DEAL_POSITION_VIEWS,
					names,
					parms);
		} else {
			ArrayList<DealPositionView> views = new ArrayList<>();
			SubLister<Integer> subLister = new SubLister<>(dealIds, 2000);
			while (subLister.moreElements()) {
				Object[] parmsTwo = {subLister.nextList(), currencyCode.getCode(), createdDateTime};
				views.addAll (
						(Collection<? extends DealPositionView>) executeReportQuery(
							FIND_DEAL_POSITION_VIEWS,
							names,
							parmsTwo));

			}
			return views;
		}
	}

	@Override
	public List<Integer> findIdsByDeal(EntityId dealId) {
		return (List<Integer>) executeReportQuery(
				FIND_IDS_BY_DEAL,
				"dealId",
				dealId);
	}

	@Override
	public List<DealPosition> fetchByIds(QuerySelectedPage querySelectedPage) {
		return fetchEntitiesById(
				dealPositionColumnDefinitions,
				"DealPosition",
				querySelectedPage);
	}

	@Override
	public List<Integer> findPositionIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				dealPositionColumnDefinitions,
				definedQuery);

	}



}
