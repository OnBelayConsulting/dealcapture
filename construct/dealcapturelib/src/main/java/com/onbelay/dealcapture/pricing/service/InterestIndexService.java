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
package com.onbelay.dealcapture.pricing.service;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.QuerySelectedPage;
import com.onbelay.dealcapture.busmath.model.InterestRate;
import com.onbelay.dealcapture.pricing.snapshot.InterestCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.InterestIndexSnapshot;

import java.time.LocalDate;
import java.util.List;

public interface InterestIndexService {
	public static String BEAN_NAME = "interestIndexService";
	public InterestIndexSnapshot load(EntityId entityId);

	public TransactionResult save(InterestIndexSnapshot snapshot);

	public TransactionResult save(List<InterestIndexSnapshot> snapshots);

	public QuerySelectedPage findInterestIndexIds(DefinedQuery definedQuery);

	public List<InterestIndexSnapshot> findByIds(QuerySelectedPage selectedPage);

	InterestIndexSnapshot findInterestIndexByName(String indexName);

	InterestRate getCurrentInterestRate(LocalDate currentDate);

	//////////////// Interest Curves /////////////////

	public TransactionResult saveInterestCurves(
			EntityId interestIndexId,
			List<InterestCurveSnapshot> interestCurves);

	public QuerySelectedPage findInterestCurveIds(DefinedQuery definedQuery);

	List<InterestCurveSnapshot> fetchInterestCurvesByIds(QuerySelectedPage querySelectedPage);
}
