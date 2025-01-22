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
import com.onbelay.dealcapture.pricing.repository.InterestIndexRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository (value="interestIndexRepository")
@Transactional


public class InterestIndexRepositoryBean extends BaseRepository<InterestIndex> implements InterestIndexRepository {
	public static final String FIND_BY_NAME = "InterestIndexRepository.FIND_BY_NAME";
	public static final String FIND_UNEXPIRED_INDICES = "InterestIndexRepository.FIND_UNEXPIRED_INDICES";
	public static final String FIND_BY_IS_RISK_FREE_RATE ="InterestIndexRepository.FIND_BY_IS_RISK_FREE_RATE";

	@Autowired
	private InterestIndexColumnDefinitions interestIndexColumnDefinitions;

	@Override
	public InterestIndex findInterestIndexByName(String name) {
		return (InterestIndex) executeSingleResultQuery(FIND_BY_NAME, "name", name);
	}

	public List<InterestIndex> findNonExpiredInterestIndexes() {
		return executeQuery(FIND_UNEXPIRED_INDICES);
	}


	@Override
	public InterestIndex findInterestIndexByIsRiskFreeRate() {
		return executeSingleResultQuery(FIND_BY_IS_RISK_FREE_RATE, "isRiskFreeRate", true);
	}


	@Override
	public InterestIndex load(EntityId entityId) {

		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isSet())
			return (InterestIndex) find(InterestIndex.class, entityId.getId());
		else if (entityId.getCode() != null)
			return findInterestIndexByName(entityId.getCode());
		else
			return null;
		
	}

	@Override
	public List<Integer> findInterestIndexIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				interestIndexColumnDefinitions,
				definedQuery);
	}

	@Override
	public List<InterestIndex> fetchByIds(QuerySelectedPage selectedPage) {
		return fetchEntitiesById(
				interestIndexColumnDefinitions,
				"InterestIndex",
				selectedPage);
	}

}
