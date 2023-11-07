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
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;

@Repository (value="priceIndexRepository")
@Transactional


public class PriceIndexRepositoryBean extends BaseRepository<PriceIndex> implements PriceIndexRepository {
	public static final String FIND_BY_NAME = "PriceIndexRepository.FIND_BY_NAME";
	public static final String LOAD_ALL = "PriceIndexRepository.LOAD_ALL";

	@Autowired
	private PriceIndexColumnDefinitions priceIndexColumnDefinitions;

	@Override
	public PriceIndex findPriceIndexByName(String name) {
		return (PriceIndex) executeSingleResultQuery(FIND_BY_NAME, "name", name);
	}

	@Override
	public List<PriceIndex> loadAll() {
		return executeQuery(LOAD_ALL);
	}

	@Override
	public PriceIndex load(EntityId entityId) {

		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isSet())
			return (PriceIndex) find(PriceIndex.class, entityId.getId());
		else if (entityId.getCode() != null)
			return findPriceIndexByName(entityId.getCode());
		else
			return null;
		
	}

	@Override
	public List<Integer> findPriceIndexIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				priceIndexColumnDefinitions,
				definedQuery);
	}

	@Override
	public List<PriceIndex> fetchByIds(QuerySelectedPage selectedPage) {
		return fetchEntitiesById(
				priceIndexColumnDefinitions,
				"PriceIndex",
				selectedPage);
	}

}
