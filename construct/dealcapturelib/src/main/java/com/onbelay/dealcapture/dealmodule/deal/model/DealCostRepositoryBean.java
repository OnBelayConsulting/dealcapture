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

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.onbelay.core.entity.repository.BaseRepository;

@Repository (value="dealCostRepository")
@Transactional

public class DealCostRepositoryBean extends BaseRepository<DealCost> {
	public static final String BEAN_NAME = "dealCostRepository";
	public static final String FETCH_DEAL_COSTS = "DealCostRepository.FETCH_DEAL_COSTS";


	public List<DealCost> fetchDealCosts(Integer dealId) {
		
		return (List<DealCost>) executeQuery(
				FETCH_DEAL_COSTS,
				"dealId",
				dealId); 
	}

}
