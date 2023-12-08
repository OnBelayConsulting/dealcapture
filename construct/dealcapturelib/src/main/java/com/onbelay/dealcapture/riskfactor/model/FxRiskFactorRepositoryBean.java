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
package com.onbelay.dealcapture.riskfactor.model;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.riskfactor.repository.FxRiskFactorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository (value="fxRiskFactorRepository")
@Transactional
public class FxRiskFactorRepositoryBean extends BaseRepository<FxRiskFactor> implements FxRiskFactorRepository {
	public static final String FETCH_RISK_FACTOR_BY_MARKET_DATE = "FxRiskFactorRepositoryBean.FETCH_RISK_FACTOR_BY_MARKET_DATE";
	public static final String FETCH_RISK_FACTORS_BY_DATES = "FxRiskFactorRepositoryBean.FETCH_RISK_FACTORS_BY_DATES";
	public static final String LOAD_ALL = "FxRiskFactorRepositoryBean.LOAD_ALL";
	public static final String FIND_BY_INDEX_ID = "FxRiskFactorRepositoryBean.FIND_BY_INDEX_ID";

	@Autowired
	private RiskFactorColumnDefinitions riskFactorColumnDefinitions;


	@Override
	public FxRiskFactor load(EntityId entityId) {
		if (entityId.isSet())
			return (FxRiskFactor) find(FxRiskFactor.class, entityId.getId());
		else
			return null;
		
	}

	@Override
	public List<FxRiskFactor> fetchByFxIndex(EntityId fxIndexId) {
		return executeQuery(FIND_BY_INDEX_ID, "indexId", fxIndexId.getId());
	}

	@Override
	public FxRiskFactor fetchByMarketDate(EntityId entityId, LocalDate marketDate) {
		String[] names = {"indexId", "marketDate"};
		Object[] values = {entityId.getId(), marketDate};
		return executeSingleResultQuery(FETCH_RISK_FACTOR_BY_MARKET_DATE, names, values);
	}

	@Override
	public List<FxRiskFactor> loadAll() {
		
		return executeQuery(LOAD_ALL);
	}


	@Override
	public List<FxRiskFactor> fetchByDatesInclusive(
			EntityId entityId,
			LocalDate fromMarketDate,
			LocalDate toMarketDate) {

		String[] names = {"indexId", "fromMarketDate", "toMarketDate"};
		Object[] values = {entityId.getId(), fromMarketDate, toMarketDate};
		return executeQuery(FETCH_RISK_FACTORS_BY_DATES, names, values);
	}


	@Override
	public List<Integer> findFxRiskFactorIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				riskFactorColumnDefinitions,
				definedQuery);
	}

	@Override
	public List<FxRiskFactor> fetchByIds(QuerySelectedPage selectedPage) {
		return fetchEntitiesById(
				riskFactorColumnDefinitions,
				"FxRiskFactor",
				selectedPage);
	}

}
