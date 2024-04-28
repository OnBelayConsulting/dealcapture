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

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.PhysicalDealSummary;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository (value="dealRepository")
@Transactional

public class DealRepositoryBean extends BaseRepository<BaseDeal> implements DealRepository {
	public static final String FETCH_ALL_DEALS = "DealRepository.FETCH_ALL_DEALS";
	public static final String FETCH_ASSIGNED_DEAL_SUMMARIES = "DealRepository.FETCH_ASSIGNED_DEAL_SUMMARIES";
	public static final String FIND_DEAL_BY_TICKET_NO = "DealRepository.FIND_DEAL_BY_TICKET_NO";
	public static final String GET_DEAL_SUMMARY = "DealRepository.GET_DEAL_SUMMARY";
    public static final String FETCH_PHYSICAL_DEAL_SUMMARIES = "DealRepository.FETCH_PHYSICAL_DEAL_SUMMARIES" ;

    private static final String UPDATE_DEAL_POSITION_GENERATION_STATUS
			= "UPDATE BaseDeal  " +
			"     SET dealDetail.positionGenerationStatusValue = 'Pending', " +
			"         dealDetail.positionGenerationIdentifier = null " +
			"   WHERE id in (:dealIds) " +
			"     AND dealDetail.positionGenerationStatusValue != 'Pending'";

	private static final String UPDATE_DEAL_POSITION_GENERATION_STATUS_TO_COMPLETE
			= "UPDATE BaseDeal  " +
			"     SET dealDetail.positionGenerationStatusValue = 'Complete', " +
			"         dealDetail.positionGenerationDateTime = :updateDateTime " +
			"   WHERE id in (:dealIds) " +
			"     AND dealDetail.positionGenerationStatusValue = 'Generating'";


	private static final String UPDATE_DEAL_POSITION_GENERATION_ASSIGNMENT
			= "UPDATE BaseDeal  " +
			"     SET dealDetail.positionGenerationStatusValue = 'Generating', " +
			"         dealDetail.positionGenerationIdentifier = :identifier " +
			"   WHERE id in (:dealIds) " +
			"     AND dealDetail.positionGenerationStatusValue = 'Pending'";


	@Autowired
	private DealColumnDefinitions dealColumnDefinitions;

	@Override
	public void executeDealUpdateAssignForPositionGeneration(
			List<Integer> dealIds,
			String positionGeneratorId) {

		String[] names = {"identifier","dealIds"};
		if (dealIds.size() < 1000) {
			Object[] parms = {positionGeneratorId, dealIds};
				executeUpdate(
						UPDATE_DEAL_POSITION_GENERATION_ASSIGNMENT,
					names,
					parms);
		} else {
			SubLister<Integer> subLister = new SubLister<>(dealIds, 1000);
			while (subLister.moreElements()) {
				Object[] parms2 = {positionGeneratorId, subLister.nextList()};
				executeUpdate(
						UPDATE_DEAL_POSITION_GENERATION_ASSIGNMENT,
						names,
						parms2);
			}
		}

	}


	@Override
	public void executeDealUpdatePositionGenerationToComplete(
			List<Integer> dealIds,
			LocalDateTime positionGenerationDateTime) {

		String[] names = {"dealIds", "updateDateTime"};
		if (dealIds.size() < 2000) {
			Object[] parms = {dealIds, positionGenerationDateTime};
			executeUpdate(
					UPDATE_DEAL_POSITION_GENERATION_STATUS_TO_COMPLETE,
					names,
					parms);
		} else {
			SubLister<Integer> subLister = new SubLister<>(dealIds, 2000);
			while (subLister.moreElements()) {
				Object[] parms2 = {subLister.nextList(), positionGenerationDateTime};
				executeUpdate(
						UPDATE_DEAL_POSITION_GENERATION_STATUS_TO_COMPLETE,
						names,
						parms2);
			}
		}

	}


	@Override
	public void executeDealUpdateSetPositionGenerationToPending(List<Integer> dealIds) {

		if (dealIds.size() < 2000) {
			executeUpdate(
					UPDATE_DEAL_POSITION_GENERATION_STATUS,
					"dealIds",
					dealIds);
		} else {
			SubLister<Integer> subLister = new SubLister<>(dealIds, 2000);
			while (subLister.moreElements()) {
				executeUpdate(
						UPDATE_DEAL_POSITION_GENERATION_STATUS,
						"dealIds",
						subLister.nextList());
			}
		}

	}

	@Override
	public List<DealSummary> findAssignedDealSummaries(String positionGenerationIdentifier) {
		return (List<DealSummary>) executeReportQuery(
				FETCH_ASSIGNED_DEAL_SUMMARIES,
				"identifier",
				positionGenerationIdentifier);
	}

	@Override
	public List<PhysicalDealSummary> findPhysicalDealSummariesByIds(List<Integer> physicalDealIds) {

		if (physicalDealIds.size() < 2000) {
			return (List<PhysicalDealSummary>) executeReportQuery(
					FETCH_PHYSICAL_DEAL_SUMMARIES,
					"dealIds",
					physicalDealIds);
		} else {
			SubLister<Integer> subLister = new SubLister<>(physicalDealIds, 2000);
			ArrayList<PhysicalDealSummary> summaries = new ArrayList<>();
			while (subLister.moreElements()) {
				summaries.addAll(
                        (Collection<? extends PhysicalDealSummary>) executeReportQuery(
                            FETCH_PHYSICAL_DEAL_SUMMARIES,
                            "dealIds",
                            subLister.nextList()));
			}
			return summaries;
		}
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

	@Override
	public List<DealSummary> fetchDealSummaries() {
		return (List<DealSummary>) executeReportQuery(FETCH_ASSIGNED_DEAL_SUMMARIES);
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

	@Override
	public DealSummary getDealSummary(EntityId id) {
		return (DealSummary) executeSingleResultReportQuery(
				GET_DEAL_SUMMARY,
				"dealId",
				id.getId());
	}

	@Override
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
