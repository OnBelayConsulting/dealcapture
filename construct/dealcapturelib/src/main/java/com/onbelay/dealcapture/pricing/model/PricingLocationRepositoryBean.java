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
package com.onbelay.dealcapture.pricing.model;

import java.util.List;

import jakarta.transaction.Transactional;

import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.repository.PricingLocationRepository;

@Repository (value="pricingLocationRepository")
@Transactional


public class PricingLocationRepositoryBean extends BaseRepository<PricingLocation> implements PricingLocationRepository {
	public static final String FIND_PRICING_LOCATION_BY_NAME = "FIND_PRICING_LOCATION_BY_NAME";
	
	@Autowired
	private PricingLocationColumnDefinitions pricingLocationColumnDefinitions;

	public PricingLocation findByName(String name) {
		return  executeSingleResultQuery(FIND_PRICING_LOCATION_BY_NAME, "name", name);
	}

	@Override
	public PricingLocation load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isSet())
			return (PricingLocation) find(PricingLocation.class, entityId.getId());
		else if (entityId.getCode() != null)
			return findByName(entityId.getCode());
		else
			return null;
		
	}
	

	@Override
	public List<Integer> findPricingLocationIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				pricingLocationColumnDefinitions, 
				definedQuery);
	}

	@Override
	public List<PricingLocation> fetchByIds(QuerySelectedPage selectedPage) {
		return fetchEntitiesById(
				pricingLocationColumnDefinitions,
				"PricingLocation",
				selectedPage);
	}



}
