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
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.core.utils.SubLister;
import com.onbelay.dealcapture.dealmodule.deal.repository.PowerProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository (value="powerProfileRepository")
@Transactional
public class PowerProfileRepositoryBean extends BaseRepository<PowerProfile> implements PowerProfileRepository {
	public static final String BEAN_NAME = "powerProfileRepository";
	public static final String FETCH_ASSIGNED_POWER_PROFILES = "PowerProfileRepositoryBean.FETCH_ASSIGNED_POWER_PROFILES" ;
	private static final String UPDATE_POSITION_GENERATION_STATUS
			= "UPDATE PowerProfile  " +
			"     SET detail.positionGenerationStatusValue = 'Pending', " +
			"         detail.positionGenerationIdentifier = null " +
			"   WHERE id in (:powerProfileIds) " +
			"     AND detail.positionGenerationStatusValue != 'Pending'";

	private static final String UPDATE_POSITION_GENERATION_STATUS_TO_COMPLETE
			= "UPDATE PowerProfile  " +
			"     SET detail.positionGenerationStatusValue = 'Complete', " +
			"         detail.positionGenerationDateTime = :updateDateTime " +
			"   WHERE id in (:powerProfileIds) " +
			"     AND detail.positionGenerationStatusValue = 'Generating'";


	private static final String UPDATE_POSITION_GENERATION_ASSIGNMENT
			= "UPDATE PowerProfile  " +
			"     SET detail.positionGenerationStatusValue = 'Generating', " +
			"         detail.positionGenerationIdentifier = :identifier " +
			"   WHERE id in (:powerProfileIds) " +
			"     AND detail.positionGenerationStatusValue = 'Pending'";


	@Override
	public void executeUpdateAssignForPositionGeneration(
			List<Integer> powerProfileIds,
			String positionGeneratorId) {

		String[] names = {"identifier","powerProfileIds"};
		if (powerProfileIds.size() < 1000) {
			Object[] parms = {positionGeneratorId, powerProfileIds};
			executeUpdate(
					UPDATE_POSITION_GENERATION_ASSIGNMENT,
					names,
					parms);
		} else {
			SubLister<Integer> subLister = new SubLister<>(powerProfileIds, 1000);
			while (subLister.moreElements()) {
				Object[] parms2 = {positionGeneratorId, subLister.nextList()};
				executeUpdate(
						UPDATE_POSITION_GENERATION_ASSIGNMENT,
						names,
						parms2);
			}
		}

	}


	@Override
	public void executeUpdatePositionGenerationToComplete(
			List<Integer> powerProfileIds,
			LocalDateTime positionGenerationDateTime) {

		String[] names = {"powerProfileIds", "updateDateTime"};
		if (powerProfileIds.size() < 1000) {
			Object[] parms = {powerProfileIds, positionGenerationDateTime};
			executeUpdate(
					UPDATE_POSITION_GENERATION_STATUS_TO_COMPLETE,
					names,
					parms);
		} else {
			SubLister<Integer> subLister = new SubLister<>(powerProfileIds, 1000);
			while (subLister.moreElements()) {
				Object[] parms2 = {subLister.nextList(), positionGenerationDateTime};
				executeUpdate(
						UPDATE_POSITION_GENERATION_STATUS_TO_COMPLETE,
						names,
						parms2);
			}
		}

	}


	@Override
	public void executeUpdateSetPositionGenerationToPending(List<Integer> powerProfileIds) {

		if (powerProfileIds.size() < 1000) {
			executeUpdate(
					UPDATE_POSITION_GENERATION_STATUS,
					"powerProfileIds",
					powerProfileIds);
		} else {
			SubLister<Integer> subLister = new SubLister<>(powerProfileIds, 1000);
			while (subLister.moreElements()) {
				executeUpdate(
						UPDATE_POSITION_GENERATION_STATUS,
						"powerProfileIds",
						subLister.nextList());
			}
		}

	}



	@Override
	public PowerProfile load(EntityId entityId) {
		if (entityId == null)
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());

		if (entityId.isNull())
			return null;

		if (entityId.isInvalid())
			throw new OBRuntimeException(CoreTransactionErrorCode.INVALID_ENTITY_ID.getCode());


		if (entityId.isSet())
			return find(PowerProfile.class, entityId.getId());
		else
			return null;
	}

	@Override
	public List<PowerProfile> getAssignedPowerProfiles(String positionGeneratorId) {
		return executeQuery(
				FETCH_ASSIGNED_POWER_PROFILES,
				"positionGenerationIdentifier",
				positionGeneratorId);
	}

	@Override
	public List<PowerProfile> fetchByIds(QuerySelectedPage querySelectedPage) {
		return List.of();
	}

	@Override
	public List<Integer> findProfileIds(DefinedQuery definedQuery) {
		return List.of();
	}

	@Override
	public List<PowerProfile> findByQuery(DefinedQuery query) {
		return List.of();
	}
}
