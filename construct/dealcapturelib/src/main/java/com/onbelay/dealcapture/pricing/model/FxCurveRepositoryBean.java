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
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.repository.FxCurveRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository (value="fxCurveRepository")
@Transactional


public class FxCurveRepositoryBean extends BaseRepository<FxCurve> implements FxCurveRepository {
	public static final String FETCH_FX_BY_FX_DATE_OBS_DATE = "FETCH_FX_BY_FX_DATE_OBS_DATE";
	
	@Autowired
	private FxCurveColumnDefinitions fxCurveColumnDefinitions;


	@Override
	public FxCurve load(EntityId entityId) {
		if (entityId.isSet())
			return (FxCurve) find(FxCurve.class, entityId.getId());
		else
			return null;
		
	}
	
	

	@Override
	public FxCurve fetchCurrentFx(EntityId entityId, LocalDate fxDate) {
		String[] names = {"fxIndexId", "fxDate", "currentDateTime"};
		Object[] values = {entityId.getId(), fxDate, LocalDateTime.now()};
		return executeSingleResultQuery(FETCH_FX_BY_FX_DATE_OBS_DATE, names, values);
	}



	@Override
	public List<Integer> findFxCurveIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				fxCurveColumnDefinitions,
				definedQuery);
	}

	@Override
	public List<FxCurve> fetchByIds(QuerySelectedPage selectedPage) {
		return fetchEntitiesById(
				fxCurveColumnDefinitions,
				"FxCurve",
				selectedPage);
	}

}
