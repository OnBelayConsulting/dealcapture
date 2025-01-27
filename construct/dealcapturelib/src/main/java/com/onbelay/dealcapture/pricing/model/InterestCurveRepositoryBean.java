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
import com.onbelay.dealcapture.pricing.repository.InterestCurveRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository (value="interestCurveRepository")
@Transactional


public class InterestCurveRepositoryBean extends BaseRepository<InterestCurve> implements InterestCurveRepository {
	public static final String FETCH_RATE_BY_CURVE_DATE_OBS_DATE = "InterestCurveRepository.FETCH_RATE_BY_CURVE_DATE_OBS_DATE";

	@Autowired
	private InterestCurveColumnDefinitions interestCurveColumnDefinitions;


	@Override
	public InterestCurve load(EntityId entityId) {
		if (entityId.isSet())
			return (InterestCurve) find(InterestCurve.class, entityId.getId());
		else
			return null;
		
	}


	@Override
	public InterestCurve fetchCurrentInterestRate(EntityId entityId, LocalDate interestDate) {
		String[] names = {"interestIndexId", "curveDate", "hourEnding", "currentDateTime"};
		Object[] values = {entityId.getId(), interestDate, 0, LocalDateTime.now()};
		List<InterestCurve> curves = executeQuery(FETCH_RATE_BY_CURVE_DATE_OBS_DATE, names, values);
		if (curves == null || curves.isEmpty())
			return null;
		return curves.get(0);
	}


	@Override
	public List<Integer> findInterestCurveIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				interestCurveColumnDefinitions,
				definedQuery);
	}

	@Override
	public List<InterestCurve> fetchByIds(QuerySelectedPage selectedPage) {
		return fetchEntitiesById(
				interestCurveColumnDefinitions,
				"InterestCurve",
				selectedPage);
	}

}
