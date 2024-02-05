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
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.riskfactor.repository.PriceRiskFactorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository (value="priceRiskFactorRepository")
@Transactional
public class PriceRiskFactorRepositoryBean extends BaseRepository<PriceRiskFactor> implements PriceRiskFactorRepository {
	public static final String FETCH_RISK_FACTOR_BY_MARKET_DATE = "PriceRiskFactorRepository.FETCH_RISK_FACTOR_BY_MARKET_DATE";
	public static final String FETCH_RISK_FACTORS_BY_DATES = "PriceRiskFactorRepository.FETCH_RISK_FACTORS_BY_DATES";
	public static final String FIND_BY_PRICE_INDEX_ID = "PriceRiskFactorRepository.FIND_BY_PRICE_INDEX_ID";
    public static final String FETCH_BY_PRICE_INDEX_IDS =  "PriceRiskFactorRepository.FETCH_BY_PRICE_INDEX_IDS"; ;

    @Autowired
	private RiskFactorColumnDefinitions riskFactorColumnDefinitions;


	@Override
	public PriceRiskFactor load(EntityId entityId) {
		if (entityId.isSet())
			return (PriceRiskFactor) find(PriceRiskFactor.class, entityId.getId());
		else
			return null;
		
	}

	@Override
	public List<PriceRiskFactor> find(DefinedQuery definedQuery) {
		return executeDefinedQuery(
				riskFactorColumnDefinitions,
				definedQuery);
	}

	@Override
	public List<PriceRiskFactor> fetchByPriceIndex(EntityId priceIndexId) {
		return executeQuery(
				FIND_BY_PRICE_INDEX_ID,
				"indexId",
				priceIndexId.getId());
	}

	@Override
	public List<PriceRiskFactor> fetchByPriceIndices(
			List<Integer> priceIndexIds,
			LocalDate fromDate,
			LocalDate toDate) {

		List<PriceRiskFactor> riskFactors = new ArrayList<>();
		String[] names = {"indexIds", "fromDate", "toDate"};
		if (priceIndexIds.size() < 2000) {
			Object[] parms = {priceIndexIds, fromDate, toDate};
			riskFactors = executeQuery(
					FETCH_BY_PRICE_INDEX_IDS,
					names,
					parms);
		} else {
			SubLister<Integer> subLister = new SubLister<>(priceIndexIds, 2000);
			while (subLister.moreElements()) {
				Object[] parms2 = {subLister.nextList(), fromDate, toDate};
				riskFactors.addAll(
						executeQuery(
						FETCH_BY_PRICE_INDEX_IDS,
								names,
								parms2));
			}
		}
		return riskFactors;
	}

	@Override
	public PriceRiskFactor fetchByMarketDate(EntityId entityId, LocalDate marketDate) {
		String[] names = {"indexId", "marketDate"};
		Object[] values = {entityId.getId(), marketDate};
		return executeSingleResultQuery(FETCH_RISK_FACTOR_BY_MARKET_DATE, names, values);
	}

	@Override
	public List<PriceRiskFactor> fetchByDatesInclusive(
			EntityId entityId,
			LocalDate fromMarketDate,
			LocalDate toMarketDate) {

		String[] names = {"priceIndexId", "fromMarketDate", "toMarketDate"};
		Object[] values = {entityId.getId(), fromMarketDate, toMarketDate};
		return executeQuery(FETCH_RISK_FACTORS_BY_DATES, names, values);
	}

	@Override
	public List<Integer> findPriceRiskFactorIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				riskFactorColumnDefinitions,
				definedQuery);
	}

	@Override
	public List<PriceRiskFactor> fetchByIds(QuerySelectedPage selectedPage) {
		return fetchEntitiesById(
				riskFactorColumnDefinitions,
				"PriceRiskFactor",
				selectedPage);
	}

}
