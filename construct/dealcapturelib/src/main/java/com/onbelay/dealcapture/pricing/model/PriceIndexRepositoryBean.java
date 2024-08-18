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
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.common.snapshot.EntityIdCollection;
import com.onbelay.dealcapture.pricing.repository.PriceIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexReport;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository (value="priceIndexRepository")
@Transactional


public class PriceIndexRepositoryBean extends BaseRepository<PriceIndex> implements PriceIndexRepository {
	public static final String FIND_BY_NAME = "PriceIndexRepository.FIND_BY_NAME";
	public static final String FIND_BY_DEAL_IDS = "PriceIndexRepository.FIND_BY_DEAL_IDS";
	public static final String FETCH_PRICE_INDEX_REPORTS = "PriceIndexRepository.FETCH_PRICE_INDEX_REPORTS";
	public static final String LOAD_ALL = "PriceIndexRepository.LOAD_ALL";

	@Autowired
	private PriceIndexColumnDefinitions priceIndexColumnDefinitions;

	@Override
	public PriceIndex findPriceIndexByName(String name) {
		return (PriceIndex) executeSingleResultQuery(FIND_BY_NAME, "name", name);
	}

	@Override
	public List<PriceIndex> findActivePriceIndices() {
		return executeQuery(LOAD_ALL);
	}

	@Override
	public List<PriceIndexReport> fetchPriceIndexReports(List<Integer> indexIds) {
		if (indexIds.size() < 2000) {
			return (List<PriceIndexReport>) executeReportQuery(
					FETCH_PRICE_INDEX_REPORTS,
					"indexIds",
					indexIds);
		} else {
			SubLister<Integer> subLister = new SubLister<>(indexIds, 2000);
			ArrayList<PriceIndexReport> reports = new ArrayList<>();
			while (subLister.moreElements()) {
				reports.addAll(
						(Collection<? extends PriceIndexReport>) executeReportQuery(
								FETCH_PRICE_INDEX_REPORTS,
								"indexIds",
								subLister.nextList()));
			}
			return reports;
		}

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
