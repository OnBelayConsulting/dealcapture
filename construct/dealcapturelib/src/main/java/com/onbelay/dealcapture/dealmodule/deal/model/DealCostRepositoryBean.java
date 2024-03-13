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

import java.util.ArrayList;
import java.util.List;

import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealCostRepository;
import com.onbelay.dealcapture.dealmodule.deal.snapshot.DealCostSummary;
import jakarta.transaction.Transactional;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import org.springframework.stereotype.Repository;

import com.onbelay.core.entity.repository.BaseRepository;

@Repository (value="dealCostRepository")
@Transactional

public class DealCostRepositoryBean extends BaseRepository<DealCost> implements DealCostRepository {
	public static final String BEAN_NAME = "dealCostRepository";
	public static final String FETCH_DEAL_COSTS = "DealCostRepository.FETCH_DEAL_COSTS";
	public static final String FIND_BY_DEAL_AND_NAME = "DealCostRepository.FIND_BY_DEAL_AND_NAME";
    public static final String FETCH_DEAL_COST_SUMMARIES = "DealCostRepository.FETCH_DEAL_COST_SUMMARIES" ;


    @Override
	public DealCost load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return (DealCost) find(DealCost.class, entityId.getId());
		else
			return null;
	}

	@Override
	public DealCost findByDealAndName(
			Integer dealId,
			String name) {

		String[] names = {"dealId", "name"};
		Object[] parms = {dealId, name};
		return executeSingleResultQuery(
				FIND_BY_DEAL_AND_NAME,
				names,
				parms);
	}

	@Override
	public List<DealCost> fetchDealCosts(Integer dealId) {
		
		return (List<DealCost>) executeQuery(
				FETCH_DEAL_COSTS,
				"dealId",
				dealId); 
	}

	@Override
	public List<DealCostSummary> fetchDealCostSummaries(List<Integer> dealIds) {

		if (dealIds.size() < 2000) {
			return (List<DealCostSummary>) executeReportQuery(
					FETCH_DEAL_COST_SUMMARIES,
					"dealIds",
					dealIds);
		} else {
			ArrayList<DealCostSummary> summaries = new ArrayList<>();
			SubLister<Integer> subLister = new SubLister<>(dealIds, 2000);
			while (subLister.moreElements()) {
				summaries.addAll(
						(List<DealCostSummary>) executeReportQuery(
								FETCH_DEAL_COST_SUMMARIES,
								"dealIds",
								subLister.nextList()));
			}
			return summaries;
		}
	}
}
