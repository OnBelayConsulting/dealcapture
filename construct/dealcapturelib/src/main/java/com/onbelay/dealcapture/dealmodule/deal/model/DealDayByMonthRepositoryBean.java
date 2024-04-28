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
import com.onbelay.dealcapture.dealmodule.deal.repository.DealDayByMonthRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository (value="dealDayByMonthRepository")
@Transactional

public class DealDayByMonthRepositoryBean extends BaseRepository<DealDayByMonth> implements DealDayByMonthRepository {
	public static final String FETCH_DEAL_DAYS = "DealDayByMonthRepository.FETCH_DEAL_DAYS";
	public static final String FIND_BY_DEAL_AND_TYPE = "DealDayByMonthRepository.FIND_BY_DEAL_AND_TYPE";
	public static final String FETCH_DEAL_DAY_VIEWS = "DealDayByMonthRepository.FETCH_DEAL_DAY_VIEWS" ;
	public static final String FETCH_DEAL_DAY_VIEWS_BY_TYPE = "DealDayByMonthRepository.FETCH_DEAL_DAY_VIEWS_BY_TYPE" ;
	public static final String FETCH_ALL_DEAL_DAY_VIEWS_BY_DATE = "DealDayByMonthRepository.FETCH_ALL_DEAL_DAY_VIEWS_BY_DATE";


	@Override
	public DealDayByMonth load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return (DealDayByMonth) find(DealDayByMonth.class, entityId.getId());
		else
			return null;
	}

	@Override
	public List<DealDayByMonth> fetchDealDayByMonths(
			Integer dealId,
			DayTypeCode code) {

		String[] names = {"dealId", "dealTypeCode"};
		Object[] parms = {dealId, code.getCode()};
		return executeQuery(
				FIND_BY_DEAL_AND_TYPE,
				names,
				parms);
	}

	@Override
	public List<DealDayByMonth> fetchDealDayByMonths(Integer dealId) {
		
		return (List<DealDayByMonth>) executeQuery(
				FETCH_DEAL_DAYS,
				"dealId",
				dealId); 
	}

	@Override
	public List<DealDayByMonthView> fetchDealDayViewsByType(
			EntityId dealId,
			DayTypeCode dayTypeCode,
			LocalDateTime fromDate,
			LocalDateTime toDate) {

		String[] names = {"dealId", "dayTypeCode", "fromDate", "toDate"};
		Object[] parms = {dealId, dayTypeCode.getCode(), fromDate, toDate};

		return (List<DealDayByMonthView>) executeReportQuery(
				FETCH_DEAL_DAY_VIEWS_BY_TYPE,
				names,
				parms);
	}

	@Override
	public List<DealDayByMonthView> fetchDealDayViews(EntityId dealId) {

		return (List<DealDayByMonthView>) executeReportQuery(
				FETCH_DEAL_DAY_VIEWS,
				"dealId",
				dealId.getId());
	}
	@Override
	public List<DealDayByMonthView> fetchAllDealDayViewsByDates(
			List<Integer> dealIds,
			LocalDate fromDate,
			LocalDate toDate) {

		String[] names = {"dealIds", "fromDate", "toDate"};
		if (dealIds.size() < 2000) {
			Object[] parms = {dealIds, fromDate, toDate};
			return (List<DealDayByMonthView>) executeReportQuery(
					FETCH_ALL_DEAL_DAY_VIEWS_BY_DATE,
					names,
					parms);
		} else {
			ArrayList<DealDayByMonthView> summaries = new ArrayList<>();
			SubLister<Integer> subLister = new SubLister<>(dealIds, 2000);
			while (subLister.moreElements()) {
				Object[] parms2 = {subLister.nextList(), fromDate, toDate};
				summaries.addAll(
						(List<DealDayByMonthView>) executeReportQuery(
								FETCH_ALL_DEAL_DAY_VIEWS_BY_DATE,
								names,
								parms2));
			}
			return summaries;
		}
	}


}
