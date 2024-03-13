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
package com.onbelay.dealcapture.dealmodule.deal.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.model.DealDayView;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DealService {
	public static final String BEAN_NAME = "dealService";
	
	public QuerySelectedPage findDealIds(DefinedQuery definedQuery);

	public List<BaseDealSnapshot> findByIds(QuerySelectedPage selectedPage);

	public TransactionResult save(BaseDealSnapshot snapshot);
	
	public TransactionResult save(List<BaseDealSnapshot> snapshots);
	
	public BaseDealSnapshot load(EntityId entityId);

	public DealSummary getDealSummary(EntityId entityId);

	public List<DealCostSnapshot> fetchDealCosts(EntityId dealId);

	public TransactionResult saveDealCosts(
			EntityId dealId,
			List<DealCostSnapshot> snapshots);

	public List<DealDaySnapshot> fetchDealDays(EntityId dealId);

    List<DealDayView> fetchDealDayViews(EntityId dealId);

	List<DealDayView> fetchDealDayViewsByDates(
			List<Integer> dealIds,
			LocalDate fromDate,
			LocalDate toDate);


	public List<DealDaySnapshot> fetchDealDaysByType(
			EntityId dealId,
			DayTypeCode code);

	public TransactionResult saveDealDays(
			EntityId dealId,
			List<DealDaySnapshot> snapshots);

	public List<DealSummary> getAssignedDealSummaries(String positionGenerationIdentifier);

	public void updateDealPositionGenerationStatusToPending(List<Integer> dealIds);

	public void assignPositionIdentifierToDeals(
			String positionGenerationIdentifier,
			List<Integer> dealIds);

	List<PhysicalDealSummary> findPhysicalDealSummariesByIds(List<Integer> physicalDealIds);

    void updateDealPositionStatusToComplete(
			List<Integer> dealIds,
			LocalDateTime observedDateTime);

	List<DealCostSummary> fetchDealCostSummaries(List<Integer> dealIds);
}
