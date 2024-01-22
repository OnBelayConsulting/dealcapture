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
package com.onbelay.dealcapture.pricing.adapter;

import com.onbelay.core.entity.snapshot.EntityId;
import com.onbelay.core.entity.snapshot.TransactionResult;
import com.onbelay.core.query.enums.ExpressionConnector;
import com.onbelay.core.query.enums.ExpressionOperator;
import com.onbelay.core.query.snapshot.DefinedQuery;
import com.onbelay.core.query.snapshot.DefinedWhereExpression;
import com.onbelay.dealcapture.pricing.model.FxCurve;
import com.onbelay.dealcapture.pricing.model.FxIndex;
import com.onbelay.dealcapture.pricing.model.FxIndexFixture;
import com.onbelay.dealcapture.pricing.repository.FxCurveRepository;
import com.onbelay.dealcapture.pricing.repository.FxIndexRepository;
import com.onbelay.dealcapture.pricing.snapshot.FxCurveSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshot;
import com.onbelay.dealcapture.pricing.snapshot.FxIndexSnapshotCollection;
import com.onbelay.dealcapture.test.DealCaptureAppSpringTestCase;
import com.onbelay.shared.enums.CurrencyCode;
import com.onbelay.shared.enums.FrequencyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@WithMockUser(username="test")
public class FxIndexRestAdapterTest extends DealCaptureAppSpringTestCase {
	private static final Logger logger = LogManager.getLogger(FxIndexRestAdapterTest.class);
	
	@Autowired
	private FxIndexRestAdapter fxIndexRestAdapter;

	@Autowired
	private FxIndexRepository fxIndexRepository;

	@Autowired
	private FxCurveRepository fxCurveRepository;

	private FxIndex cadUsdfxIndex;
	private FxIndex cadEurFxIndex;

	private LocalDate fromMarketDate = LocalDate.of(2023, 1, 1);
	private LocalDate toMarketDate = LocalDate.of(2023, 1, 31);

	@Override
	public void setUp() {
		super.setUp();

		cadUsdfxIndex = FxIndexFixture.createFxIndex(
				FrequencyCode.DAILY,
				CurrencyCode.USD,
				CurrencyCode.CAD);

		FxIndexFixture.generateDailyFxCurves(
				cadUsdfxIndex,
				fromMarketDate,
				toMarketDate,
				LocalDateTime.of(10, 1, 1, 1, 1));

		cadEurFxIndex = FxIndexFixture.createFxIndex(
				FrequencyCode.DAILY,
				CurrencyCode.CAD,
				CurrencyCode.EURO);


		FxIndexFixture.generateDailyFxCurves(
				cadUsdfxIndex,
				fromMarketDate,
				toMarketDate,
				LocalDateTime.of(2023, 10, 1, 0, 0));


		flush();
	}

	@Test
	public void findFxIndices() {
		FxIndexSnapshotCollection collection = fxIndexRestAdapter.find(
				"WHERE name = " + "'" + cadEurFxIndex.getDetail().getName() + "'",
				0,
				100);
		assertEquals(1, collection.getTotalItems());
	}

	@Test
	public void createFxIndex() {
		FxIndexSnapshot snapshot = new FxIndexSnapshot();
		snapshot.getDetail().setToCurrencyCode(CurrencyCode.EURO);
		snapshot.getDetail().setFromCurrencyCode(CurrencyCode.USD);
		snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);

		TransactionResult result = fxIndexRestAdapter.save(snapshot);

		FxIndex index = fxIndexRepository.load(result.getEntityId());
		assertEquals("EURO _ USD:D", index.getDetail().getName());
	}

	@Test
	public void findFxCurves() {
		DefinedQuery definedQuery = new DefinedQuery("FxCurve");
		definedQuery.getWhereClause().addExpression(
				new DefinedWhereExpression(
						"indexName",
						ExpressionOperator.EQUALS,
						cadUsdfxIndex.getDetail().getName()));

		definedQuery.getWhereClause().addConnector(ExpressionConnector.AND);
		definedQuery.getWhereClause().addExpression(
				new DefinedWhereExpression(
						"curveDate",
						ExpressionOperator.GREATER_THAN,
						fromMarketDate));

		String queryText = definedQuery.toString();

		fxIndexRestAdapter.findFxCurves(
				queryText,
				0,
				100);
	}

	@Test
	public void createFxCurves() {
		FxCurveSnapshot snapshot = new FxCurveSnapshot();
		snapshot.getDetail().setCurveValue(BigDecimal.ONE);
		snapshot.getDetail().setCurveDate(LocalDate.of(2022, 1, 1));
		snapshot.getDetail().setObservedDateTime(LocalDateTime.now());
		snapshot.getDetail().setFrequencyCode(FrequencyCode.DAILY);

		TransactionResult result = fxIndexRestAdapter.saveFxCurves(
				cadEurFxIndex.getId(),
				List.of(snapshot));
		flush();
		DefinedQuery definedQuery = new DefinedQuery("FxCurve");
		definedQuery.getWhereClause().addExpression(
				new DefinedWhereExpression("fxIndexId", ExpressionOperator.EQUALS, cadEurFxIndex.getId()));

		List<Integer> ids  = fxCurveRepository.findFxCurveIds(definedQuery);
		assertEquals(1, ids.size());
		FxCurve curve = fxCurveRepository.load(new EntityId(ids.get(0)));
		assertEquals(LocalDate.of(2022, 1, 1), curve.getDetail().getCurveDate());
	}
	

}
