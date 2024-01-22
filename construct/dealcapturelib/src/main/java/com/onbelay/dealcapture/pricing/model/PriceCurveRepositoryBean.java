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
import java.util.ArrayList;
import java.util.List;

import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.pricing.snapshot.CurveReport;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.pricing.repository.PriceCurveRepository;

@Repository (value="priceCurveRepository")
@Transactional


public class PriceCurveRepositoryBean extends BaseRepository<PriceCurve> implements PriceCurveRepository {
	public static final String FETCH_PRICE_BY_PRICE_DATE_OBS_DATE = "PriceCurveRepository.FETCH_PRICE_BY_PRICE_DATE_OBS_DATE";
	public static final String FETCH_PRICE_REPORTS = "PriceCurveRepository.FETCH_PRICE_REPORTS";

	@Autowired
	private PriceCurveColumnDefinitions priceCurveColumnDefinitions;


	@Override
	public PriceCurve load(EntityId entityId) {
		if (entityId.isSet())
			return (PriceCurve) find(PriceCurve.class, entityId.getId());
		else
			return null;
		
	}

	@Override
	public List<CurveReport> fetchPriceCurveReports(
			List<Integer> priceIndexIds,
			LocalDate fromCurveDate,
			LocalDate toCurveDate,
			LocalDateTime observedDateTime) {


		String[] names = {"indexIds", "fromCurveDate", "toCurveDate", "observedDateTime"};
		if (priceIndexIds.size() < 2000) {
			Object[] parms =  {priceIndexIds, fromCurveDate, toCurveDate, observedDateTime};
			return (List<CurveReport>) executeReportQuery(
					FETCH_PRICE_REPORTS,
					names,
					parms);
		} else {
			ArrayList<CurveReport> reports = new ArrayList<>();
			SubLister<Integer> subLister = new SubLister<>(priceIndexIds, 2000);
			Object[] parms =  {subLister.nextList(), fromCurveDate, toCurveDate, observedDateTime};
			while (subLister.moreElements()) {
				reports.addAll(
						(List<CurveReport>) executeReportQuery(
								FETCH_PRICE_REPORTS,
								names,
								parms));
			}
			return reports;
 		}
	}

	@Override
	public PriceCurve fetchCurrentPrice(EntityId entityId, LocalDate priceDate) {
		String[] names = {"priceIndexId", "curveDate", "currentDateTime"};
		Object[] values = {entityId.getId(), priceDate, LocalDateTime.now()};
		return executeSingleResultQuery(FETCH_PRICE_BY_PRICE_DATE_OBS_DATE, names, values);
	}



	@Override
	public List<Integer> findPriceCurveIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				priceCurveColumnDefinitions,
				definedQuery);
	}

	@Override
	public List<PriceCurve> fetchByIds(QuerySelectedPage selectedPage) {
		return fetchEntitiesById(
				priceCurveColumnDefinitions,
				"PriceCurve",
				selectedPage);
	}

}
