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
import com.onbelay.dealcapture.pricing.snapshot.CurveReport;
import com.onbelay.dealcapture.pricing.snapshot.PriceCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexReport;
import com.onbelay.dealcapture.pricing.snapshot.PriceIndexSnapshot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PriceIndexService {
	public static String BEAN_NAME = "priceIndexService";
	public PriceIndexSnapshot load(EntityId entityId);

	public TransactionResult save(PriceIndexSnapshot snapshot);

	public TransactionResult save(List<PriceIndexSnapshot> snapshots);

	public QuerySelectedPage findPriceIndexIds(DefinedQuery definedQuery);

	public List<PriceIndexSnapshot> findByIds(QuerySelectedPage selectedPage);

	PriceIndexSnapshot findPriceIndexByName(String indexName);

	public List<PriceIndexReport> fetchPriceIndexReports(QuerySelectedPage priceIndices);

	//////////////// Price Curves /////////////////

	public TransactionResult savePrices(
			EntityId priceIndexId,
			List<PriceCurveSnapshot> prices);

	public List<CurveReport> fetchPriceCurveReports(
			QuerySelectedPage priceIndices,
			LocalDate fromCurveDate,
			LocalDate toCurveDate,
			LocalDateTime observedDateTime);

	public QuerySelectedPage findPriceCurveIds(DefinedQuery definedQuery);

	List<PriceCurveSnapshot> fetchPriceCurvesByIds(QuerySelectedPage querySelectedPage);

    List<PriceIndexSnapshot> findActivePriceIndices();

    PriceCurveSnapshot loadPriceCurve(EntityId priceCurveId);
}
