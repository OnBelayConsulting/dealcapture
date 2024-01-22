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
package com.onbelay.dealcapture.dealmodule.deal.model;

import java.util.List;

import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import jakarta.transaction.Transactional;

import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;

@Repository (value="dealRepository")
@Transactional

public class DealRepositoryBean extends BaseRepository<BaseDeal> implements DealRepository {
	public static final String FETCH_ALL_DEALS = "DealRepository.FETCH_ALL_DEALS";
	public static final String FETCH_DEAL_SUMMARIES = "DealRepository.FETCH_DEAL_SUMMARIES";
	public static final String FIND_DEAL_BY_TICKET_NO = "DealRepository.FIND_DEAL_BY_TICKET_NO";
	public static final String GET_DEAL_SUMMARY = "DealRepository.GET_DEAL_SUMMARY";

	private static final String UPDATE_POSITION_GENERATION_STATUS_STATEMENT
			= "UPDATE BaseDeal  SET dealDetail.positionGenerationStatusValue = :status, " +
			" dealDetail.positionGenerationIdentifier = :identifier " +
			"  WHERE id = :dealId " +
			"   AND dealDetail.positionGenerationStatusValue in ('Completed', 'Cancelled', 'None')";

	@Autowired
	private DealColumnDefinitions dealColumnDefinitions;


	public boolean executeUpdateOfPositionGenerationStatus(
			Integer dealId,
			String positionGeneratorId,
			PositionGenerationStatusCode code) {

		String[] names = {"identifier", "status", "dealId"};
		Object[] parms = {positionGeneratorId, code.getCode(), dealId};

		int result = executeUpdate(
				UPDATE_POSITION_GENERATION_STATUS_STATEMENT,
				names,
				parms);

		return result > 0;
	}

	/*
	 * Defined in bean only for testing.
	 */
	public List<BaseDeal> fetchAllDeals() {
		return executeQuery(FETCH_ALL_DEALS);
	}
	
	@Override
	public List<BaseDeal> findByQuery(DefinedQuery definedQuery) {
		
		return  executeDefinedQuery(dealColumnDefinitions, definedQuery);
	}

	public List<DealSummary> fetchDealSummaries() {
		return (List<DealSummary>) executeReportQuery(FETCH_DEAL_SUMMARIES);
	}

	@Override
	public BaseDeal load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return (BaseDeal) find(BaseDeal.class, entityId.getId());
		else if (entityId.getCode() != null)
			return findDealByTicketNo(entityId.getCode());
		else
			return null;
	}

	public DealSummary getDealSummary(EntityId id) {
		return (DealSummary) executeSingleResultReportQuery(
				GET_DEAL_SUMMARY,
				"dealId",
				id.getId());
	}

	public BaseDeal findDealByTicketNo(String ticketNo) {
		return (BaseDeal) executeSingleResultQuery(
				FIND_DEAL_BY_TICKET_NO, 
				"ticketNo", 
				ticketNo);
	}


	@Override
	public List<BaseDeal> fetchByIds(QuerySelectedPage querySelectedPage) {
		return fetchEntitiesById(
				dealColumnDefinitions,
				"BaseDeal",
				querySelectedPage);
	}

	@Override
	public List<Integer> findDealIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				dealColumnDefinitions, 
				definedQuery);
	
	}


}
