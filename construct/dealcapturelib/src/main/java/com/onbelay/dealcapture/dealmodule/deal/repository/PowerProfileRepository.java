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
package com.onbelay.dealcapture.dealmodule.deal.repository;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.dealmodule.deal.model.PowerProfile;

import java.time.LocalDateTime;
import java.util.List;

public interface PowerProfileRepository {
	public static final String BEAN_NAME = "powerProfileRepository";

	public List<PowerProfile> fetchByIds(QuerySelectedPage querySelectedPage);
	
	/**
	 * Fetch a list of deal ids from a defined query.
	 * @param definedQuery
	 * @return
	 */
	public List<Integer> findProfileIds(DefinedQuery definedQuery);

	/**
	 * Find PowerProfiles using a defined query
	 * @param query
	 * @return
	 */
	public List<PowerProfile> findByQuery(DefinedQuery query);


	public PowerProfile load(EntityId entityId);

	public PowerProfile findByName(String name);

	/**
	 * Update power profiles by assigning deals to a position generation identifier.
	 * @param powerProfileIds - list of  ids
	 * @param positionGeneratorId - owning generator process name;
	 */
	public void executeUpdateAssignForPositionGeneration(
			List<Integer> powerProfileIds,
			String positionGeneratorId);

	void executeUpdatePositionGenerationToComplete(
			List<Integer> powerProfileIds,
			LocalDateTime createdDateTime);

	public void executeUpdateSetPositionGenerationToPending(List<Integer> powerProfileIds);

	List<PowerProfile> getAssignedPowerProfiles(String positionGeneratorId);
}
