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

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.shared.enums.CurrencyCode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository (value="fxIndexRepository")
@Transactional


public class FxIndexRepositoryBean extends BaseRepository<FxIndex> implements FxIndexRepository {
	public static final String FIND_BY_NAME = "FxIndexRepository.FIND_BY_NAME";
	public static final String LOAD_ALL = "FxIndexRepository.LOAD_ALL";
	public static final String FIND_BY_FROM_TO_CURRENCIES = "FxIndexRepository.FIND_BY_FROM_TO_CURRENCIES";

	@Autowired
	private FxIndexColumnDefinitions fxIndexColumnDefinitions;

	@Override
	public FxIndex findFxIndexByName(String name) {
		return (FxIndex) executeSingleResultQuery(FIND_BY_NAME, "name", name);
	}

	@Override
	public List<FxIndex> loadAll() {
		return executeQuery(LOAD_ALL);
	}

	@Override
	public List<FxIndex> findFxIndexByFromToCurrencyCodes(
			CurrencyCode from,
			CurrencyCode to) {
		String[] names = {"from", "to"};
		Object[] parms = {from.getCode(), to.getCode()};
		return executeQuery(FIND_BY_FROM_TO_CURRENCIES, names, parms);
	}

	@Override
	public FxIndex load(EntityId entityId) {

		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isSet())
			return (FxIndex) find(FxIndex.class, entityId.getId());
		else if (entityId.getCode() != null)
			return findFxIndexByName(entityId.getCode());
		else
			return null;
		
	}

	@Override
	public List<Integer> findFxIndexIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				fxIndexColumnDefinitions,
				definedQuery);
	}

	@Override
	public List<FxIndex> fetchByIds(QuerySelectedPage selectedPage) {
		return fetchEntitiesById(
				fxIndexColumnDefinitions,
				"FxIndex",
				selectedPage);
	}

}
