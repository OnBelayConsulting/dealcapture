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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.repository.IndexPriceRepository;

@Repository (value="indexPriceRepository")
@Transactional


public class IndexPriceRepositoryBean extends BaseRepository<IndexPrice> implements IndexPriceRepository {
	public static final String FETCH_PRICE_BY_PRICE_DATE_OBS_DATE = "FETCH_PRICE_BY_PRICE_DATE_OBS_DATE";
	
	@Autowired
	private IndexPriceColumnDefinitions indexPriceColumnDefinitions;


	@Override
	public IndexPrice load(EntityId entityId) {
		if (entityId.isSet())
			return (IndexPrice) find(IndexPrice.class, entityId.getId());
		else
			return null;
		
	}
	
	

	@Override
	public IndexPrice fetchCurrentPrice(EntityId entityId, LocalDate priceDate) {
		String[] names = {"pricingIndexId", "priceDate", "currentDateTime"};
		Object[] values = {entityId.getId(), priceDate, LocalDateTime.now()};
		return executeSingleResultQuery(FETCH_PRICE_BY_PRICE_DATE_OBS_DATE, names, values);
	}



	@Override
	public List<Integer> findIndexPriceIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				indexPriceColumnDefinitions, 
				definedQuery);
	}

	@Override
	public List<IndexPrice> fetchByIds(QuerySelectedPage selectedPage) {
		return fetchEntitiesById(
				indexPriceColumnDefinitions,
				"IndexPrice",
				selectedPage);
	}

}
