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
package com.onbelay.dealcapture.dealmodule.positions.model;

import com.onbelay.core.entity.repository.BaseRepository;
import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.enums.CoreTransactionErrorCode;
import com.onbelay.core.exception.OBRuntimeException;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.positions.repository.PowerProfilePositionRepository;
import com.onbelay.shared.enums.CurrencyCode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository (value="powerProfilePositionRepository")
@Transactional

public class PowerProfilePositionRepositoryBean extends BaseRepository<PowerProfilePosition> implements PowerProfilePositionRepository {
	public static final String FIND_BY_POWER_PROFILE = "PowerProfilePositionsRepository.FIND_BY_POWER_PROFILE";
	public static final String FIND_PROFILE_POSITION_VIEWS ="PowerProfilePositionsRepository.FIND_PROFILE_POSITION_VIEWS";
	public static final String FIND_PROFILE_POSITION_VIEWS_BY_CONTEXT ="PowerProfilePositionsRepository.FIND_PROFILE_POSITION_VIEWS_BY_CONTEXT";

	@Autowired
	private PowerProfilePositionColumnDefinitions powerProfilePositionColumnDefinitions;

	@Override
	public PowerProfilePosition load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return find(PowerProfilePosition.class, entityId.getId());
		else
			return null;
	}

	@Override
	public List<PowerProfilePosition> findByPowerProfile(EntityId powerProfileId) {

		return executeQuery(
				FIND_BY_POWER_PROFILE,
				"powerProfileId",
				powerProfileId.getId());
	}

	@Override
	public List<PowerProfilePositionView> findPowerProfilePositionViews(
			List<Integer> powerProfileIds,
			LocalDateTime createdDateTime) {

		String[] names = {"powerProfileIds", "createdDateTime"};

		if (powerProfileIds.size() < 2000) {
			Object[] parms = {powerProfileIds, createdDateTime};

			return (List<PowerProfilePositionView>) executeReportQuery(
					FIND_PROFILE_POSITION_VIEWS,
					names,
					parms);
		} else {
			ArrayList<PowerProfilePositionView> views = new ArrayList<>();
			SubLister<Integer> subLister = new SubLister<>(powerProfileIds, 2000);
			while (subLister.moreElements()) {
				Object[] parmsTwo = {subLister.nextList(), createdDateTime};
				views.addAll (
						(Collection<? extends PowerProfilePositionView>) executeReportQuery(
								FIND_PROFILE_POSITION_VIEWS,
								names,
								parmsTwo));

			}
			return views;
		}
	}

	@Override
	public List<PowerProfilePositionView> findPowerProfilePositionViewsByDate(
			LocalDate startDate,
			LocalDate endDate,
			LocalDateTime createdDateTime) {

		String[] names = {"startDate", "endDate", "createdDateTime"};
		Object[] parms = {startDate, endDate,  createdDateTime};
		return (List<PowerProfilePositionView>) executeReportQuery(
				FIND_PROFILE_POSITION_VIEWS_BY_CONTEXT,
				names,
				parms);
	}

	@Override
	public List<PowerProfilePosition> fetchByIds(QuerySelectedPage querySelectedPage) {
		return fetchEntitiesById(
				powerProfilePositionColumnDefinitions,
				"PowerProfilePosition",
				querySelectedPage);
	}

	@Override
	public List<Integer> findPowerProfilePositionIds(DefinedQuery definedQuery) {
		return executeDefinedQueryForIds(
				powerProfilePositionColumnDefinitions,
				definedQuery);

	}



}
