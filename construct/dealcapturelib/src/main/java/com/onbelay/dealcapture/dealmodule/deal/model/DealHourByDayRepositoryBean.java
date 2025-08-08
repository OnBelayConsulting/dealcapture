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
package com.onbelay.dealcapture.dealmodule.deal.model;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.enums.DayTypeCode;
import com.onbelay.dealcapture.dealmodule.deal.repository.DealHourByDayRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository (value="dealHourByDayRepository")
@Transactional

public class DealHourByDayRepositoryBean extends BaseRepository<DealHourByDay> implements DealHourByDayRepository {
	public static final String FETCH_DEAL_HOURS_BY_DAYS = "DealHourByDayRepository.FETCH_DEAL_HOURS_BY_DAYS";
	public static final String FIND_BY_DEAL_AND_TYPE = "DealHourByDayRepository.FIND_BY_DEAL_AND_TYPE";
	public static final String FETCH_DEAL_HOUR_VIEWS = "DealHourByDayRepository.FETCH_DEAL_HOUR_VIEWS" ;
	public static final String FETCH_DEAL_HOUR_VIEWS_BY_TYPE = "DealHourByDayRepository.FETCH_DEAL_HOUR_VIEWS_BY_TYPE" ;
	public static final String FETCH_ALL_DEAL_HOUR_VIEWS_BY_DATE = "DealHourByDayRepository.FETCH_ALL_DEAL_HOUR_VIEWS_BY_DATE";
	public static final String FETCH_DEAL_HOURS_FOR_ONE_DAY = "DealHourByDayRepository.FETCH_DEAL_HOURS_FOR_ONE_DAY" ;


	@Override
	public DealHourByDay load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return (DealHourByDay) find(DealHourByDay.class, entityId.getId());
		else
			return null;
	}

	@Override
	public List<DealHourByDay> fetchDealHourByDays(
			Integer dealId,
			DayTypeCode code) {

		String[] names = {"dealId", "dayTypeCode"};
		Object[] parms = {dealId, code.getCode()};
		return executeQuery(
				FIND_BY_DEAL_AND_TYPE,
				names,
				parms);
	}

	@Override
	public List<DealHourByDay> fetchDealHourByDays(Integer dealId) {
		
		return  executeQuery(
				FETCH_DEAL_HOURS_BY_DAYS,
				"dealId",
				dealId); 
	}

	@Override
	public List<DealHourByDay> fetchDealHourByDayForADay(
			Integer dealId,
			LocalDate dayDate) {
		String[] names = {"dealId", "dayDate"};
		Object[] parms = {dealId, dayDate};

		return  executeQuery(
				FETCH_DEAL_HOURS_FOR_ONE_DAY,
				names,
				parms);
	}


	@Override
	public List<DealHourByDayView> fetchDealHourByDayViewsByType(
			EntityId dealId,
			DayTypeCode dayTypeCode,
			LocalDateTime fromDate,
			LocalDateTime toDate) {

		String[] names = {"dealId", "dayTypeCode", "fromDate", "toDate"};
		Object[] parms = {dealId, dayTypeCode.getCode(), fromDate, toDate};

		return (List<DealHourByDayView>) executeReportQuery(
				FETCH_DEAL_HOUR_VIEWS_BY_TYPE,
				names,
				parms);
	}

	@Override
	public List<DealHourByDayView> fetchDealHourByDayViews(EntityId dealId) {

		return (List<DealHourByDayView>) executeReportQuery(
				FETCH_DEAL_HOUR_VIEWS,
				"dealId",
				dealId.getId());
	}
	@Override
	public List<DealHourByDayView> fetchAllDealHourByDayViewsByDates(
			List<Integer> dealIds,
			LocalDate fromDate,
			LocalDate toDate) {

		String[] names = {"dealIds", "fromDate", "toDate"};
		if (dealIds.size() < 2000) {
			Object[] parms = {dealIds, fromDate, toDate};
			return (List<DealHourByDayView>) executeReportQuery(
					FETCH_ALL_DEAL_HOUR_VIEWS_BY_DATE,
					names,
					parms);
		} else {
			ArrayList<DealHourByDayView> summaries = new ArrayList<>();
			SubLister<Integer> subLister = new SubLister<>(dealIds, 2000);
			while (subLister.moreElements()) {
				Object[] parms2 = {subLister.nextList(), fromDate, toDate};
				summaries.addAll(
						(List<DealHourByDayView>) executeReportQuery(
								FETCH_ALL_DEAL_HOUR_VIEWS_BY_DATE,
								names,
								parms2));
			}
			return summaries;
		}
	}


}
