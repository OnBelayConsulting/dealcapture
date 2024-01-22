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
package com.onbelay.dealcapture.dealmodule.deal.repository;

import java.util.List;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.enums.PositionGenerationStatusCode;
import com.onbelay.dealcapture.dealmodule.deal.model.BaseDeal;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealSummary;

public interface DealRepository {
	
	public static final String BEAN_NAME = "dealRepository";

	/**
	 * Update position generation status fields
	 * @param dealId
	 * @param positionGeneratorId - owning generator process name;
	 * @param code - PositionGenerationStatus to set
	 * @return true if update succeeded.
	 */
	public boolean executeUpdateOfPositionGenerationStatus(
			Integer dealId,
			String positionGeneratorId,
			PositionGenerationStatusCode code);

	/**
	 * Fetch a list of deals by ids. This is usually used in paging.
	 * @param querySelectedPage
	 * @return
	 */
	public List<BaseDeal> fetchByIds(QuerySelectedPage querySelectedPage);
	
	/**
	 * Fetch a list of deal ids from a defined query.
	 * @param definedQuery
	 * @return
	 */
	public List<Integer> findDealIds(DefinedQuery definedQuery);

	public DealSummary getDealSummary(EntityId id);
	/**
	 * Find deals using a defined query
	 * @param query
	 * @return
	 */
	public List<BaseDeal> findByQuery(DefinedQuery query);

	/**
	 * Fetch a list of deal summaries
	 * @return
	 */
	public List<DealSummary> fetchDealSummaries();
	
	
	public BaseDeal load(EntityId dealKey);
	
}
